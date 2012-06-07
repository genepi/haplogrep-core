package search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import search.ranking.results.RankedResult;
import core.Haplogroup;



public class ClusteredSearchResult{
	private ArrayList<ArrayList<RankedResult>> cluster =  new ArrayList<ArrayList<RankedResult>>();
	private HashMap<Haplogroup,ArrayList<RankedResult>> clusterLookup =  new HashMap<Haplogroup,ArrayList<RankedResult>>();
	
	public  ClusteredSearchResult(List<RankedResult> unclusteredResults)
	{
		 int i = -1;
		 double currentRank = -100;
		 
//		 for(RankedResult currentResult : unclusteredResults)
//		 {
//		 currentResult.getPhyloSearchData().getDetailedResult().updateResult();
//		 }
		 
		 ArrayList<RankedResult> currentCluster =  null;
		 for(RankedResult currentResult : unclusteredResults)
		 {
			
			 
			 //Only process the top rated results
			 if(i == 50)
				 break;
			 
			 if(currentRank != currentResult.getDistance()) 
			 {
				 i++;
				  
				 currentRank = currentResult.getDistance();
					
				 currentCluster =  new ArrayList<RankedResult>();
				 cluster.add(currentCluster);
				 clusterLookup.put(currentResult.getHaplogroup(), currentCluster);
				 currentCluster.add(currentResult);
			 }
			 else
				 currentCluster.add(currentResult);
			 
			
		 }		 	
	}

//	public Element getDetailsXML(String haplogroup)
//	{
//		for(RankedResult currentResult : cluster)
//		{
//			if(currentResult.getHaplogroup().equals(new Haplogroup(haplogroup)))
//			{
//				return currentResult.getPhyloSearchData().getDetailedResult().toXML();
//			}
//		}
//		
//		return null;
//	}
	
//	public String getHaplogroup()
//	{
//		return getCluster().get(0).getHaplogroup().toString();
//	}
	
//	public boolean containsSuperhaplogroup(Haplogroup haplogroup)
//	{
//		if(haplogroup.isSuperHaplogroup(getCluster().get(0).getPhyloSearchData().getPhyloTree(),getCluster().get(0).getHaplogroup()))
//			return true;
//		
//		 return false;
//	}
	
//	@Override
//	public int compareTo(ClusteredSearchResult o) {
//		if(this.getCluster().get(0).getDistance() > o.getCluster().get(0).getDistance())
//			return -1;
//		if(this.getCluster().get(0).getDistance() < o.getCluster().get(0).getDistance())
//			return 1;	
//		else
//			return 0;
//		
//	}
	
//	public String toString()
//	{
//		String result = "";
//		DecimalFormat df = new DecimalFormat( "0.000" );
//		result += "\t-------------------------------------------------------------------\n";
//		
//		for(RankedResult currentResult : getCluster())
//		 {			
//			result += "\tHaplogroup: " + currentResult.getHaplogroup() +"\n";
//			result += "\tFinalRank: " + currentResult.getRank() + " \n";
//			result += "\tCorrect polys in test sample ratio: " + currentResult.getCorrectPolyInTestSampleRatio()
//			+ " ("+df.format(currentResult.getWeightFoundPolys()) + " / " +df.format(currentResult.getUsedWeightPolys())    + ")"+"\n"; 
//			result += "\tCorrect polys in haplogroup ratio: " + df.format(currentResult.getCorrectPolyInHaplogroupRatio())
//			+ " ("+df.format(currentResult.getWeightFoundPolys()) + " / " +df.format(currentResult.getExpectedWeightPolys())    + ")"+"\n"; 
//			
//
//			result += "\t\tExpected\tCorrect\t\tUsed polys\tWeight\n";
//			
//			Collections.sort(currentResult.getDetailedResult().getCheckedPolys());
//			
//			
//			ArrayList<Polymorphism> unusedPolys = new ArrayList<Polymorphism>();
//			unusedPolys.addAll(currentResult.getSample().getPolymorphismn());
//			for(Polymorphism current : currentResult.getDetailedResult().getCheckedPolys())
//			{
//				
//				String fluctString = df.format( currentResult.getPhyloTree().getMutationRate(current));
//				
//				
//				result +=  "\t\t"+ current.toString();
//				
//				result +=  "\t\t";
//				if(currentResult.getFoundPolys().contains(current))
//					result +=  current.toString();
//				
//				result +=  "\t\t";
//				
//				//The polymorphism is  contained in this haplogroup
//				if(currentResult.getSample().getPolymorphismn().contains(current))
//				{
//					result +=  current.toString();	
//					unusedPolys.remove(current);
//				}
//								
//				
//				
//					
//				result += "\t\t" + fluctString;
//				result +=  "\n";
//						
//			}
//			
//			//Write unused polymorphismn in this haplogroup
//			for(Polymorphism current : unusedPolys)
//			{
//				String fluctString = df.format( currentResult.getPhyloTree().getMutationRate(current));
//				result +=  "\t\t\t\t\t\t" + current; 
//				result += "\t\t" + fluctString + "\n";
//			}
//			result += "\t\t-------------------------------------------------------------\n";
//			result += "\t\t" + df.format(currentResult.getExpectedWeightPolys())
//				+"\t\t"+ df.format(currentResult.getWeightFoundPolys()) 
//				+"\t\t"+ df.format(currentResult.getUsedWeightPolys()) +"\n";
//			
//			result += "\n\n";
//		 }
//		
//		return result;
//	}

//	public void setCluster(ArrayList<SearchResult> cluster) {
//		this.cluster = cluster;
//	}

//	public ArrayList<RankedResult> getCluster() {
//		return cluster;
//	}


public JSONArray toJSON(){
	JSONArray resultArray = null;
	resultArray = new JSONArray();

	int rank = 1;
	for (ArrayList<RankedResult> currentCluster : cluster) {

		try {
			JSONObject resultObject = getClusterAsJSON(currentCluster);
			resultObject.put("pos",rank);
			resultArray.put(resultObject);
			rank++;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	return resultArray;
}
private JSONObject getClusterAsJSON(ArrayList<RankedResult> cluster) throws JSONException {
	
		JSONObject child =  new JSONObject();
		JSONObject child1 =  new JSONObject();
		JSONArray a = new JSONArray();
		
		int i = 1;
		for(RankedResult currentResult : cluster)
		 {	
		   	if(i == 1)
			{
				child.put("iconCls", "icon-treegrid");
				child.put("expanded", true);
				if (cluster.size() == 1)
					child.put("leaf", true);
				else
					child.put("leaf", false);
		   		
				currentResult.attachToJsonObject(child);
			}
		   	
		   	else
		   	{
		   		child1 =  new JSONObject();
		   		child1.put("pos","");
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

public ArrayList<RankedResult> getCluster(Haplogroup haplogroup) {
	return clusterLookup.get(haplogroup);
}
	

//	public Element getUnusedPolysXML(String haplogroup) {
//		for(SearchResult currentResult : cluster)
//		{
//			if(currentResult.getHaplogroup().equals(new Haplogroup(haplogroup)))
//			{
//				return currentResult.getDetailedResult().getUnusedPolysXML(true);
//			}
//		}
//		
//		return null;
//	}
//	
//	public Element getNotInRangePolysXML(String haplogroup) {
//		for(SearchResult currentResult : cluster)
//		{
//			if(currentResult.getHaplogroup().equals(new Haplogroup(haplogroup)))
//			{
//				return currentResult.getNotInRangePolysXML();
//			}
//		}
//		
//		return null;
//	}
//
//	public SearchResult getSearchResult(Haplogroup haplogroup) {
//		for(SearchResult currentResult : cluster)
//		{
//			if(currentResult.getHaplogroup().equals(haplogroup))
//			{
//				return currentResult;
//			}
//		}
//		return null;
//	}
//	
//	public static SearchResult getSearchResultByHaplogroup(List<ClusteredSearchResult> allResults,Haplogroup haplogroup){
//
//		SearchResult result = null;
//			for(ClusteredSearchResult cr : allResults){
//			
//				result = cr.getSearchResult(haplogroup);
//				if(result != null)
//				break;
//			
//		}
//			return result;
//	}


}
