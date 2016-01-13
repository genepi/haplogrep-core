package phylotree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import search.SearchResult;
import search.ranking.RankingMethod;
import search.ranking.results.RankedResult;

import core.Haplogroup;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidBaseException;
import exceptions.parse.sample.InvalidPolymorphismException;

/**
 * Represents an instance of a phylotree. Includes phylogentic weights and
 * search code for haplogroup detection.
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */
public final class Phylotree {

	final Log log = LogFactory.getLog(Phylotree.class);
	private PhyloTreeNode root;
	private HashMap<Polymorphism, Double> phyloGeneticWeights = new HashMap<Polymorphism, Double>();
	private HashMap<Haplogroup, PhyloTreeNode> haplogroupLookup = new HashMap<Haplogroup, PhyloTreeNode>();

	/**
	 * Creates a new phylotree instance
	 * 
	 * @param phylotreeFile
	 *            Inputstream to the phylotree input file
	 * @param phylogeneticWeightsFile
	 *            Inputstream to the phylogentic weights file
	 */
	public Phylotree(InputStream phylotreeFile, InputStream phylogeneticWeightsFile) {

		root = new PhyloTreeNode(this);
		// Create a JDOM document out of the phylotree XML
		SAXBuilder builder = new SAXBuilder();
		try {

			Document phyloTree = builder.build(phylotreeFile);
			buildPhylotree(root, phyloTree.getRootElement().getChild("haplogroup"));
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

	/**
	 * Recursively creates a new phylotree using a xml input tree.
	 * 
	 * @param parentNode
	 *            The current parent node of the phylotree
	 * @param currentXMLElement
	 *            The current parent element of the xml tree to parse
	 * @throws InvalidPolymorphismException
	 *             Thrown if the phylotree contains invalid (unreadable)
	 *             polymorphisms
	 */
	private void buildPhylotree(PhyloTreeNode parentNode, Element currentXMLElement) throws InvalidPolymorphismException {
		PhyloTreeNode newNode = new PhyloTreeNode(this, parentNode, new Haplogroup(currentXMLElement.getAttribute("name").getValue()));
		parentNode.addSubHaplogroup(newNode);
		// Update index
		haplogroupLookup.put(newNode.getHaplogroup(), newNode);
//System.out.print(" " + newNode.getHaplogroup() + " ");
		
		List<Element> polys = currentXMLElement.getChild("details").getChildren("poly");
		for (Element currentPolyElement : polys) {
			Polymorphism newExpectedPoly = new Polymorphism(currentPolyElement.getValue());
			newNode.addExpectedPoly(newExpectedPoly);
		}
		List<Element> children = currentXMLElement.getChildren("haplogroup");
		for (Element currentChildElement : children) {
			buildPhylotree(newNode, currentChildElement);
			}
	}

	/**
	 * Starts haplogroup search
	 * 
	 * @param testSample
	 *            The test sample a haplogroup should be detected
	 * @param rankingMethodToUse
	 *            The ranking method that should be used
	 * @return A sorted (ranked) list of results
	 */
	public List<RankedResult> search(TestSample testSample, RankingMethod rankingMethodToUse) {
		// Start first search step
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();

		// Start at root node
		SearchResult rootResult = new SearchResult(root, testSample);
		// First call to RECURSIVE search function
		
		searchPhylotree(root, results, testSample, rootResult);
		
		rankingMethodToUse.setResults(testSample, results);

		// set results to null (>20) to save memory.
		results.clear();
		return rankingMethodToUse.getResults();
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
	 */
	private void searchPhylotree(PhyloTreeNode parent, ArrayList<SearchResult> results, TestSample sample, SearchResult parentResult) {
		// Query all child haplogroup nodes
		List<PhyloTreeNode> children = parent.getSubHaplogroups();

		for (PhyloTreeNode currentElement : children) {
			SearchResult newResult = new SearchResult(currentElement, parentResult);

			List<Polymorphism> polys = currentElement.getExpectedPolys();
		
			// Check all expected polys of the current haplogroup
		
			for (Polymorphism currentPoly : polys) {
		
				// Check whether polymorphism is in range
		
				
				if (sample.getSample().getSampleRanges().contains(currentPoly)) {
					// In case of a backmutation we have to correct the current
					// result since a polymorphism is no longer expected
					if (currentPoly.isBackMutation()) {
						newResult.removeExpectedPolyWeight(currentPoly);
						newResult.removeFoundPolyWeight(currentPoly, sample.getSample());	
					}

					// The sample contains the right polymorphism for this group
					else if (newResult.getSample().contains(currentPoly)==1) {
						newResult.addExpectedPolyWeight(currentPoly);
						newResult.addFoundPolyWeight(currentPoly);
					}
					// The sample contains a heteroplasmy for this position for this group
					else if (newResult.getSample().contains(currentPoly)==2) {
						currentPoly.setHeteroplasmy(true);
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

				// Polymorphism is not in sample range
				else
					newResult.addMissingOutOfRangeWeight(currentPoly);

			}

			// Add new result to the list of all results
			results.add(newResult);
			// RECURSIVE call
	
			searchPhylotree(currentElement, results, sample, newResult);
		
		}
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
	 */
	private void getAllHaplogroups(PhyloTreeNode parent, ArrayList<SearchResult> results,  SearchResult parentResult, PrintWriter fileHSD) {
		// Query all child haplogroup nodes
		List<PhyloTreeNode> children = parent.getSubHaplogroups();
	 
	StringBuffer sb = new StringBuffer();
			
		for (PhyloTreeNode currentElement : children) {
			SearchResult newResult = new SearchResult(currentElement, parentResult);

			List<Polymorphism> polys = currentElement.getExpectedPolys();
		//System.out.println("HG_" + currentElement.getHaplogroup() +  "\t 1-16569\t" + currentElement.getHaplogroup()+"\t"+ newResult.getDetailedResult().getExpectedPolys().toString().replace(",","\t").replace("[", "").replace("]", "").replace(" ", "")+"");
			// Check all expected polys of the current haplogroup
			fileHSD.printf("" + currentElement.getHaplogroup() +  "\t 1-16569\t" + currentElement.getHaplogroup()+"\t"+ newResult.getDetailedResult().getExpectedPolys().toString().replace(",","\t").replace("[", "").replace("]", "").replace(" ", "")+"\n" );
			// Add new result to the list of all results
			results.add(newResult);
			// RECURSIVE call
	
			getAllHaplogroups(currentElement, results,  newResult, fileHSD);
			fileHSD.printf("ENDE " +  currentElement.getHaplogroup());
		}
		

	}
	
	

	/**
	 * Parses the pyhlo weights given by a file. Sets weights for all
	 * polymorphismn
	 * 
	 * @param inStreamPhyloWeightsFile
	 *            The stream of the file with the phylo genetic weights
	 * @throws IOException
	 * @throws InvalidBaseException
	 * @throws InvalidFormatException
	 */

	public void setPolygeneticWeights(InputStream inStreamPhyloWeightsFile) throws IOException, InvalidBaseException {

		// Read in the fluctuation rates
		BufferedReader flucFile = new BufferedReader(new InputStreamReader(inStreamPhyloWeightsFile));
		String line = flucFile.readLine();

		// Read-in each line
		while (line != null) {
			StringTokenizer mainTokenizer = new StringTokenizer(line, "\t");

			String polyString = mainTokenizer.nextToken();
			double phyloGeneticWeight = Double.parseDouble(mainTokenizer.nextToken());

			// TODO remove with fixed phylotree 8 BUG 2232.12A
			try {
				phyloGeneticWeights.put(new Polymorphism(polyString), phyloGeneticWeight);
			} catch (Exception e) {
				// TODO: handle exception
			}
			line = flucFile.readLine();
		}

	}

	/**
	 * Returns the phylogenetic weight of a given polymorphism.
	 * 
	 * @param polyToCheck
	 *            The input polymorphism
	 * @return The phylogenetic weight of the polymorphism
	 */
	public double getMutationRate(Polymorphism polyToCheck) {
		if (phyloGeneticWeights.containsKey(polyToCheck))
			return phyloGeneticWeights.get(polyToCheck);

		else
			return 0;

	}

	/**
	 * @return A reference to this phylotree instance
	 */
	public PhyloTreeNode getPhyloTree() {
		return root;
	}

	/**
	 * Checks the phylotree if a haplogroup is super group of another
	 * @param superGroup The super haplogroup
	 * @param hgToCheck The haplogroup to check
	 * @return True if superGroup is a super haplogroup of hgToCheck, false otherwise
	 */
	public boolean isSuperHaplogroup(Haplogroup superGroup, Haplogroup hgToCheck) {
		PhyloTreeNode currentNode = haplogroupLookup.get(superGroup);
		if (superGroup == null)
			return false;

		while (currentNode != null) {
			if (currentNode.getHaplogroup().equals(hgToCheck))
				return true;

			currentNode = currentNode.getParent();
		}
		return false;
	}

	public int distanceToSuperHaplogroup(Haplogroup superGroup, Haplogroup hgToCheck) {
		PhyloTreeNode currentNode = haplogroupLookup.get(superGroup);
		int distance = 0;
		if (superGroup == null)
			return -1;

		while (currentNode != null) {
			if (currentNode.getHaplogroup().equals(hgToCheck))
				return distance;

			currentNode = currentNode.getParent();
			distance++;
		}
		return -1;
	}

	public int getDistanceBetweenHaplogroups( Haplogroup hgToCheck1, Haplogroup hgToCheck2) {
	
		int distance = -1;
		HashSet<Haplogroup> markedHaplogroups = new HashSet<Haplogroup>();
		
		boolean complete = false;
		PhyloTreeNode c1 = haplogroupLookup.get(hgToCheck1);
		PhyloTreeNode c2 = haplogroupLookup.get(hgToCheck2);
		
		while(!complete){
			if(c1 != null)
					if(!markedHaplogroups.contains(c1.getHaplogroup())){
					markedHaplogroups.add(c1.getHaplogroup());
					c1 = c1.getParent();
					distance++;
				}
				else{
					complete = true;
					break;
				}
			if(c2 != null)
				if(!markedHaplogroups.contains(c2.getHaplogroup())){
					markedHaplogroups.add(c2.getHaplogroup());
					c2 = c2.getParent();
					distance++;
				}
				else{
					complete = true;
					break;
				}
		}
		return distance;
	}

}