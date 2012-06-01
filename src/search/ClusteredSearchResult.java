package search;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import search.ranking.Ranker;
import search.results.Result;
import search.results.ResultKylcinski;

import core.Haplogroup;
import core.Polymorphism;



public class ClusteredSearchResult implements Comparable<ClusteredSearchResult>{
	private int rankedPosition = -1;
	private ArrayList<Result> cluster =  new ArrayList<Result>();
	private final static int MAX_RESULTS = 50;
	
	public ClusteredSearchResult(int position) {
		rankedPosition = position;
	}

	public static ArrayList<ClusteredSearchResult> createClusteredSearchResult(Ranker usedRanker)
	{
		 ArrayList<ClusteredSearchResult> clusteredSearchResult = new ArrayList<ClusteredSearchResult>();
		
		 int i = 0;
		 double currentRank = -1;
		 for(Result currentResult : usedRanker.getResults())
		 {
			 //Only process the top rated results
			 if(i == MAX_RESULTS)
				 break;
			 
			 if(currentRank != currentResult.getDistance()) 
			 {
				 clusteredSearchResult.add(new ClusteredSearchResult(i+1));
				 i++;
				 currentRank = currentResult.getDistance();
			 }
			 clusteredSearchResult.get(i-1).getCluster().add(currentResult);
		 }
		 
		return clusteredSearchResult;	
	}
	
	public int getRankedPosition() {
		return rankedPosition;
	}


	
	public Haplogroup getHaplogroup()
	{
		return getCluster().get(0).getHaplogroup();
	}
	
//	public boolean containsSuperhaplogroup(Haplogroup haplogroup)
//	{
//		//if(haplogroup.isSuperHaplogroup(getCluster().get(0).getHaplogroup()) == false)
//		//{
//	/*	for(Result currentResult : getCluster())
//		 {	
//			if(haplogroup.isSuperHaplogroup(currentResult.getHaplogroup()))
//				return true;
//		 }*/
//		
//		//}
//		if(haplogroup.isSuperHaplogroup(getCluster().get(0).getHaplogroup()))
//			return true;
//		
//		 return false;
//	}
	
	//TODO needed?
	@Override
	public int compareTo(ClusteredSearchResult o) {
		return this.getCluster().get(0).compareTo(o.getCluster().get(0));
	}
	
//	public String toString()
//	{
//		String result = "";
//		DecimalFormat df = new DecimalFormat( "0.000" );
//		result += "\t-------------------------------------------------------------------\n";
//		
//		for(Result currentResult : getCluster())
//		 {			
//			result += "\tHaplogroup: " + currentResult.getHaplogroup() +"\n";
//			result += "\tFinalRank: " + currentResult.getRank() + " \n";
//			result += "\tCorrect polys in test sample ratio: " + currentResult.getCorrectPolyInTestSampleRatio()
//			+ " ("+df.format(currentResult.getWeightFoundPolys()) + " / " +df.format(currentResult.getUsedWeightPolys())    + ")"+"\n"; 
//			result += "\tCorrect polys in haplogroup ratio: " + df.format(currentResult.getCorrectPolyInHaplogroupRatio())
//			+ " ("+df.format(currentResult.getWeightFoundPolys()) + " / " +df.format(currentResult.getExpectedWeightPolys())    + ")"+"\n"; 
//			
//			
//			
//			//result += "\t";
//			
//			//Collections.sort(currentResult.getExpectedPolysFullRange());
//			//for(Polymorphism currentMissing: currentResult.getExpectedPolysFullRange())
//			//	result +=currentMissing+ "(" + currentMissing.getMutationRate(currentMissing.getMutation()) + ") ";
//			
//			//result += "=" + currentResult.getExpectedWeightFullRangePolys() + "\n";
//			
//			result += "\t\tExpected\tCorrect\t\tUsed polys\tWeight\n";
//			
//			Collections.sort(currentResult.getCheckedPolys());
//			
//			
//			ArrayList<Polymorphism> unusedPolys = new ArrayList<Polymorphism>();
//			unusedPolys.addAll(currentResult.getSample().getPolymorphismn());
//			for(Polymorphism current : currentResult.getCheckedPolys())
//			{
//				
//				String fluctString = df.format( current.getPhylogeneticWeight(phyolTreeString));
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
//				String fluctString = df.format( current.getPhylogeneticWeight(phyolTreeString));
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

