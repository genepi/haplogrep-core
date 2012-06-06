package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import phylotree.Phylotree;
import search.ClusteredSearchResult;
import search.OverviewTreePath;
import search.SearchResult;
import search.SearchResultTreeNode;
import search.ranking.RankingMethod;
import search.ranking.results.RankedResult;
import dataVisualizers.PhylotreeRenderer;
import exceptions.parse.sample.InvalidBaseException;
import exceptions.parse.sample.InvalidPolymorphismException;
import exceptions.parse.sample.InvalidRangeException;
import exceptions.parse.samplefile.HsdFileException;
import exceptions.parse.samplefile.InvalidColumnCountException;
import exceptions.parse.samplefile.UniqueKeyException;



public class SampleFile {
	Hashtable<String,TestSample> testSamples = new Hashtable<String,TestSample>();
	HashMap<String, List<ClusteredSearchResult>> classificationResults = 
			new HashMap<String, List<ClusteredSearchResult>>();
	public SampleFile(ArrayList<String> sampleLines) throws HsdFileException
	{
		int lineIndex = 1;
		for(String currentLine : sampleLines)
		{
			TestSample newSample;
			try {
				newSample = TestSample.parse(currentLine);
			} catch (HsdFileException e) {
				e.setLineExceptionOccured(lineIndex);
				throw e;
			}
			if(testSamples.containsKey(newSample.getSampleID()))
				try {
					throw new UniqueKeyException();
				} catch (UniqueKeyException e) {
					e.setLineExceptionOccured(lineIndex);
					e.setTestSampleeID(newSample.getSampleID());
					throw e;
				}
			else
			testSamples.put(newSample.getSampleID(), newSample);
			lineIndex++;
		}
	}
	//depends on the read in method
	public SampleFile(String pathToSampleFile,boolean testCase) throws IOException, NumberFormatException, HsdFileException, InvalidBaseException, InvalidRangeException, InvalidColumnCountException 
	{
		BufferedReader sampleFileStream;
		if(testCase){ //for test cases
		File sampleFile = new File(pathToSampleFile);
		System.out.println("%%%% "+pathToSampleFile);
		sampleFileStream = new BufferedReader(new FileReader(sampleFile));
		}
		else{ //"Load Testdata" button
		InputStream testFile = this.getClass().getClassLoader().getResourceAsStream(pathToSampleFile);
		sampleFileStream = new BufferedReader ( new InputStreamReader ( testFile ) );
		}
		String currentLine = sampleFileStream.readLine();
		
		while (currentLine != null) {
			TestSample newSample = TestSample.parse(currentLine);
			testSamples.put(newSample.getSampleID(), newSample);

			currentLine = sampleFileStream.readLine();
		}
		
	}
	

	
//	private  void parseNewTestSample(String line) throws IOException,
//			NumberFormatException, InvalidBaseException, InvalidFormatException, InvalidHsdFileException {
//	
//			String[] tokens = line.split("\t");
//			
//			if(tokens.length != 4)
//				throw new InvalidHsdFileException();
//			
//			String sampleID=tokens[0];
//			SampleRange currentSampleRange = new SampleRange(tokens[1]);
//			String expectedHaplogroup=tokens[3];
//			String polymorphimn=tokens[4];
//			
//			TestSample newTestSample = new TestSample(sampleID, expectedHaplogroup, polymorphimn,currentSampleRange);
//			
//			/*// to ignore lines which start with #
//			if (!line.contains("#")) { 
//				StringTokenizer mainTokenizer = new StringTokenizer(line, "\t");
//				if (mainTokenizer.hasMoreElements()) {
//
//					String sampleID = mainTokenizer.nextToken();
//					Haplogroup haplogroup = new Haplogroup(mainTokenizer.nextToken());
//					
//					//frequency column
//					String frequency= mainTokenizer.nextToken();
//					
//
//					//if (!testSamples.containsKey(f.getName()))
//					//	testSamples.put(f.getName(), new ArraTestSample>());
//
//					ArrayList<String> sample = new ArrayList<String>();
//					while (mainTokenizer.hasMoreElements()) {
//						sample.add(mainTokenizer.nextToken());
//					}
//
//					Sample newSample = new Sample(sample, new SampleRange(range));
//					TestSample newTestSample = new TestSample(sampleID, haplogroup, newSample,frequency);
//					testSamples.put(newTestSample.getSampleID(), newTestSample);
//				}
//			}*/
//		
//	}

