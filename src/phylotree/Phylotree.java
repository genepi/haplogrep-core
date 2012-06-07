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

import search.SearchResult;
import search.ranking.RankingMethod;
import search.ranking.results.RankedResult;

import com.sun.media.sound.InvalidFormatException;

import core.Haplogroup;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidBaseException;
import exceptions.parse.sample.InvalidPolymorphismException;

public final class Phylotree {

	PhyloTreeNode root;
	private HashMap<Polymorphism, Double>  phyloGeneticWeights = new HashMap<Polymorphism, Double>();
	private HashMap<Haplogroup, PhyloTreeNode>  haplogroupLookup = new HashMap<Haplogroup, PhyloTreeNode>();
	
	public Phylotree(InputStream phylotreeFile, InputStream phylogeneticWeightsFile)
	{

		 root= new PhyloTreeNode(this);
		// Create a JDOM document out of the phylotree XML
			SAXBuilder builder = new SAXBuilder();
			try {
				
				Document phyloTree = builder.build(phylotreeFile);
				buildPhylotree(root,phyloTree.getRootElement().getChild("haplogroup"));		
				// parses and sets the polygenetic weights
				setPolygeneticWeights(phylogeneticWeightsFile);
				
				
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
		PhyloTreeNode newNode =  new PhyloTreeNode(this,parentNode, new Haplogroup(currentXMLElement.getAttribute("name").getValue()));
		parentNode.addSubHaplogroup(newNode);
		//Update index	
		haplogroupLookup.put(newNode.getHaplogroup(), newNode);
		
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
	
	
	public List<RankedResult> search(TestSample testSample,RankingMethod rankingMethodToUse) throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException {

	
		// Start first search step
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();

		// Start at root node 
		SearchResult rootResult = new SearchResult(root, testSample);
		// First call to RECURSIVE search function
		searchPhylotree(root, results, testSample, rootResult);
		
		rankingMethodToUse.setResults(testSample, results);

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
		SearchResult rootResult = new SearchResult(root, sample);
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
		List<PhyloTreeNode> children = (List<PhyloTreeNode>) parent.getSubHaplogroups();

		for (PhyloTreeNode currentElement : children) {
			SearchResult newResult = new SearchResult(currentElement.getHaplogroup().toString(),currentElement,parentResult);

			List<Polymorphism> polys = currentElement.getExpectedPolys();
			// Check all expected polys of the current haplogroup
			for (Polymorphism currentPoly : polys) {
				// Check whether polymorphism is in range
				if (sample.getSample().getSampleRanges().contains(currentPoly)) {
					// In case of a backmutation we must correct the current
					// result since a polymorhism is no longer expected
					if (currentPoly.isBackMutation()) {
						newResult.removeExpectedPolyWeight(currentPoly);					
						newResult.removeFoundPolyWeight(currentPoly,sample.getSample());
					}
					
					// The sample contains the right polymorphism for this group
					else if (newResult.getSample().contains(currentPoly)) {
						newResult.addExpectedPolyWeight(currentPoly);
						newResult.addFoundPolyWeight(currentPoly);
					}

					// There is no fitting polymorphism in the sample though we
					// expect one for this haplogroup
					else {
						if (currentPoly.isBackMutation()) {
							newResult.removeMissingOutOfRangeWeight(currentPoly);
						}

						newResult.addExpectedPolyWeight(currentPoly);	
					}
					
				}
				
				//Polymorphism is not in sample range
				else
					newResult.addMissingOutOfRangeWeight(currentPoly);
				
			}
			
//			newResult.setUnusedNotInRange(sample.getSample().getPolyNotinRange());
			
			// Add new result to the list of all results
			results.add(newResult);
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
        return new HaploSeachManager();
    }*/

	public PhyloTreeNode getPhyloTree() {
		return root;
	}

	public boolean isSuperHaplogroup(Haplogroup startHg, Haplogroup hgToCheck) {
		PhyloTreeNode currentNode = haplogroupLookup.get(startHg);
		
		if(startHg == null)
			return false;
		
		while(currentNode != null){
			if(currentNode.getHaplogroup().equals(hgToCheck))
				return true;
			
			currentNode = currentNode.getParent();
		}
		return false;
	}

}