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
import java.util.Hashtable;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import phylotree.PhyloTreeNode;
import phylotree.Phylotree;
import search.SearchResult;
import search.SearchResultTreeNode;
import search.ranking.RankingMethod;
import search.ranking.results.RankedResult;
import dataVisualizers.OverviewTree;
import dataVisualizers.PhylotreeRenderer;
import exceptions.parse.HsdFileException;
import exceptions.parse.samplefile.UniqueSampleIDException;

/**
 * Represents the entire file of test sample. Used as main object in haplogrep.
 * 
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 * 
 */
public class SampleFile {
	Hashtable<String, TestSample> testSamples = new Hashtable<String, TestSample>();

	/**
	 * Main constructor of SampleFile class. Creates a new test sample instance. 
	 * @param sampleLines An array of strings representing each line of the hsd file
	 * @throws HsdFileException thrown if the hsd file cannot be parsed correctly 
	 */
	public SampleFile(ArrayList<String> sampleLines) throws HsdFileException {
		int lineIndex = 1;
		for (String currentLine : sampleLines) {
			TestSample newSample;
			try {
				newSample = TestSample.parse(currentLine);
			} catch (HsdFileException e) {
				e.setLineExceptionOccured(lineIndex);
				throw e;
			}
			if (testSamples.containsKey(newSample.getSampleID()))
				try {
					throw new UniqueSampleIDException();
				} catch (UniqueSampleIDException e) {
					e.setLineExceptionOccured(lineIndex);
					e.setTestSampleeID(newSample.getSampleID());
					throw e;
				}
			else
				testSamples.put(newSample.getSampleID(), newSample);
			
			lineIndex++;
		}
	}


	/**
	 * Creates a new instance of a disk based hsd file
	 * @param pathToSampleFile The path to the hsd file
	 * @param testCase	
	 * @throws HsdFileException
	 * @throws IOException
	 */
	//TODO:Try to get rid of the ugly boolean testCase parameter
	public SampleFile(String pathToSampleFile, boolean testCase) throws HsdFileException, IOException
		{
		BufferedReader sampleFileStream;
		if (testCase) { // for test cases
			File sampleFile = new File(pathToSampleFile);
			System.out.println("%%%% " + pathToSampleFile);
			sampleFileStream = new BufferedReader(new FileReader(sampleFile));
		} else { // "Load Testdata" button
			InputStream testFile = this.getClass().getClassLoader().getResourceAsStream(pathToSampleFile);
			sampleFileStream = new BufferedReader(new InputStreamReader(testFile));
		}
		String currentLine = sampleFileStream.readLine();

		while (currentLine != null) {
			TestSample newSample = TestSample.parse(currentLine);
			testSamples.put(newSample.getSampleID(), newSample);

			currentLine = sampleFileStream.readLine();
		}
	}

	/**
	 * Returns a test sample.
	 * @param sampleID The unique sampleID of the sample
	 * @return
	 */
	public TestSample getTestSample(String sampleID) {
		return testSamples.get(sampleID);
	}