	private SampleRange tryDetectRange(String line) {
		// added for files in folder TestSamples. Ignore lines with #, but
		// dynamically read range from files (= #!)
		if (line.contains("#!")) {
			SampleRange range = new SampleRange();
			StringTokenizer rangeTokenizer = new StringTokenizer(line, " ");
			rangeTokenizer.nextToken();
			
			while (rangeTokenizer.hasMoreElements()) {
				String rangeToken = rangeTokenizer.nextToken().trim();
				if (rangeToken.contains("-"))
					range.addCustomRange(Integer.valueOf(rangeToken.substring(0, rangeToken.indexOf("-"))), Integer
							.valueOf(rangeToken.substring(rangeToken.indexOf("-") + 1, rangeToken.length())));
				else
					// range has length 1
					range.addCustomRange(Integer.valueOf(rangeToken), Integer.valueOf(rangeToken));
			}
			return range;
		}
		
		else return null;
	}
	
	
	public TestSample getTestSample(String sampleID)
	{
		return testSamples.get(sampleID);
	}
	
	public ArrayList<TestSample> getTestSamples()
	{
		return new ArrayList<TestSample>(testSamples.values());
	}
	
	public String toString()
	{
		String result = "";
		
		for(TestSample currenTestSample : testSamples.values())
		{
			result += currenTestSample.toString() + System.getProperty("line.separator");
		}
		
		return result;
	}
	
	

	public Element toXMLString() {
		Element root = new Element("catalog");
		
		for (TestSample sample : testSamples.values()) {
			Element newElement = new Element("mtDNA_lines");
			Element newElement1 = new Element("sample_name");
			newElement1.setText(sample.getSampleID().toString());
			newElement.addContent(newElement1);
			newElement1 = new Element("range");
			SampleRange range = sample.getSample().getSampleRanges();
			ArrayList<Integer> startRange = range.getStarts();
			
			ArrayList<Integer> endRange = range.getEnds();
			StringBuffer result = new StringBuffer();
			for(int i=0;i<startRange.size();i++){
				if(startRange.get(i).equals(endRange.get(i))){result.append(startRange.get(i)+"; ");}
				else {result.append(startRange.get(i)+"-"+endRange.get(i)+"; ");}
			}
			newElement1.setText(result.toString());
			newElement.addContent(newElement1);
			//if(sample.getExpectedHaplogroup().toString().equals(""))
			newElement1 = new Element("haplogroup");
			
			//if no haplogroup is predefinied, than set our result to predefinied
			if(sample.getExpectedHaplogroup().toString().equals("")&&
					sample.getDetectedHaplogroup() != null ){ 
			sample.setExpectedHaplogroup(sample.getDetectedHaplogroup());
			sample.setState("top rank");
			}
			if(sample.getDetectedHaplogroup() != null 
					&& !sample.getDetectedHaplogroup().equals(sample.getExpectedHaplogroup())
					)
				newElement1.setText(sample.getExpectedHaplogroup().toString() 
						+ " (" +sample.getDetectedHaplogroup() .toString() + ")");
			else
			{
				newElement1.setText(sample.getExpectedHaplogroup().toString());	}
			newElement.addContent(newElement1);
			
			newElement1 = new Element ("status");
			newElement1.setText(String.valueOf(sample.getState()));
			newElement.addContent(newElement1);
			
			newElement1 = new Element ("hit");
			newElement1.setText(String.valueOf(sample.getResultQuality()));
			newElement.addContent(newElement1);
			
			//parse Polymorphisms
			ArrayList<Polymorphism> t = sample.getSample().getPolymorphismn();
			String polys = "";
			for (Polymorphism t1 : t)
				polys += t1.toString()+" ";
			newElement1 = new Element("polys");
			newElement.addContent(newElement1);
			newElement1.setText(polys);
			root.addContent(newElement);
		}
		
		return root;
		
	}
	
