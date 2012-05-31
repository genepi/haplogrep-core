//package search;
// 
//
//import java.util.Comparator;
//import java.util.Iterator;
//
//import core.Polymorphism;
// 
//public class HammingDistanceComparator implements Comparator<SearchResult> {
// 
//	private String phyolTreeString;
//	
//	public HammingDistanceComparator(String phyolTreeString){
//		this.phyolTreeString = phyolTreeString;
//	}
//	
//  @Override
//  public int compare(SearchResult r1, SearchResult r2) {
// 
//	if(r1.getHammingDistance() == r2.getHammingDistance())
//		return 0;
//	else if(r1.getHammingDistance() > r2.getHammingDistance())
//		return -1;
//	else
//		return 1;
//    }
//
//
//	private double calcHammingDistance(SearchResult r) {
//		double distance = 0;
//		Iterator<Polymorphism> missing = r.getIterMissingPolys();
//		
//		while(missing.hasNext()){
//			distance += missing.next().getPhylogeneticWeight(phyolTreeString);
//		}
//		
//		for(Polymorphism remainingPoly : r.getUnusedPolys())
//			distance += remainingPoly.getPhylogeneticWeight(phyolTreeString);
//		
//		return distance;
//	}
//  
//}