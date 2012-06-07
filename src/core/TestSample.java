package core;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.jdom.JDOMException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import phylotree.Phylotree;

import qualityAssurance.Cerberus;
import search.ClusteredSearchResult;
import search.SearchResult;
import search.SearchResultTreeNode;
import search.ranking.RankingMethod;
import search.ranking.results.RankedResult;
import exceptions.parse.sample.HsdFileSampleParseException;
import exceptions.parse.sample.InvalidPolymorphismException;
import exceptions.parse.sample.InvalidRangeException;
import exceptions.parse.samplefile.HsdFileException;
import exceptions.parse.samplefile.InvalidColumnCountException;

public class TestSample implements Comparable<TestSample>{
	
	ArrayList<RankedResult> classificationResults = new ArrayList<RankedResult>();
	ClusteredSearchResult clusteredResults = new ClusteredSearchResult(classificationResults);
	
	private String testSampleID = "Unknown";
	private Haplogroup expectedHaplogroup;
	private Haplogroup detectedHaplogroup;
	private Sample sample;
	
	
	private TreeSet<SearchResult> allSearchResults;
	private Cerberus cerberus = null;
	
	private String state="n/a";
	private double resultQuality=0;

	public TestSample(){
		
	}
	
	public TestSample (String sampleID, Haplogroup predefiniedHaplogroup,Sample sample, String state) 
	{
		this.testSampleID = sampleID;
		this.expectedHaplogroup = predefiniedHaplogroup;
		this.sample = sample;
		this.state=state;
	}

	/**
	 * Parses a new Test sample object from an input string
	 * @param inputString The string to parse
	 * @return The parsed string as new TestSample object
	 * @throws InvalidRangeException
	 * @throws InvalidColumnCountException
	 * @throws HsdFileSampleParseException
	 */
	public static TestSample parse(String inputString) throws HsdFileException {
		TestSample parsedSample = new TestSample();
		SampleRange sampleRange = null;
		try {
			//Split the input string in separate column strings 
			String[] columns = inputString.split("\t");

			//Check of number of columns are correct
			if (columns.length < 4)
				throw new InvalidColumnCountException(columns.length);

			//Parse the test sample id
			parsedSample.testSampleID = columns[0].trim();

			//Parse range
			columns[1] = columns[1].replaceAll("\"", "");
			sampleRange = new SampleRange(columns[1]);

			//Parse expected haplogroup
			if (columns[2].equals("?") || columns[2].equals("SEQ"))
				parsedSample.expectedHaplogroup = new Haplogroup("");

			else
				parsedSample.expectedHaplogroup = new Haplogroup(columns[2]);

			// Parse the sample and all its polymprhisms
			StringBuffer sampleString = new StringBuffer();
			for (int i = 3; i < columns.length; i++) {
				sampleString.append(columns[i] + " ");
			}
			parsedSample.sample = new Sample(sampleString.toString(),sampleRange, 0);
		} 
		
		//Something went wrong during the parse process. Throw exception.
		 catch (InvalidPolymorphismException e) {
			HsdFileSampleParseException ex = new HsdFileSampleParseException(e.getMessage());
			ex.setTestSampleID(parsedSample.testSampleID);
			throw ex;
		}

		return parsedSample;
	}

	public Haplogroup getExpectedHaplogroup() {	
		return expectedHaplogroup;
	}

	public ArrayList<Polymorphism> getPolymorphismn() {
		return sample.sample;
	}

	
	
	public Sample getSample() {
		return sample;
	}

	public Haplogroup getDetectedHaplogroup() {
		return detectedHaplogroup;
	}

	public void setDetectedHaplogroup(Haplogroup recognizedHaplogroup) {
		this.detectedHaplogroup = recognizedHaplogroup;
	}

	public void setExpectedHaplogroup(Haplogroup predefiniedHaplogroup) {
		this.expectedHaplogroup = predefiniedHaplogroup;
	}

	public String getSampleID() {
		return testSampleID;
	}

	public String toString()
	{
		String result = testSampleID + "\t" + expectedHaplogroup + "\t";
		
		for(Polymorphism currentPoly : sample.sample)
		{
			result += currentPoly.toString() + " ";
		}
		
		return result;	
	}

	
	//TODO Consider removing state and use new warning/error system instead
	public String getState() {
		return state;
	}

	public void setState(String status) {
		state = status;
	}
	

	//??
	public void setResultQuality(double myDec) {
		this.resultQuality = myDec;
	}
//??
	public double getResultQuality() {
		return resultQuality;
	}

	