	public List<ClusteredSearchResult> getClassificationResults(String sampleID) {
		return classificationResults.get(sampleID);
	}
	public void updateClassificationResults(Phylotree phylotree, RankingMethod rankingMethod) throws NumberFormatException, InvalidPolymorphismException, JDOMException, IOException {
		for(TestSample currenTestSample : testSamples.values())
		{
			List<RankedResult> results = phylotree.search(currenTestSample,rankingMethod.clone());
			classificationResults.put(currenTestSample.getSampleID(), ClusteredSearchResult.createClusteredSearchResult(results, currenTestSample.getExpectedHaplogroup()));
		}

	}
	

private Element combinePathsToXMLTree( HashMap<String, List<ClusteredSearchResult>> classificationResults2,boolean includeHotspots, boolean includeMissingPolys) throws Exception {
	
	if(classificationResults.size() < 2)
		throw new Exception("There must be more than one classified sample to create a tree. Please start classification process first!");
	
	//Element root = new Element("root");
	//root.setAttribute("name","rCRS");
	//ClusteredSearchResult firstSampleSearchResult = classificationResults.values().
	 //Element currentPath = currentResultList.getPhyloTreePath(0).toXML(currentSampleID);
	 
	Element combinedResultTree = null;
	for(String currentSampleID : classificationResults.keySet())
	{
		//ClusteredSearchResult currentResultList = classificationResults.get(currentSampleID).get(0);
		//combinedResultTree = currentResultList.getPhyloTreePath(0).toXML(currentSampleID);
		if(combinedResultTree == null)
		{
			Haplogroup assignedHaplogroup = getTestSample(currentSampleID).getDetectedHaplogroup();
			
			List<ClusteredSearchResult> allResults = classificationResults.get(currentSampleID);
			 SearchResult resultToExport = ClusteredSearchResult.getSearchResultByHaplogroup(allResults, assignedHaplogroup);
			
			//ClusteredSearchResult firstResult = classificationResults.get(currentSampleID).get(0);
			combinedResultTree = resultToExport.getDetailedResult().getPhyloTreePathXML(includeMissingPolys);
	
			OverviewTreePath op = new OverviewTreePath(combinedResultTree,currentSampleID,resultToExport.getDetailedResult().getUnusedPolysXML(includeHotspots));
			combinedResultTree = op.toXML();
			
		}	
		else{
			Haplogroup assignedHaplogroup = getTestSample(currentSampleID).getDetectedHaplogroup();
			
			List<ClusteredSearchResult> allResults = classificationResults.get(currentSampleID);
			 SearchResult resultToExport = ClusteredSearchResult.getSearchResultByHaplogroup(allResults, assignedHaplogroup);
		
			 
			Element additionalPath = resultToExport.getDetailedResult().getPhyloTreePathXML(includeMissingPolys);		
			OverviewTreePath op = new OverviewTreePath(additionalPath,currentSampleID,resultToExport.getDetailedResult().getUnusedPolysXML(includeHotspots));	
			combinePathRec(combinedResultTree, op.toXML());	
		}
		
	}	
	
	return combinedResultTree;
}


public JSONArray getClassificationResultJson(String sampleID){
	JSONArray resultArray = null;
	resultArray = new JSONArray();
		

		if(!classificationResults.containsKey(sampleID))
			return resultArray;
			
		for(ClusteredSearchResult currentResult : classificationResults.get(sampleID))
		{
			
			try {
				JSONObject resultObject = currentResult.toJson();
				resultArray.put(resultObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		}
		getTestSample(sampleID).getExpectedHaplogroup();
		//classificationResults.g
		
		return resultArray;
	}
	
	

	

	public Element getDetailsXML(String sampleID,String haplogroup) {
		for(ClusteredSearchResult currentResult : classificationResults.get(sampleID))
		{
			Element result = currentResult.getDetailsXML(haplogroup);
			
			if(result != null)
				return result;
			
		}
		return null;
	}

	public Element getUnusedPolys(String sampleID, String haplogroup) {
		for(ClusteredSearchResult currentResult : classificationResults.get(sampleID))
		{
			Element result = currentResult.getUnusedPolysXML(haplogroup);
			
			if(result != null)
				return result;
			
		}
		return null;
	}
	
	public void clearClassificationResults() {
		classificationResults.clear();
		
	}
	
	public Element getNotInRangePolys(String sampleID, String haplogroup) {
		for(ClusteredSearchResult currentResult : classificationResults.get(sampleID))
		{
			Element result = currentResult.getNotInRangePolysXML(haplogroup);
			
			if(result != null)
				return result;
			
		}
		return null;
	}
	
	public File getOverviewBestResultsAllSamples(String sessionID, String format, int res, boolean includeHotspots, boolean includeMissingPolys) throws Exception
	{
		
		
		
		/*
		for(String currentSampleID : classificationResults.keySet())
		{
			List<ClusteredSearchResult> currentResultList = classificationResults.get(currentSampleID);
			
			if(currentResultList.size() > 0)
			{
				
				
				Element newExpectedPoly = new Element("expected");				
				newExpectedPoly.setText(current.toString());
				result.addContent(newExpectedPoly);
				
				Element newCorrectPoly = new Element("correct");				
				newCorrectPoly.setText("no");
				result.addContent(newCorrectPoly);
				
				results.addContent(result);
				
				combinePathsToTree
				
				SearchResult topResult = currentResultList.get(0).getCluster().get(0);
				PhyloTreePath newPath = topResult.getPhyloTreePath();
				paths.add(newPath);	
			}
		}
		
		
		try {
			JSONObject result =  combinePathsToTree(paths,null);
			
			/*FileWriter fstream;
			try {
				fstream = new FileWriter("testdataVis.txt");
				 BufferedWriter out = new BufferedWriter(fstream);
			        out.write(result.toString());
			        out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
	       
			
			//System.out.println("All Paths for overview:" + result.toString());
			Element resultTree = combinePathsToXMLTree(classificationResults,includeHotspots,includeMissingPolys);
			Document d =new Document(resultTree);
			saveXMLToDisc(d);
		
			SAXBuilder parser = new SAXBuilder();
			
			
			File image = null;
			PhylotreeRenderer renderer = new PhylotreeRenderer(d);
			URL url = this.getClass().getClassLoader().getResource("haplogrepGray.png");

			renderer.setWatermark(url);
			renderer.setDpi(res);
			 //image = renderer.createImage("Overview" + this.id + ".png");
				image = renderer.createImage(format,"Overview"  + sessionID + "." + format ,includeHotspots);
			/*FileReader r;
			BufferedImage image = null;
			try {
				r = new FileReader("testAll2.xml");
				BufferedReader b = new BufferedReader(r);
				String s = b.readLine();
			
				
				
				
				SAXBuilder parser = new SAXBuilder();
				Document d =parser.build(b);
				
				PhylotreeRenderer renderer = new PhylotreeRenderer(d);
				 image = renderer.createImage();
				r.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			
			return image;
		/*} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;*/
	}
	
	private boolean combinePathRec(Element currentTreeRootNode, Element currentPathNode)
	{
		/*
		 * ArrayList<Element> newPolys = new ArrayList<Element>();
		for(Element currentPoly  : (List<Element>)currentPathNode.getChildren("Poly"))
		{
			boolean found = false;
			for(Element currentPolyTree : (List<Element>)currentTreeRootNode.getChildren("Poly")){
			if(currentPoly.getText().equals(currentPolyTree.getText())){
				found = true;
				
				
			
				}
			}
			if(!found){
			Element newPoly =  new Element("Poly");
			newPoly.setText(currentPoly.getText());
			newPolys.add(newPoly);
			System.out.print(currentPoly.getText() + " ");}
		}
		System.out.println();
		for(Element c : newPolys)
			currentTreeRootNode.addContent(c);
		
		newPolys.clear();
		 * */
		//The current result tree does NOT contain the current subpath. So we add it to the tree 
		//and are finished
		if(currentTreeRootNode.getChildren().size() == 0){
			currentTreeRootNode.addContent(currentPathNode.cloneContent());
			return true;
		}
			
		
		
		if(currentTreeRootNode.getAttributeValue("type").equals("Haplogroup") && currentTreeRootNode.getAttributeValue("name").equals(currentPathNode.getAttributeValue("name")))
		{
			ArrayList<Element> newPolys = new ArrayList<Element>();
			for(Element currentPoly  : (List<Element>)currentPathNode.getChildren("Poly"))
			{
				boolean found = false;
				for(Element currentPolyTree : (List<Element>)currentTreeRootNode.getChildren("Poly")){
				if(currentPoly.getText().equals(currentPolyTree.getText())){
					found = true;
					
					
				
					}
				}
				if(!found){
				Element newPoly =  new Element("Poly");
				newPoly.setText(currentPoly.getText());
				newPolys.add(newPoly);
				System.out.print(currentPoly.getText() + " ");}
			}
			System.out.println();
			for(Element c : newPolys)
				currentTreeRootNode.addContent(c);
		
			newPolys.clear();
			
			
			
			
			//Check if we are at the end of the subpath. If true then our result tree already contains
			//the subpath completely and we leave the function immediately
			if(currentPathNode.getChildren("TreeNode").size() == 0){
				return true;
			}
			
			
			//boolean foundInsertPos = false;
			//The tree contains our current subpath so we step one node ahead and make a RECURSIVE
			//call to this function for each child element
			for(Element currentTreeChild : (List<Element>)currentTreeRootNode.getChildren("TreeNode")){
				
				if(combinePathRec(currentTreeChild,currentPathNode.getChild("TreeNode")))
					return true;
			}
			
			//if(!foundInsertPos){
				currentPathNode.removeChildren("Poly");
				currentTreeRootNode.addContent(currentPathNode.cloneContent());
			//}
			
			return true;
		}
		
		else{
			//currentTreeRootNode.addContent(currentPathNode.cloneContent());
			return false;
		}

	}
	
	private void saveXMLToDisc(Document combinePathsToXMLTree) {
		SAXBuilder builder = new SAXBuilder();
		Document resultTree;
		try {
			//resultTree = builder.build();
			//Document doc = new Document(combinePathsToXMLTree);
			Format fmt = Format.getPrettyFormat();
			XMLOutputter outp = new XMLOutputter(fmt);
			File newoutputFile = new File("testOutputXML.xml");
			outp.output(combinePathsToXMLTree, new FileOutputStream(newoutputFile));
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
		}
	public JSONObject getSelectetHaplogroupSubtree(String sampleID,ArrayList<String> selectedHaplogroups) 
	{
		ArrayList<ArrayList<SearchResultTreeNode>> paths = new ArrayList<ArrayList<SearchResultTreeNode>>(); 
		for(ClusteredSearchResult currentResult : classificationResults.get(sampleID))
		{
			
			
			
			for(SearchResult currentSearchResult : currentResult.getCluster()){
				
				if(selectedHaplogroups.contains(currentSearchResult.getHaplogroup().toString())){
					for(SearchResult currentSearchResult2 : currentResult.getCluster()){
						ArrayList<SearchResultTreeNode> newPath = currentSearchResult2.getDetailedResult().getPhyloTreePath();
				paths.add(newPath);}
				}
			}
				// newPath = currentResult.getPhyloTreePath(1);
				//paths.add(newPath);
			//if(newPath != null)
				
			//}
		}
		
		try {
			JSONObject result =  combinePathsToTree(paths,classificationResults.get(sampleID).get(0));
			
						return result;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}
	

private JSONObject combinePathsToTree(ArrayList<ArrayList<SearchResultTreeNode>> paths, ClusteredSearchResult list) throws JSONException {
		
		JSONObject currentNode = new JSONObject();
		JSONObject result = currentNode;
		currentNode.put("id", "root");
		
		JSONObject dataNode = new JSONObject();	
		dataNode.put("type", "hg");
		
		currentNode.put("data",dataNode);
		currentNode.put("name", "sample");
		//currentNode.append("children", new JSONArray());
		
		//PhyloTreePath longestPath = paths.get(0);
		int ipath = 0;
		boolean step = false;
		for(ArrayList<SearchResultTreeNode> currentPath : paths)
		{
			currentNode = result;
			if(currentNode.has("children"))
			{
				JSONArray currentChildren = currentNode.getJSONArray("children");	
				ipath = 0;
				int i = 0;
				//For each child
				while( i< currentChildren.length())
				{
					if(ipath < currentPath.size()){
						JSONObject childNode = currentChildren.getJSONObject(i);
						
						if(childNode.get("name").toString().equals(currentPath.get(ipath).getHaplogroup() + "_Polys"))
						{
							currentNode = childNode;
									currentChildren = currentNode.getJSONArray("children");
									i= 0;
						}
							
						else 
						{
							if(childNode.get("name").equals(currentPath.get(ipath).getHaplogroup()))
								{
								System.out.print(currentPath.get(ipath).getHaplogroup() + " ");	
								//step = true;
									currentNode = childNode;
									currentChildren = currentNode.getJSONArray("children");
									i= 0;
									ipath++;
									
									
								}
							else
							{
							i++;
							}
						}
					}
					//Path is shorter
					else {
						break;
					}
				}	
			}
						
				
					
					
				
					
					for(int i1 = ipath; i1< currentPath.size();i1++)
					{
						 dataNode = new JSONObject();	
						dataNode.put("type", "poly");
						for(Polymorphism currentPoly : currentPath.get(i1).getExpectedPolys())
						{
							JSONObject poly = new JSONObject();	
							poly.put("name", currentPoly);
							
							
							
							if(currentPath.get(i1).getFoundPolys().contains(currentPoly))
							{
								poly.put("state", "found");		
							}
							
							else{
								if(list != null){
								if(list.getCluster().get(0).getCorrectedBackmutations().contains(currentPoly))
									poly.put("state", "corrected");
									
								else		
									poly.put("state", "notfound");
								}
							}
							
							dataNode.append("polys", poly);	
							
						}
						
						System.out.print("Neu " +  currentPath.get(i1).getHaplogroup() + " ");	
						
						for(Polymorphism currentPoly : currentPath.get(i1).getNotInRangePolys())
						{
							JSONObject poly = new JSONObject();	
							poly.put("name", currentPoly);
							poly.put("state", "notInRange");
							
							dataNode.append("polys", poly);
						}
						
						int numAllPolys =  currentPath.get(i1).getExpectedPolys().size() + 
						currentPath.get(i1).getNotInRangePolys().size();
						
						dataNode.put("$height", numAllPolys * 13 + 10);
						dataNode.put("$width", 50);
						
						JSONObject newPolyNode = new JSONObject();	
						newPolyNode.put("id", currentPath.get(i1).getHaplogroup() + "_Polys");
						newPolyNode.put("data", dataNode);
						newPolyNode.put("name", currentPath.get(i1).getHaplogroup() + "_Polys");
						
						dataNode = new JSONObject();	
						dataNode.put("type", "hg");
						//dataNode.put("$width", longestPath.getNodes().get(i).getHaplogroup().toString().length() * 10 + 10);
						
						JSONObject newNode = new JSONObject();	
						newNode.put("id", currentPath.get(i1).getHaplogroup());
						newNode.put("data", dataNode);
						newNode.put("name", currentPath.get(i1).getHaplogroup());
						newNode.put("children", new JSONArray());
						//dataNode.put("$width", longestPath.getNodes().get(i).getHaplogroup().toString().length() * 5 + 10);
						//dataNode.put("$height", longestPath.getNodes().get(i).getHaplogroup().toString().length() * 5 + 10);
						//dataNode.put("$dim", (longestPath.getNodes().get(i).getHaplogroup().toString().length() * 5 + 10) / 2);
						
						newPolyNode.append("children", newNode);
						
						currentNode.append("children", newPolyNode);
						currentNode = newNode;
					}	
					
				
					System.out.println();
				
			//}
		}
		
		
		
		
		
		
		
			//if(!step)
			//{
				
			//}
		//}
		
		//currentNode.put("children",new JSONArray());
		/*
		InputStream phyloFile = this.getClass().getClassLoader().getResourceAsStream("phylotree8.xml");
		try {
			currentNode.put("children", JsonConverter.generateJson(phyloFile, haplogroup ,2).getJSONArray("children"));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		
		return result;
			
			
//				for(int i = 0;  i < currentPath.getNodes().size();i++){
//				
//			}
//			
//			if(currentPath != longestPath && currentPath.getNodes().get(i).getHaplogroup()
//					.equals(longestPath.getNodes().get(i).getHaplogroup())){
//				
//			}
//			
//			else
//			{
//				
//			}
//		}
//		
//		
//		for(PhyloTreePath currentPath : paths){
//			if(longestPath.getNodes().size() < currentPath.getNodes().size())
//			{
//				longestPath = currentPath;
//			}
//		}
//		
//		for(int i = 0;  i < longestPath.getNodes().size();i++){
//			for(PhyloTreePath currentPath :paths)
//			{
//				if(currentPath != longestPath && currentPath.getNodes().get(i).getHaplogroup()
//						.equals(longestPath.getNodes().get(i).getHaplogroup())){
//					JSONObject newNode = new JSONObject();	
//					newNode.put("id", "node" + i);
//					newNode.put("name", longestPath.getNodes().get(i).getHaplogroup());
//					tree.append("", newNode);
//				}
//				
//				else
//				{
//					
//				}
//			}
//		}
	}
}
