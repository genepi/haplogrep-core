package search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import search.ranking.results.RankedResult;
import core.Haplogroup;

/**
 * Encapsulates a list of ranked results and clusters same distance instances.
 * 
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 * 
 */
public class ClusteredSearchResults {
	private ArrayList<ArrayList<RankedResult>> cluster = new ArrayList<ArrayList<RankedResult>>();
	private HashMap<Haplogroup, ArrayList<RankedResult>> clusterLookup = new HashMap<Haplogroup, ArrayList<RankedResult>>();

	/**
	 * Creates a new instance
	 * 
	 * @param unclusteredResults
	 *            A list of ranked unclustered results
	 */
	public ClusteredSearchResults(List<RankedResult> unclusteredResults) {
		int i = -1;
		double currentRank = -100;

		ArrayList<RankedResult> currentCluster = null;
		for (RankedResult currentResult : unclusteredResults) {

			// Only process the top rated results
			if (i == 50)
				break;

			if (currentRank != currentResult.getDistance()) {
				i++;

				currentRank = currentResult.getDistance();

				currentCluster = new ArrayList<RankedResult>();
				cluster.add(currentCluster);
				clusterLookup.put(currentResult.getHaplogroup(), currentCluster);
				currentCluster.add(currentResult);
			} else
				currentCluster.add(currentResult);

		}
	}

	/**
	 * @return A JSON array of all clusters. Used to transmit results to web gui.
	 */
	public JSONArray toJSON() {
		JSONArray resultArray = null;
		resultArray = new JSONArray();

		int rank = 1;
		for (ArrayList<RankedResult> currentCluster : cluster) {

			try {
				JSONObject resultObject = getClusterAsJSON(currentCluster);
				resultObject.put("pos", rank);
				resultArray.put(resultObject);
				rank++;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return resultArray;
	}

	/**
	 * @param cluster The cluster a JSON object should be created from.
	 * @return	The JSON representation of the cluster
	 * @throws JSONException
	 */
	private JSONObject getClusterAsJSON(ArrayList<RankedResult> cluster) throws JSONException {

		JSONObject child = new JSONObject();
		JSONObject child1 = new JSONObject();
		JSONArray a = new JSONArray();

		int i = 1;
		for (RankedResult currentResult : cluster) {
			if (i == 1) {
				child.put("iconCls", "icon-treegrid");
				child.put("expanded", true);
				if (cluster.size() == 1)
					child.put("leaf", true);
				else
					child.put("leaf", false);

				currentResult.attachToJsonObject(child);
			}

			else {
				child1 = new JSONObject();
				child1.put("pos", "");
				child1.put("iconCls", "icon-treegridSW");
				child1.put("leaf", true);
				currentResult.attachToJsonObject(child1);
				a.put(child1);
			}

			i++;

		}
		child.put("children", a);

		return child;
	}

	/**
	 * @param haplogroup The haplogroup a cluster is searched for
	 * @return The cluster the haplogroup belongs to
	 */
	public ArrayList<RankedResult> getCluster(Haplogroup haplogroup) {
		return clusterLookup.get(haplogroup);
	}

}
