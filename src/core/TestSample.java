package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import phylotree.Phylotree;
import search.ClusteredSearchResults;
import search.SearchResultTreeNode;
import search.ranking.RankingMethod;
import search.ranking.results.RankedResult;
import exceptions.parse.HsdFileException;
import exceptions.parse.sample.HsdFileSampleParseException;
import exceptions.parse.sample.InvalidPolymorphismException;
import exceptions.parse.samplefile.InvalidColumnCountException;

/**
 * Represents one test sample. Includes the expected and detected haplogroup. In addition it stores all 
 * search results for a test sample 
 * 
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 * 
 */
public class TestSample implements Comparable<TestSample>{
	
	final Logger log = Logger.getLogger(TestSample.class);
	
	ArrayList<RankedResult> searchResults = new ArrayList<RankedResult>();
	ClusteredSearchResults clusteredResults = new ClusteredSearchResults(searchResults);
	
	private String testSampleID = "Unknown";
	private Haplogroup expectedHaplogroup;
	private Haplogroup detectedHaplogroup;
	private Sample sample;
	private int qualityRulesLevelReached = 0;
	private boolean reset = false;
	
	private TestSample(){
		
	}
	
	public TestSample(String sampleID,ArrayList<Polymorphism> polymorphisms,SampleRanges sampleRange) {
		this.testSampleID = sampleID;
		sample = new Sample(polymorphisms,sampleRange);
	}