	/**
	 * Returns all test sample
	 * @return The array of test samples
	 */
	public ArrayList<TestSample> getTestSamples() {
		return new ArrayList<TestSample>(testSamples.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "";

		for (TestSample currenTestSample : testSamples.values()) {
			result += currenTestSample.toString() + System.getProperty("line.separator");
		}

		return result;
	}

	/**
	 * Converts all data of test samples in a xml file. Used to display grid data on the web gui.
	 * @return The root element of the xml file.
	 */
	public Element toXMLString() {
		Element root = new Element("catalog");

		for (TestSample sample : testSamples.values()) {
			//Create new sample row
			Element sampleRowElement = new Element("mtDNA_lines");
			
			//Sample Name (=ID)
			Element newElement = new Element("sample_name");
			newElement.setText(sample.getSampleID().toString());
			sampleRowElement.addContent(newElement);
			
			//sample sange
			newElement = new Element("range");
			SampleRanges range = sample.getSample().getSampleRanges();
			ArrayList<Integer> startRange = range.getStarts();

			ArrayList<Integer> endRange = range.getEnds();
			StringBuffer result = new StringBuffer();
			for (int i = 0; i < startRange.size(); i++) {
				if (startRange.get(i).equals(endRange.get(i))) {
					result.append(startRange.get(i) + "; ");
				} else {
					result.append(startRange.get(i) + "-" + endRange.get(i) + "; ");
				}
			}
			newElement.setText(result.toString());
			sampleRowElement.addContent(newElement);
			
			
			//The detected haplogroup
			newElement = new Element("haplogroup");

			// if no haplogroup is expected, than set our result to
			// predefinied
			if (sample.getExpectedHaplogroup().toString().equals("") && sample.getDetectedHaplogroup() != null) {
				sample.setExpectedHaplogroup(sample.getDetectedHaplogroup());
			}
			if (sample.getDetectedHaplogroup() != null && !sample.getDetectedHaplogroup().equals(sample.getExpectedHaplogroup()))
				newElement.setText(sample.getExpectedHaplogroup().toString() + " (" + sample.getDetectedHaplogroup().toString() + ")");
			else {
				newElement.setText(sample.getExpectedHaplogroup().toString());
			}
			sampleRowElement.addContent(newElement);

			
			//sample status (detected haplogroup equal, similar or different to expected haplogroup? )
			newElement = new Element("status");
			newElement.setText(String.valueOf("Column not in use"));
			sampleRowElement.addContent(newElement);

			//matching quality of sample
			newElement = new Element("hit");
			RankedResult topResult = sample.getTopResult();
			if(topResult != null)
				newElement.setText(String.valueOf(topResult.getDistance()));
			
			else
				newElement.setText(String.valueOf(0));
			
			sampleRowElement.addContent(newElement);

			//all polymorphism of sample
			ArrayList<Polymorphism> t = sample.getSample().getPolymorphismn();
			String polys = "";
			for (Polymorphism t1 : t)
				polys += t1.toString() + " ";
			newElement = new Element("polys");
			sampleRowElement.addContent(newElement);
			newElement.setText(polys);
			root.addContent(sampleRowElement);
		}

		return root;
	}


	/**
	 * Updates the haplogrep classification results for all test sample(Restarts haplogroup search)
	 * @param phylotree The phylotree version to use for the update process
	 * @param rankingMethod The ranking method that should be used for the results (e.g. Hamming) 
	 */
	public void updateClassificationResults(Phylotree phylotree, RankingMethod rankingMethod){
		for (TestSample currenTestSample : testSamples.values()) {
			currenTestSample.updateSearchResults(phylotree, rankingMethod);
		}

	}

	/**
	 * Clears all previous search results
	 */
	public void clearClassificationResults() {
		for (TestSample currentSample : testSamples.values())
			currentSample.clearSearchResults();

	}

	/**
	 * Creates a new overview image of all test samples. Uses detected haplogroups and polymorphisms to create
	 * this overview.
	 * @param sessionID The current session ID
	 * @param format	The image format as string ('png' or 'svg')
	 * @param resolution The image resolution in DPI
	 * @param includeHotspots	True if mitochondrial hotspots should be included, false otherwise
	 * @param includeMissingPolys	True if polymorphisms that are expected but not found for a test sample should be included
	 * @return The generated overview file handle
	 */
	public File createOverviewImageFileBestResults(String sessionID, String format, int resolution, boolean includeHotspots, boolean includeMissingPolys){

		OverviewTree resultTree = combineAllSamplesToXMLTree(includeHotspots, includeMissingPolys);
//		Document d = new Document(resultTree);
//		saveXMLToDisc(d);

		File image = null;
		PhylotreeRenderer renderer = new PhylotreeRenderer(testSamples.values().iterator().next().searchResults.get(0).getSearchResult().getAttachedPhyloTreeNode().getTree(),resultTree);
		URL url = this.getClass().getClassLoader().getResource("haplogrepGray.png");

		try {
			renderer.setWatermark(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renderer.setDpi(resolution);

		image = renderer.createImage(format, "Overview" + sessionID + "." + format, includeHotspots);

		return image;
	}

	private OverviewTree combineAllSamplesToXMLTree(boolean includeHotspots, boolean includeMissingPolys) {
//		PhyloTreeNode combinedResultTree = null;
		OverviewTree newOverviewTree = new OverviewTree();
//		int d = 0;
		for (TestSample currentSample : testSamples.values()) {

			newOverviewTree.addNewPath(currentSample,currentSample.searchResults.get(0).getSearchResult().getDetailedResult().getPhyloTreePath());
//
//			if(d==3)break;
//			
//			d++;
			//			if (combinedResultTree == null) {
//				Haplogroup assignedHaplogroup = currentSample.getDetectedHaplogroup();
				
//				combinedResultTree = combinePathRec(combinedResultTree,currentSample.searchResults.get(0).getSearchResult().getDetailedResult().getPhyloTreePath());
//				SearchResult resultToExport = currentSample.getResult(assignedHaplogroup).getSearchResult();
//				resultToExport.getDetailedResult().
//				combinedResultTree = resultToExport.getDetailedResult().getPhyloTreePathXML(includeMissingPolys);

//				resultToExport.getSample().
				
				
//				OverviewTreePath op = new OverviewTreePath(combinedResultTree, currentSample.getSampleID(), resultToExport.getDetailedResult()
//						.getUnusedPolysXML(includeHotspots));
//				combinedResultTree = op.toXML();

//			} else {
//				Haplogroup assignedHaplogroup = currentSample.getDetectedHaplogroup();
//				SearchResult resultToExport = currentSample.getResult(assignedHaplogroup).getSearchResult();// ClusteredSearchResult.getSearchResultByHaplogroup(allResults,
//																											// assignedHaplogroup);
//
//				Element additionalPath = resultToExport.getDetailedResult().getPhyloTreePathXML(includeMissingPolys);
//				OverviewTreePath op = new OverviewTreePath(additionalPath, currentSample.getSampleID(), resultToExport.getDetailedResult().getUnusedPolysXML(
//						includeHotspots));
//				combinePathRec(combinedResultTree, op.toXML());
//			}

		}

		newOverviewTree.generateLeafNodes(includeHotspots,includeMissingPolys);
		
		return newOverviewTree;
	}
	
//	/**
//	 * Creates a xml tree out of xml paths. Recursive function.
//	 * @param currentTreeRootNode The root node of the xml tree
//	 * @param currentPathNode The root node the path begins with
//	 * @return False if the end of the current path has been reached, true otherwise. Used to check for recursion stop.
//	 */
//	private boolean combinePathRec(Element currentTreeRootNode, Element currentPathNode) {
//
//		// The current result tree does NOT contain the current subpath. So we
//		// add it to the tree
//		// and are finished
//		if (currentTreeRootNode.getChildren().size() == 0) {
//			currentTreeRootNode.addContent(currentPathNode.cloneContent());
//			return true;
//		}
//
//		if (currentTreeRootNode.getAttributeValue("type").equals("Haplogroup")
//				&& currentTreeRootNode.getAttributeValue("name").equals(currentPathNode.getAttributeValue("name"))) {
//			ArrayList<Element> newPolys = new ArrayList<Element>();
//			for (Element currentPoly : (List<Element>) currentPathNode.getChildren("Poly")) {
//				boolean found = false;
//				for (Element currentPolyTree : (List<Element>) currentTreeRootNode.getChildren("Poly")) {
//					if (currentPoly.getText().equals(currentPolyTree.getText())) {
//						found = true;
//					}
//				}
//				if (!found) {
//					Element newPoly = new Element("Poly");
//					newPoly.setText(currentPoly.getText());
//					newPolys.add(newPoly);
//				}
//			}
//		
//			for (Element c : newPolys)
//				currentTreeRootNode.addContent(c);
//
//			newPolys.clear();
//
//			// Check if we are at the end of the subpath. If true then our
//			// result tree already contains
//			// the subpath completely and we leave the function immediately
//			if (currentPathNode.getChildren("TreeNode").size() == 0) {
//				return true;
//			}
//
//			// boolean foundInsertPos = false;
//			// The tree contains our current subpath so we step one node ahead
//			// and make a RECURSIVE
//			// call to this function for each child element
//			for (Element currentTreeChild : (List<Element>) currentTreeRootNode.getChildren("TreeNode")) {
//
//				if (combinePathRec(currentTreeChild, currentPathNode.getChild("TreeNode")))
//					return true;
//			}
//
//			currentPathNode.removeChildren("Poly");
//			currentTreeRootNode.addContent(currentPathNode.cloneContent());
//
//			return true;
//		}
//
//		else {
//			return false;
//		}
//
//	}

	
	
	/**
	 * Saves XML document to disc. Only used for additional testing
	 * @param combinePathsToXMLTree The xml document object
	 */
	private void saveXMLToDisc(Document combinePathsToXMLTree) {

		try {
			Format fmt = Format.getPrettyFormat();
			XMLOutputter outp = new XMLOutputter(fmt);
			File newoutputFile = new File("testOutputXML.xml");
			outp.output(combinePathsToXMLTree, new FileOutputStream(newoutputFile));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	

}