	@Override
	public int compareTo(TestSample o) {
	
		 if(this.getSampleID().compareTo(o.getSampleID())<0)
			   return -1;
		 if (this.getSampleID().compareTo(o.getSampleID())>0)	
			  return 1;
		 else
			 return 0;
	}

//	public void addRecommendedHaplogroups(Haplogroup hg, double rank) {
//		setDetectedHaplogroup(hg);
//
//		double firstRank = (rank);
//		BigDecimal myDec = new BigDecimal(firstRank);
//		myDec = myDec.setScale(1, BigDecimal.ROUND_HALF_UP);
//		setResultQuality(myDec.doubleValue());
//
//		// set status for colors
//		if (getExpectedHaplogroup().equals(getDetectedHaplogroup()))
//			setState("identical");
//		else if (getExpectedHaplogroup().isSuperHaplogroup(getDetectedHaplogroup()) || getDetectedHaplogroup().isSuperHaplogroup(getExpectedHaplogroup()))
//			setState("similar");
//		else
//			setState("mismatch");
//
//	}
	
	void addNewSearchResult(SearchResult newResult){
		allSearchResults.add(newResult);
	}
	
	SearchResult getSearchResultByHaplogroup(Haplogroup assignedHaplogroup) {
		for(RankedResult currentResult : classificationResults){
			if(currentResult.getHaplogroup().equals(assignedHaplogroup))
				return currentResult.getPhyloSearchData();
		}
		return null;
	}

	public RankedResult getResult(Haplogroup haplogroup) {
		for(RankedResult currentResult: classificationResults){
			if(currentResult.getHaplogroup().equals(haplogroup))
				return currentResult;
		}
		return null;
	}

	public List<RankedResult> getResults() {
		
		return classificationResults;
	}
	