	public void setCluster(ArrayList<Result> cluster) {
		this.cluster = cluster;
	}

	public ArrayList<Result> getCluster() {
		return cluster;
	}

	//TODO Ugly reduant Refactor!
	public JSONObject toJson() throws JSONException {
		
		DecimalFormat df = new DecimalFormat( "0.000",new DecimalFormatSymbols(Locale.US));
		JSONObject child =  new JSONObject();
		JSONObject child1 =  new JSONObject();
		JSONArray a = new JSONArray();
		
		int i = 1;
		for(Result currentResult : getCluster())
		 {	
		   	if(i == 1)
			{
		   		child.put("pos",this.rankedPosition);
		   		child.put("name",currentResult.getHaplogroup().toString());
				child.put("id",currentResult.getHaplogroup().toString());
				child.put("rank",df.format(currentResult.getDistance()));
				if(currentResult instanceof ResultKylcinski){
					child.put("rankHG",df.format(((ResultKylcinski)currentResult).getCorrectPolyInHaplogroupRatio()));
					child.put("rankS",df.format(((ResultKylcinski)currentResult).getCorrectPolyInTestSampleRatio()));
				}
				child.put("iconCls", "icon-treegrid");
				child.put("expanded", true);
				if(getCluster().size()==1) child.put("leaf", true); else child.put("leaf", false); 
		   		
			}
		   	
		   	else
		   	{
		   		child1 =  new JSONObject();
		   		child1.put("pos","");
		   		child1.put("name",currentResult.getHaplogroup().toString());
		   		child1.put("id",currentResult.getHaplogroup().toString());
				child1.put("rank",df.format(currentResult.getDistance()).toString());
				if(currentResult instanceof ResultKylcinski){
					child.put("rankHG",df.format(((ResultKylcinski)currentResult).getCorrectPolyInHaplogroupRatio()));
					child.put("rankS",df.format(((ResultKylcinski)currentResult).getCorrectPolyInTestSampleRatio()));
				}
				child1.put("iconCls", "icon-treegridSW");
				child1.put("leaf", true);
				a.put(child1);
		   	}	
		   	
			   	i++;	
			   	
			   
		 }
		child.put("children", a);
		
		
		return child;
	}
	
//public SearchResultPath getPhyloTreePath(Haplogroup haplogroup) {
//	for(Result currentResult : cluster)
//	{
//		if(currentResult.getHaplogroup().equals(haplogroup))
//		{
//			return currentResult.getUsedPath();
//		}
//	}
//	
//	return null;
//}
//
//public SearchResultPath getPhyloTreePath(int index) {
//	
//	return cluster.get(index).getUsedPath();
//	/*for(Result currentResult : cluster)
//	{
//		if(currentResult.getHaplogroup().equals(haplogroup))
//		{
//			return currentResult.getUsedPath();
//		}
//	}
//	
//	return null;*/
//}

//	public Element getUnusedPolysXML(String haplogroup) {
//		for(Result currentResult : cluster)
//		{
//			if(currentResult.getHaplogroup().equals(new Haplogroup(haplogroup)))
//			{
//				return currentResult.getUnusedPolysXML(currentResult.getPhyloTreePath(),true);
//			}
//		}
//		
//		return null;
//	}
//	
//	public Element getNotInRangePolysXML(String haplogroup) {
//		for(Result currentResult : cluster)
//		{
//			if(currentResult.getHaplogroup().equals(new Haplogroup(haplogroup)))
//			{
//				return currentResult.getNotInRangePolysXML();
//			}
//		}
//		
//		return null;
//	}

	public Result getSearchResult(Haplogroup haplogroup) {
		for(Result currentResult : cluster)
		{
			if(currentResult.getHaplogroup().equals(haplogroup))
			{
				return currentResult;
			}
		}
		return null;
	}
	
	public static Result getSearchResultByHaplogroup(List<ClusteredSearchResult> allResults,Haplogroup haplogroup){

		Result result = null;
			for(ClusteredSearchResult cr : allResults){
			
				result = cr.getSearchResult(haplogroup);
				if(result != null)
				break;
			
		}
			return result;
	}
}
