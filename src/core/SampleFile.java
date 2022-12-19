package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import dataVisualizers.OverviewTree;
import dataVisualizers.PhylotreeRenderer;
import exceptions.parse.HsdFileException;
import exceptions.parse.samplefile.UniqueSampleIDException;
import phylotree.Phylotree;
import qualityAssurance.QualityAssistent;
import qualityAssurance.RuleSet;
import qualityAssurance.issues.QualityIssue;
import search.ranking.RankingMethod;

/**
 * Represents the entire file of test sample. Used as main object in haplogrep.
 * 
 * @author Dominic Pacher, Sebastian Schönherr, Hansi Weissensteiner
 * 
 */
public class SampleFile {

	// final Log log = LogFactory.getLog(SampleFile.class);

	Hashtable<String, TestSample> testSamples = new Hashtable<String, TestSample>();
	QualityAssistent qualityAssistent = null;
	// QualityAssistent preChecksQualityAssistent = null;
	Phylotree usedPhyloTreeLastRun = null;
	RankingMethod usedRankingMethodLastRun = null;
	Reference reference;

	/**
	 * Main constructor of SampleFile class. Creates a new test sample instance.
	 * 
	 * @param sampleLines
	 *            An array of strings representing each line of the hsd file
	 * @throws HsdFileException
	 *             thrown if the hsd file cannot be parsed correctly
	 */
	public SampleFile(ArrayList<String> sampleLines, Reference reference) throws HsdFileException {
		int lineIndex = 1;
		this.reference = reference;
		for (String currentLine : sampleLines) {
			TestSample newSample;
			try {
				String[] splits = currentLine.split("\t");
				// if no polymorphisms have been found make a fake line (e.g.
				// for VCF)
				if (splits.length == 2) {
					StringBuilder build = new StringBuilder();
					build.append(splits[0] + "\t");
					build.append(splits[1] + "\t?\t.");
					currentLine = build.toString();
				}
				newSample = TestSample.parse(currentLine, reference);
				// log.info("new sample " + newSample);
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
	 * 
	 * @param pathToSampleFile
	 *            The path to the hsd file
	 * @param testCase
	 * @throws HsdFileException
	 * @throws IOException
	 */
	// TODO:Try to get rid of the ugly boolean testCase parameter
	public SampleFile(String pathToSampleFile, Reference reference, boolean testCase) throws HsdFileException, IOException {
		BufferedReader sampleFileStream;
		if (testCase) { // for test cases
			String userDir = new java.io.File("").getAbsolutePath();
			File sampleFile = new File(userDir + pathToSampleFile);
			sampleFileStream = new BufferedReader(new FileReader(sampleFile));
		} else { // "Load Testdata" button
			InputStream testFile = this.getClass().getClassLoader().getResourceAsStream(pathToSampleFile);
			sampleFileStream = new BufferedReader(new InputStreamReader(testFile));
		}

		String currentLine = sampleFileStream.readLine();

		if (!currentLine.startsWith("SampleId\tRange")) {
			TestSample newSample = TestSample.parse(currentLine, reference);
			testSamples.put(newSample.getSampleID(), newSample);
		}

		while ((currentLine = sampleFileStream.readLine()) != null) {
			TestSample newSample = TestSample.parse(currentLine, reference);
			testSamples.put(newSample.getSampleID(), newSample);

		}
	}

	public SampleFile() {
		// TODO Auto-generated constructor stub
	}

	public void setTestSamples(ArrayList<TestSample> file) {
		testSamples.clear();
		for (TestSample currentSample : file) {
			testSamples.put(currentSample.getSampleID(), currentSample);
		}
	}

	/**
	 * Returns a test sample.
	 * 
	 * @param sampleID
	 *            The unique sampleID of the sample
	 * @return
	 */
	public TestSample getTestSample(String sampleID) {
		return testSamples.get(sampleID);
	}

	/**
	 * Returns all test sample
	 * 
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
	 * Updates the haplogrep classification results for all test sample(Restarts
	 * haplogroup search)
	 * 
	 * @param phylotree
	 *            The phylotree version to use for the update process
	 * @param rankingMethod
	 *            The ranking method that should be used for the results (e.g.
	 *            Hamming)
	 */
	public void updateClassificationResults(Phylotree phylotree, RankingMethod rankingMethod) {
		usedPhyloTreeLastRun = phylotree;
		usedRankingMethodLastRun = rankingMethod;

		for (TestSample currenTestSample : testSamples.values()) {
			currenTestSample.updateSearchResults(phylotree, rankingMethod);
		}
	}

	/**
	 * Runs all rules to check each sample if it is ready for classification
	 */
	/**
	 * Runs all quality rules of the standard rule set
	 * 
	 * @throws FileNotFoundException
	 */
	public void runQualityChecks(Phylotree phylotree) throws FileNotFoundException {
		if (qualityAssistent == null) {
			RuleSet rules = new RuleSet();
			rules.addStandardRules();
			qualityAssistent = new QualityAssistent(testSamples.values(), rules, phylotree);
		} else {
			qualityAssistent.getRules().addStandardRules();
		}
		qualityAssistent.reevaluateRules();
	}

	public void applyNomenclatureRules(Phylotree phylotree, String file) throws FileNotFoundException {

		if (qualityAssistent == null) {
			RuleSet rules = new RuleSet();
			rules.addNomenclatureRules(file);
			qualityAssistent = new QualityAssistent(testSamples.values(), rules, phylotree);
		} else {
			qualityAssistent.getRules().addNomenclatureRules(file);
		}

		qualityAssistent.reevaluateRules();
	}

	public void reevaluateSample(TestSample sampleToReevaluate) throws FileNotFoundException {
		qualityAssistent.reevaluateRulesForSample(sampleToReevaluate);

		if (!qualityAssistent.hasFatalIssues(sampleToReevaluate))
			sampleToReevaluate.updateSearchResults(usedPhyloTreeLastRun, usedRankingMethodLastRun);

	}

	/**
	 * Clears all previous search results
	 */
	public void clearClassificationResults() {
		for (TestSample currentSample : testSamples.values())
			currentSample.clearSearchResults();

	}

	/**
	 * Creates a new overview image of all test samples. Uses detected
	 * haplogroups and polymorphisms to create this overview.
	 * 
	 * @param sessionID
	 *            The current session ID
	 * @param format
	 *            The image format as string ('png' or 'svg')
	 * @param resolution
	 *            The image resolution in DPI
	 * @param includeHotspots
	 *            True if mitochondrial hotspots should be included, false
	 *            otherwise
	 * @param includeAAC
	 *            True if polymorphisms in remaining have non-synonymous SNPS
	 * @return The generated overview file handle
	 */
	public File createOverviewImageFileBestResults(String sessionID, String format, int resolution, boolean includeHotspots, boolean includeAAC) {

		OverviewTree resultTree = combineAllSamplesToXMLTree(includeHotspots);

		File image = null;
		PhylotreeRenderer renderer = new PhylotreeRenderer(testSamples.values().iterator().next()
				.getResult(testSamples.values().iterator().next().getExpectedHaplogroup()).getSearchResult().getAttachedPhyloTreeNode().getTree(), resultTree,
				reference);

		URL url = this.getClass().getClassLoader().getResource("haplogrepGray.png");

		try {
			renderer.setWatermark(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renderer.setDpi(resolution);

		image = renderer.createImage(format, "download/phylogeneticTree_" + System.currentTimeMillis() + "." + format, includeHotspots, includeAAC);

		return image;
	}

	private OverviewTree combineAllSamplesToXMLTree(boolean includeHotspots) {
		OverviewTree newOverviewTree = new OverviewTree();
		// int d = 0;
		for (TestSample currentSample : testSamples.values()) {
			if (currentSample.searchResults.size() > 0)
				if (currentSample.searchResults.size() > 0) // <-- neu
					newOverviewTree.addNewPath(currentSample,
							currentSample.getResult(currentSample.getExpectedHaplogroup()).getSearchResult().getDetailedResult().getPhyloTreePath());

		}

		newOverviewTree.generateLeafNodes(includeHotspots, usedPhyloTreeLastRun);

		return newOverviewTree;
	}

	public QualityAssistent getQualityAssistent() {
		return qualityAssistent;
	}

	public void correctIssue(int issueID, int correctionMethodID) throws FileNotFoundException {
		QualityIssue issue = qualityAssistent.doCorrection(issueID, correctionMethodID);
		if (issue == null) {
			issue = qualityAssistent.getIssueByID(issueID);
		}
		TestSample sample = issue.getSampleOfIssue();
		reevaluateSample(sample);
	}

}
