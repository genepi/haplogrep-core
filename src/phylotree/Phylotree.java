package phylotree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import search.ClusteredSearchResult;
import search.SearchResult;
import search.SearchResultTreeNode;
import search.ranking.HammingRanking;
import search.ranking.KychinskyRanking;
import search.ranking.RankingMethod;
import search.results.Result;


import com.sun.media.sound.InvalidFormatException;

import core.Haplogroup;
import core.Polymorphism;
import core.TestSample;
import exceptions.PolyDoesNotExistException;
import exceptions.parse.sample.InvalidBaseException;
import exceptions.parse.sample.InvalidPolymorphismException;

public final class Phylotree {

	PhyloTreeNode root;
	private String phylotreeString;
	private String fluctRates;
	private HashMap<Polymorphism, Double>  phyloGeneticWeights = new HashMap<Polymorphism, Double>();
	
	
	public Phylotree(String phylotree, String weights)
	{
		this.phylotreeString=phylotree;
//		allPolysUsedinPhylotree = new ArrayList<Polymorphism>();
		 root= new PhyloTreeNode();
		// Create a JDOM document out of the phylotree XML
			SAXBuilder builder = new SAXBuilder();
			try {
				//for CLAP protocol:
				InputStream phyloFile = this.getClass().getClassLoader().getResourceAsStream(phylotree);
				InputStream flucRates = this.getClass().getClassLoader().getResourceAsStream(weights);
				Document phyloTree = builder.build(phyloFile);
				buildPhylotree(root,phyloTree.getRootElement().getChild("haplogroup"));		
				// parses and sets the polygenetic weights
				setPolygeneticWeights(flucRates);
				
				
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidBaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidPolymorphismException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
	}
	
	private void buildPhylotree(PhyloTreeNode parentNode, Element currentXMLElement) throws NumberFormatException, InvalidPolymorphismException{
		PhyloTreeNode newNode =  new PhyloTreeNode(parentNode, new Haplogroup(currentXMLElement.getAttribute("name").getValue()));
		parentNode.addSubHaplogroup(newNode);
		
		List<Element> polys = currentXMLElement.getChild("details").getChildren("poly");
		for (Element currentPolyElement : polys) {
			Polymorphism newExpectedPoly = new Polymorphism(currentPolyElement.getValue());
			newNode.addExpectedPoly(newExpectedPoly);		
		}
		
		List<Element> children = (List<Element>) currentXMLElement.getChildren("haplogroup");
		for (Element currentChildElement : children) {				
			buildPhylotree(newNode, currentChildElement);
		}
	}
	
	
	public List<Result> search(TestSample testSample,RankingMethod rankingMethodToUse) throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException {

	
		// Start first search step
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();

		// Start at root node 
		SearchResult rootResult = new SearchResult(this,root, testSample);
		// First call to RECURSIVE search function
		searchPhylotree(root, results, testSample, rootResult);
		
		rankingMethodToUse.setResults(testSample, results);
		
		// Cluster search results with same rank together
//		ArrayList<ClusteredSearchResult> clusteredResult = ClusteredSearchResult.createClusteredSearchResult(rankingMethodToUse.getResults(),testSample.getExpectedHaplogroup());
		
		//set results to null (>20) to save memory. 
		results.clear();
		return rankingMethodToUse.getResults();
	}



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
		SearchResult rootResult = new SearchResult(this,root, sample);
//		PhyloTreeNode node = root;

		// First call to RECURSIVE search function
		searchPhylotree(root, results, sample, rootResult);

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
	private void searchPhylotree(PhyloTreeNode parent, ArrayList<SearchResult> results, TestSample sample, SearchResult parentResult) throws NumberFormatException,
	InvalidPolymorphismException {
		// Query all child haplogroup nodes
		List<PhyloTreeNode> children = (List<PhyloTreeNode>) parent.getSubHaplogroups();//.getChildren("haplogroup");

		for (PhyloTreeNode currentElement : children) {
			SearchResult newResult = new SearchResult(currentElement.getHaplogroup().toString(),currentElement,/*.getAttributeValue("name")*/ parentResult);

			List<Polymorphism> polys = currentElement.getExpectedPolys();//.getChild("details").getChildren("poly");
			//H2a2a has no polys
			//if(polys.size() > 0){
			SearchResultTreeNode newNode = new SearchResultTreeNode(currentElement/*new Haplogroup(currentElement.getAttributeValue("name"))*/);
			// Check all expected polys of the current haplogroup
			for (Polymorphism currentPoly : polys) {
//				Polymorphism currentPoly = new Polymorphism(currentPolyElement.getValue());

				// Check whether polymorphism is in range
				if (sample.getSample().getSampleRanges().contains(currentPoly)) {
					// In case of a backmutation we must correct the current
					// result.
					// A polymorhism is no longer expected
					if (currentPoly.isBackMutation()) {
						newResult.removeExpectedPoly(currentPoly);
//						newResult.removeExpectedPolyWeight(currentPoly);
						newResult.removeFoundPoly(currentPoly);
						newResult.removeFoundPolyWeight(currentPoly,sample.getSample());
						
						
						
						//Add correct backmutation poly					
						newNode.addExpectedPoly(currentPoly);
						///newResult.addExpectedPoly(currentPoly);
						
						Polymorphism newPoly = new Polymorphism(currentPoly);
						newPoly.setBackMutation(false);
//						
//						newNode.removeExpectedPoly(newPoly);
//						newNode.removeFoundPoly(newPoly);
						
						if (!newResult.getSample().contains(newPoly))
						{
							newNode.addFoundPoly(currentPoly);
							//newResult.addCorrectPoly(currentPoly);
						}
					}

					// The sample contains the right polymorphism for this group
					else if (newResult.getSample().contains(currentPoly)) {
						newResult.addExpectedPoly(currentPoly);
						newResult.addExpectedPolyWeight(currentPoly);
						newResult.addFoundPoly(currentPoly);
						newResult.addFoundPolyWeight(currentPoly);
						
						newNode.addExpectedPoly(currentPoly);
						newNode.addFoundPoly(currentPoly);
					}

					// There is no fitting polymorphism in the sample though we
					// expect one for this haplogroup
					else {
						if (currentPoly.isBackMutation()) {
							newResult.removeMissingOutOfRangePoly(currentPoly);
						}
						
						newResult.addExpectedPoly(currentPoly);
						newResult.addExpectedPolyWeight(currentPoly);
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
			
			newResult.setUnusedNotInRange(sample.getSample().getPolyNotinRange());
			
			
			
			// Add new result to the list of all results
			results.add(newResult);
			newResult.extendPath(newNode);
			// RECURSIVE call
			searchPhylotree(currentElement, results, sample, newResult);
		}
	}
	/**
	 * Traverses the whole phylo tree and saves all appearing phylo types
	 * @throws JDOMException
	 * @throws InvalidBaseException
	 * @throws InvalidFormatException
	 */
	/*private void extractAllPolysFromPhylotree() throws JDOMException, InvalidPolymorphismException, InvalidFormatException {
		List<Element>  nameList = XPath.selectNodes( phyloTree, "//poly" );
		int i=0;
		for ( Element a : nameList ) 
		{ 
			if(i++<20)
				System.out.println(a.getValue());
			allPolysUsedinPhylotree.add(new Polymorphism(a.getValue()));
		}
	}*/
	
	/**
	 * Parses the pyhlo weights given by a file. Sets weights for all polymorphismn
	 * @param pathToPhyloWeightsFile 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InvalidBaseException
	 * @throws InvalidFormatException
	 */
	//private void setPolygeneticWeights(String pathToPhyloWeightsFile) throws FileNotFoundException, IOException,
	public void setPolygeneticWeights(InputStream pathToPhyloWeightsFile) throws FileNotFoundException, IOException,
			InvalidBaseException {
		
		//Read in the fluctuation rates
		BufferedReader flucFile = new BufferedReader ( new InputStreamReader ( pathToPhyloWeightsFile ) ); 	
		String line = flucFile.readLine();
	
		//Read-in each line
		int i=0;
		while(line != null)
		{
			StringTokenizer mainTokenizer = new StringTokenizer(line,"\t");
			
			String polyString = mainTokenizer.nextToken();
			double phyloGeneticWeight = Double.parseDouble(mainTokenizer.nextToken());
			
			
			//TODO remove with fixed phylotree 8 BUG 2232.12A
			try {
				changePhyloGeneticWeight(new Polymorphism(polyString) ,phyloGeneticWeight);
			} catch (Exception e) {
				// TODO: handle exception
			}
			line = flucFile.readLine();
			}
		
	}
	
	/**
	 * Searches and renames a certain haplogroup in the XML tree
	 * @param oldName old name
	 * @param newName new name
	 * @throws JDOMException
	 */

//	public void changePoly(Haplogroup hg, Polymorphism polyOld,Polymorphism polyNew) throws JDOMException, PolyDoesNotExistException
//	{
//		List<Element> e = getPolysOfHg(hg);
//		
//		for (Element ce : e) {
//			if (ce.getText().equals(polyOld.toString())) {
//				ce.setText(polyNew.toString());
//				return;
//			}
//		}
//		
//		throw new PolyDoesNotExistException();
//	}

//	public List<Element> getPolysOfHg(Haplogroup hg) throws JDOMException {
//		Element titleNode =  (Element) XPath.selectSingleNode( phyloTree, "//haplogroup[@name=\""+ hg.toString()+"\"]/details");
//		
//		List<Element> e = titleNode.getChildren("poly");
//		return e;
//	}

	public double getMutationRate(Polymorphism poly)
	{
		if(phyloGeneticWeights.containsKey(poly))
			return phyloGeneticWeights.get(poly);
		
		else
			return 0;

	}
	
	public void changePhyloGeneticWeight(Polymorphism poly, double newPhylogeneticWeight)
	{		
		phyloGeneticWeights.put(poly, newPhylogeneticWeight);
	}
	
	/*
	public final HaploSearch createNewSearch()
	{
		return new HaploSearch(instance);
	}*/
	/*
	public static HaploSearchManager getInstance() 
    {
        return new HaploSearchManager();
    }*/

	public PhyloTreeNode getPhyloTree() {
		return root;
	}
	
//	public void setPhyloTree(Document phylotree) {
//		this.phyloTree= phylotree;
//	}

	public void setPhylotreeString(String phylotree) {
		this.phylotreeString = phylotree;
	}


	public String getPhylotreeString() {
		return phylotreeString;
	}


	public void setFluctRates(String fluctRates) {
		this.fluctRates = fluctRates;
	}


	public String getFluctRates() {
		return fluctRates;
	}
	
}