	public JSONObject getSelectetHaplogroupSubtree( ArrayList<String> selectedHaplogroups) {
		ArrayList<RankedResult> selectedResults = new ArrayList<RankedResult>();
		
		for(String currentHg : selectedHaplogroups){
			Haplogroup selectedHaplogroup = new Haplogroup(currentHg);
			ArrayList<RankedResult> currentResults  = clusteredResults.getCluster(selectedHaplogroup);
			
			if(currentResults != null)
				selectedResults.addAll(currentResults);
			
			else{
				selectedResults.add(getResult(selectedHaplogroup));
			}
		}
		
		
		ArrayList<ArrayList<SearchResultTreeNode>> paths = new ArrayList<ArrayList<SearchResultTreeNode>>();
		for (RankedResult currentResult : selectedResults) {
//				if (selectedHaplogroups.contains(currentResult.getHaplogroup().toString())) {
						ArrayList<SearchResultTreeNode> newPath = currentResult.getPhyloSearchData().getDetailedResult().getPhyloTreePath();
						paths.add(newPath);
					}
//				}
			
			// newPath = currentResult.getPhyloTreePath(1);
			// paths.add(newPath);
			// if(newPath != null)

			// }

		try {
			JSONObject result = combinePathsToTree(paths, classificationResults.get(0));

			return result;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private JSONObject combinePathsToTree(ArrayList<ArrayList<SearchResultTreeNode>> paths, RankedResult list) throws JSONException {

		JSONObject currentNode = new JSONObject();
		JSONObject result = currentNode;
		currentNode.put("id", "root");

		JSONObject dataNode = new JSONObject();
		dataNode.put("type", "hg");

		currentNode.put("data", dataNode);
		currentNode.put("name", "sample");
		// currentNode.append("children", new JSONArray());

		// PhyloTreePath longestPath = paths.get(0);
		int ipath = 0;
		boolean step = false;
		for (ArrayList<SearchResultTreeNode> currentPath : paths) {
			currentNode = result;
			if (currentNode.has("children")) {
				JSONArray currentChildren = currentNode.getJSONArray("children");
				ipath = 0;
				int i = 0;
				// For each child
				while (i < currentChildren.length()) {
					if (ipath < currentPath.size()) {
						JSONObject childNode = currentChildren.getJSONObject(i);

						if (childNode.get("name").toString().equals(currentPath.get(ipath).getHaplogroup() + "_Polys")) {
							currentNode = childNode;
							currentChildren = currentNode.getJSONArray("children");
							i = 0;
						}

						else {
							if (childNode.get("name").equals(currentPath.get(ipath).getHaplogroup())) {
								System.out.print(currentPath.get(ipath).getHaplogroup() + " ");
								// step = true;
								currentNode = childNode;
								currentChildren = currentNode.getJSONArray("children");
								i = 0;
								ipath++;

							} else {
								i++;
							}
						}
					}
					// Path is shorter
					else {
						break;
					}
				}
			}

			for (int i1 = ipath; i1 < currentPath.size(); i1++) {
				dataNode = new JSONObject();
				dataNode.put("type", "poly");
				for (Polymorphism currentPoly : currentPath.get(i1).getExpectedPolys()) {
					JSONObject poly = new JSONObject();
					poly.put("name", currentPoly);

					if (currentPath.get(i1).getFoundPolys().contains(currentPoly)) {
						poly.put("state", "found");
					}

					else {
						if (list != null) {
							if (list.getPhyloSearchData().getDetailedResult().getCorrectedBackmutations().contains(currentPoly))
								poly.put("state", "corrected");

							else
								poly.put("state", "notfound");
						}
					}

					dataNode.append("polys", poly);

				}

				System.out.print("Neu " + currentPath.get(i1).getHaplogroup() + " ");

				for (Polymorphism currentPoly : currentPath.get(i1).getNotInRangePolys()) {
					JSONObject poly = new JSONObject();
					poly.put("name", currentPoly);
					poly.put("state", "notInRange");

					dataNode.append("polys", poly);
				}

				int numAllPolys = currentPath.get(i1).getExpectedPolys().size() + currentPath.get(i1).getNotInRangePolys().size();

				dataNode.put("$height", numAllPolys * 13 + 10);
				dataNode.put("$width", 50);

				JSONObject newPolyNode = new JSONObject();
				newPolyNode.put("id", currentPath.get(i1).getHaplogroup() + "_Polys");
				newPolyNode.put("data", dataNode);
				newPolyNode.put("name", currentPath.get(i1).getHaplogroup() + "_Polys");

				dataNode = new JSONObject();
				dataNode.put("type", "hg");
				// dataNode.put("$width",
				// longestPath.getNodes().get(i).getHaplogroup().toString().length()
				// * 10 + 10);

				JSONObject newNode = new JSONObject();
				newNode.put("id", currentPath.get(i1).getHaplogroup());
				newNode.put("data", dataNode);
				newNode.put("name", currentPath.get(i1).getHaplogroup());
				newNode.put("children", new JSONArray());
				// dataNode.put("$width",
				// longestPath.getNodes().get(i).getHaplogroup().toString().length()
				// * 5 + 10);
				// dataNode.put("$height",
				// longestPath.getNodes().get(i).getHaplogroup().toString().length()
				// * 5 + 10);
				// dataNode.put("$dim",
				// (longestPath.getNodes().get(i).getHaplogroup().toString().length()
				// * 5 + 10) / 2);

				newPolyNode.append("children", newNode);

				currentNode.append("children", newPolyNode);
				currentNode = newNode;
			}

			System.out.println();

			// }
		}

		// if(!step)
		// {

		// }
		// }

		// currentNode.put("children",new JSONArray());
		/*
		 * InputStream phyloFile =
		 * this.getClass().getClassLoader().getResourceAsStream
		 * ("phylotree8.xml"); try { currentNode.put("children",
		 * JsonConverter.generateJson(phyloFile, haplogroup
		 * ,2).getJSONArray("children")); } catch (JDOMException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch (IOException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 */

		return result;

		// for(int i = 0; i < currentPath.getNodes().size();i++){
		//
		// }
		//
		// if(currentPath != longestPath &&
		// currentPath.getNodes().get(i).getHaplogroup()
		// .equals(longestPath.getNodes().get(i).getHaplogroup())){
		//
		// }
		//
		// else
		// {
		//
		// }
		// }
		//
		//
		// for(PhyloTreePath currentPath : paths){
		// if(longestPath.getNodes().size() < currentPath.getNodes().size())
		// {
		// longestPath = currentPath;
		// }
		// }
		//
		// for(int i = 0; i < longestPath.getNodes().size();i++){
		// for(PhyloTreePath currentPath :paths)
		// {
		// if(currentPath != longestPath &&
		// currentPath.getNodes().get(i).getHaplogroup()
		// .equals(longestPath.getNodes().get(i).getHaplogroup())){
		// JSONObject newNode = new JSONObject();
		// newNode.put("id", "node" + i);
		// newNode.put("name", longestPath.getNodes().get(i).getHaplogroup());
		// tree.append("", newNode);
		// }
		//
		// else
		// {
		//
		// }
		// }
		// }
	}

	public void clearClassificationResults() {
		classificationResults.clear();
		clusteredResults = null;
	}

	public void updateClassificationResults(Phylotree phyloTreeToUse,RankingMethod rankingMethod) throws NumberFormatException, InvalidPolymorphismException, JDOMException, IOException {
		List<RankedResult> results = phyloTreeToUse.search(this, rankingMethod.clone());
		classificationResults = (ArrayList<RankedResult>) results;
		clusteredResults = new ClusteredSearchResult(results);
		
	}
	
	public JSONArray getClassificationResultJson() {
//		JSONArray resultArray = null;
//		resultArray = new JSONArray();

//
//		for (ClusteredSearchResult currentResult : classificationResults.get(sampleID)) {
//
//			try {
//				JSONObject resultObject = currentResult.toJson();
//				resultArray.put(resultObject);
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//		getTestSample(sampleID).getExpectedHaplogroup();
		// classificationResults.g

		return clusteredResults.getClusterAsJson();
	}
}