	/**
	 * Parses a new test sample object from an input string
	 * @param inputString The string to parse
	 * @return The parsed string as new TestSample object
	 * @throws HsdFileSampleParseException Thrown if the string could not parsed correctly
	 */
	public static TestSample parse(String inputString) throws HsdFileException {
		TestSample parsedSample = new TestSample();
		SampleRanges sampleRange = null;
		Pattern p = Pattern.compile( "(\\d*(-|;)?)*" );
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
			
			/** Haplogrep 2.0 calculates complete range every time, decided not to use it */
			/*columns[1] = columns[1].replaceAll("\\s", "");
			String a = columns[1].trim();
			Matcher m = p.matcher(a);
			if(m.matches())
				columnPos=2;
			else columnPos=1;*/
			
			sampleRange = new SampleRanges(columns[1]);

			//Parse expected haplogroup
			if (columns[2].equals("?") || columns[2].equals("SEQ"))
				parsedSample.expectedHaplogroup = new Haplogroup("");

			else
				parsedSample.expectedHaplogroup = new Haplogroup(columns[2]);
			// Parse the sample and all its polymorphisms
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

	/**
	 * @return The haplogroup the user expected by setting it in the hsd file.
	 */
	public Haplogroup getExpectedHaplogroup() {	
		return expectedHaplogroup;
	}
	
	/**
	 * @param expectedHaplogroup The new haplogroup to expect
	 */
	public void setExpectedHaplogroup(Haplogroup expectedHaplogroup) {
		this.expectedHaplogroup = expectedHaplogroup;
	}
	
	/**
	 * @return The haplogroup of the best search result.
	 */
	public Haplogroup getDetectedHaplogroup() {
		if(getTopResult() != null){
			
			this.detectedHaplogroup = getTopResult().getSearchResult().getHaplogroup();
			return detectedHaplogroup;
		}
		
		else
			return null;
	}
	

	
	/**
	 * @return The sample object of this test sample. The sample object 
	 * represents only the sample range and its polymorphisms.
	 */
	public Sample getSample() {
		return sample;
	}

	
	/**
	 * @return Returns the unique ID of this sample
	 */
	public String getSampleID() {
		return testSampleID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String result = testSampleID + "\t" + expectedHaplogroup + "\t";
		
		for(Polymorphism currentPoly : sample.sample)
		{
			result += currentPoly.toString() + " ";
		}
		
		return result;	
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TestSample o) {
	
		 if(this.getSampleID().compareTo(o.getSampleID())<0)
			   return -1;
		 if (this.getSampleID().compareTo(o.getSampleID())>0)	
			  return 1;
		 else
			 return 0;
	}

	/**
	 * Returns a single result identified by its haplogroup name which is unique.
	 * @param haplogroup The haplogroup of the result
	 * @return The search result
	 */
	public RankedResult getResult(Haplogroup haplogroup) {
		for(RankedResult currentResult: searchResults){
			if(currentResult.getHaplogroup().equals(haplogroup))
				return currentResult;
		}
		return null;
	}

	/**
	 * @return All ranked results of this test sample.
	 */
	public List<RankedResult> getResults() {
		return searchResults;
	}
	
	/**Creates a subtree of results identified by their haplogroups. Combines the path of each result 
	 * and returns the root of the tree as json object. If a cluster of results with same distance is requested
	 * , all results in the cluster are included in the three automatically.
	 * @param selectedHaplogroups The haplogroups to identify the results
	 * @return The root json object of the tree
	 */
	public JSONObject getSelectetHaplogroupSubtree(ArrayList<String> selectedHaplogroups) {
		ArrayList<RankedResult> selectedResults = new ArrayList<RankedResult>();

		for (String currentHg : selectedHaplogroups) {
			Haplogroup selectedHaplogroup = new Haplogroup(currentHg);
			ArrayList<RankedResult> currentResults = clusteredResults.getCluster(selectedHaplogroup);

			if (currentResults != null)
				selectedResults.addAll(currentResults);

			else {
				selectedResults.add(getResult(selectedHaplogroup));
			}
		}

		ArrayList<ArrayList<SearchResultTreeNode>> paths = new ArrayList<ArrayList<SearchResultTreeNode>>();
		for (RankedResult currentResult : selectedResults) {
			ArrayList<SearchResultTreeNode> newPath = currentResult.getSearchResult().getDetailedResult().getPhyloTreePath();
			paths.add(newPath);
		}

		try {
			JSONObject result = combinePathsToTree(paths, searchResults.get(0));

			return result;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/** Recursive method to combine result path to one json tree
	 * @param paths The array of paths 
	 * @param list
	 * @return the root json object of the tree
	 * @throws JSONException
	 */
	private JSONObject combinePathsToTree(ArrayList<ArrayList<SearchResultTreeNode>> paths, RankedResult list) throws JSONException {

		JSONObject currentNode = new JSONObject();
		JSONObject result = currentNode;
		currentNode.put("id", "root");

		JSONObject dataNode = new JSONObject();
		dataNode.put("type", "hg");

		currentNode.put("data", dataNode);
		currentNode.put("name", "sample");

		int ipath = 0;
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
								log.info(currentPath.get(ipath).getHaplogroup() + " ");
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
							if (list.getSearchResult().getDetailedResult().getCorrectedBackmutations().contains(currentPoly))
								poly.put("state", "corrected");

							else
								poly.put("state", "notfound");
						}
					}

					dataNode.append("polys", poly);

				}

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

				JSONObject newNode = new JSONObject();
				newNode.put("id", currentPath.get(i1).getHaplogroup());
				newNode.put("data", dataNode);
				newNode.put("name", currentPath.get(i1).getHaplogroup());
				newNode.put("children", new JSONArray());

				newPolyNode.append("children", newNode);

				currentNode.append("children", newPolyNode);
				currentNode = newNode;
			}

		}

		return result;

	}

	/**
	 * Clears all search results
	 */
	public void clearSearchResults() {
		searchResults.clear();
		clusteredResults = null;
	}

	/**Restarts search and updates all search results for this sample
	 * @param phyloTreeToUse The phylotree version used for the search
	 * @param rankingMethod The ranking method used (e.g Hamming)
	 */
	public void updateSearchResults(Phylotree phyloTreeToUse,RankingMethod rankingMethod) {
		//if(qualityRulesLevelReached > 0){
		
			List<RankedResult> results = phyloTreeToUse.search(this, rankingMethod.clone());
		
			searchResults = (ArrayList<RankedResult>) results;
			clusteredResults = new ClusteredSearchResults(results);
		//}
	}
	
	/**
	 * @return The search results in clustered by the equal distances. Ranked by the used ranking method
	 */
	public JSONArray getClusteredSearchResults() {
		if(clusteredResults != null)
			return clusteredResults.toJSON();
		return 
				new JSONArray();
	}

	/**
	 * @return The top result of for this test sample
	 */
	public RankedResult getTopResult() {
		if(searchResults.size() > 0)
			return searchResults.get(0);
		else
			return null;
	}

	public int getQualityLevelReached() {
		return qualityRulesLevelReached;
	}

	public void setReachedQualityLevel(int level) {
		this.qualityRulesLevelReached = level;
	}
	
	public ArrayList<TestSample> createFragments(SampleRanges fragmentRanges) {

		HashMap<Integer, ArrayList<Polymorphism>> fragmentsHashMap = new HashMap<Integer, ArrayList<Polymorphism>>(); // MultiMap
		ArrayList<TestSample> resultFragments = new ArrayList<TestSample>();
	
		for(int i = 0; i <  fragmentRanges.getStarts().size();i++)
			fragmentsHashMap.put(i, new ArrayList<Polymorphism>());
		
		for (Polymorphism currentPoly : sample.getPolymorphisms()) {
			int key = fragmentRanges.getSubrangeID(currentPoly);
			
			ArrayList<Polymorphism> currentFragment = fragmentsHashMap.get(key);

			if (currentFragment == null)
				fragmentsHashMap.put(key, new ArrayList<Polymorphism>());
			
			fragmentsHashMap.get(key).add(currentPoly);

		}
		int i = 0;
		for(ArrayList<Polymorphism> currentFragment : fragmentsHashMap.values()){
			resultFragments.add( new TestSample(testSampleID  + "_Frag_" + (fragmentRanges.getStarts().get(i)), currentFragment,fragmentRanges.getSubrange(i)));
			i++;
		}
		
		return resultFragments;
	}

	public void setDetectedHaplogroup(Haplogroup detectedHaplogroup) {
		this.detectedHaplogroup = detectedHaplogroup;
	}

	public boolean isReset() {
		return reset;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}
}
