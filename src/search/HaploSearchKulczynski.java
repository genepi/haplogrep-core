package search;

import exceptions.parse.sample.InvalidBaseException;
import exceptions.parse.sample.InvalidPolymorphismException;
import genetools.Haplogroup;
import genetools.Polymorphism;
import genetools.TestSample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;

import com.sun.media.sound.InvalidFormatException;

public class HaploSearchKulczynski implements IHaploSearch 
{
	HaploSearchManager searchManager = null;
	
	public HaploSearchKulczynski(HaploSearchManager searchManager)
	{
		this.searchManager = searchManager;
	}
	
	/* (non-Javadoc)
	 * @see haploClassification.IHaploSearch#search(genetools.TestSample)
	 */
	@Override
	public List<ClusteredSearchResult> search(TestSample testSample) throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException {

		// Remove all polymorphismn which don`t appear in the phylo tree (e.g
		// unstable ones...)
		//testSample.getSample().filter(searchManager.getAllPolysUsedInPhylotree());

		// Start first search step
		ArrayList<SearchResult> results = searchPhylotreeWrapper(testSample);

		// Cluster search results with same rank together
		ArrayList<ClusteredSearchResult> clusteredResult = ClusteredSearchResult.createClusteredSearchResult(results,testSample.getPredefiniedHaplogroup(),searchManager.getPhylotreeString());
		
		//set results to null (>20) to save memory. 
		results.clear();
		return clusteredResult;
	}

//	public void addRecommendedHaplogroups(List<ClusteredSearchResult> result, TestSample sample) {
//		sample.setRecognizedHaplogroup(new Haplogroup(result.get(0).getHaplogroup()));
//
//
//			
//			double firstRank=(result.get(0).getCluster().get(0).getRank()*100);
//			BigDecimal myDec = new BigDecimal( firstRank );
//			myDec = myDec.setScale( 1, BigDecimal.ROUND_HALF_UP );
//			sample.setResultQuality(myDec.doubleValue());		
//			
//			//set status for colors
//			if(sample.getPredefiniedHaplogroup().equals(sample.getRecognizedHaplogroup()))
//				sample.setState("identical");
//			else if(sample.getPredefiniedHaplogroup().isSuperHaplogroup(sample.getRecognizedHaplogroup())||
//					sample.getRecognizedHaplogroup().isSuperHaplogroup(sample.getPredefiniedHaplogroup()))
//				sample.setState("similar");
//			else sample.setState("mismatch");
//			
//
//		}

	//}

	/**
	 * First step (wrapper) of recursive search function
	 * 
	 * @param sample
	 * @return
	 * @throws NumberFormatException
	 * @throws InvalidBaseException
	 * @throws InvalidFormatException
	 */
	private ArrayList<SearchResult> searchPhylotreeWrapper(TestSample sample) throws NumberFormatException, InvalidPolymorphismException {
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();

		// Start at root node (mt dna reference NC_012920)
		SearchResult rootResult = new SearchResult("rCRS, NC_012920", searchManager.getPhylotreeString(), sample);
		Element node = searchManager.getPhyloTree().getRootElement();

		// First call to RECURSIVE search function
		searchPhylotree(node, results, sample, rootResult);

		return results;
	}

	/**
	 * Traverses the complete phylo tree beginning at the rCRS. For each child a
	 * new SerachResult object is created.
	 * 
	 * @param parent
	 *            The XML parent node
	 * @param results
	 *            The list of all results
	 * @param sample
	 *            The test sample
	 * @param parentResult
	 *            SearchResult of the parent
	 * @throws NumberFormatException
	 * @throws InvalidBaseException
	 * @throws InvalidFormatException
	 */
	private void searchPhylotree(Element parent, ArrayList<SearchResult> results, TestSample sample, SearchResult parentResult) throws NumberFormatException,
	InvalidPolymorphismException {
		// Query all child haplogroup nodes
		List<Element> children = (List<Element>) parent.getChildren("haplogroup");

		for (Element currentElement : children) {
			SearchResult newResult = new SearchResult(currentElement.getAttributeValue("name"),searchManager.getPhylotreeString(), parentResult);

			List<Element> polys = currentElement.getChild("details").getChildren("poly");
			//H2a2a has no polys
			//if(polys.size() > 0){
			PhyloTreeNode newNode = new PhyloTreeNode(new Haplogroup(currentElement.getAttributeValue("name")));
			// Check all expected polys of the current haplogroup
			for (Element currentPolyElement : polys) {
				Polymorphism currentPoly = new Polymorphism(currentPolyElement.getValue());

				// Check whether polymorphism is in range
				if (sample.getSampleRanges().contains(currentPoly)) {
					// In case of a backmutation we must correct the current
					// result.
					// A polymorhism is no longer expected
					if (currentPoly.isBackMutation()) {
						newResult.removeExpectedPoly(currentPoly);
						newResult.removeFoundPoly(currentPoly);
						
						newNode.removeExpectedPoly(currentPoly);
						newNode.removeCorrectPoly(currentPoly);
						
						//Add correct backmutation poly					
						newNode.addExpectedPoly(currentPoly);
						///newResult.addExpectedPoly(currentPoly);
						
						Polymorphism newPoly = new Polymorphism(currentPoly);
						newPoly.setBackMutation(false);
						
						if (!newResult.getSample().contains(newPoly))
						{
							newNode.addCorrectPoly(currentPoly);
							//newResult.addCorrectPoly(currentPoly);
						}
					}

					// The sample contains the right polymorphism for this group
					else if (newResult.getSample().contains(currentPoly)) {
						newResult.addExpectedPoly(currentPoly);
						newResult.addFoundPoly(currentPoly);
						
						newNode.addExpectedPoly(currentPoly);
						newNode.addCorrectPoly(currentPoly);
					}

					// There is no fitting polymorphism in the sample though we
					// expect one for this haplogroup
					else {
						if (currentPoly.isBackMutation()) {
							newResult.removeMissingOutOfRangePoly(currentPoly);
						}
						
						newResult.addExpectedPoly(currentPoly);
						newNode.addExpectedPoly(currentPoly);	
					}
					
				}
				
				//Polymorphism is not in sample range
				else
				{
					newResult.addMissingOutOfRangePoly(currentPoly);
					newNode.addNotInRangePoly(currentPoly);	
				}
				
			//}
			}
			
			newResult.setUnusedNotInRange(sample.getPolyNotinRange());
			
			
			
			// Add new result to the list of all results
			results.add(newResult);
			newResult.extendPath(newNode);
			// RECURSIVE call
			searchPhylotree(currentElement, results, sample, newResult);
		}
	}
}
