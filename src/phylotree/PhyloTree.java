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

import com.sun.media.sound.InvalidFormatException;

import core.Haplogroup;
import core.Polymorphism;
import core.TestSample;
import exceptions.parse.sample.InvalidBaseException;
import exceptions.parse.sample.InvalidPolymorphismException;

import search.ClusteredSearchResult;
import search.PhyloTreeNode;
import search.SearchResultPerNode;
import search.SearchResult;
import search.SearchResultHamming;

public class PhyloTree {
	String name;
	PhyloTreeNode root;	
	HashMap<String, Double>  phyloGeneticWeights = new HashMap<String, Double>();

	public PhyloTree(String phylotree, String phyloWeights) {
		SAXBuilder builder = new SAXBuilder();
			//for CLAP protocol:
			InputStream phyloTreeFileStream = this.getClass().getClassLoader().getResourceAsStream(phylotree);
			InputStream polyGeneticWeightsFileStream = this.getClass().getClassLoader().getResourceAsStream(phyloWeights);
			try {
				Document phyloTreeDoc = builder.build(phyloTreeFileStream);
				buildPhylotree(root,phyloTreeDoc.getRootElement());		
				// parses and sets the polygenetic weights
				setPolygeneticWeights(polyGeneticWeightsFileStream);
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidBaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
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
		
		List<Element> children = (List<Element>) currentXMLElement.getChildren("haplogroup");

		for (Element currentChildElement : children) {		
			List<Element> polys = currentXMLElement.getChild("details").getChildren("poly");
			for (Element currentPolyElement : polys) {
				Polymorphism newExpectedPoly = new Polymorphism(currentPolyElement.getValue());
				newNode.addExpectedPoly(newExpectedPoly,getPhylogeneticWeight(newExpectedPoly));		
			}
			buildPhylotree(newNode, currentChildElement);
		}
	}
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
		System.out.println(line);
		//Read-in each line
		int i=0;
		while(line != null)
		{
			StringTokenizer mainTokenizer = new StringTokenizer(line,"\t");
			
			String polyString = mainTokenizer.nextToken();
			double phyloGeneticWeight = Double.parseDouble(mainTokenizer.nextToken());
			
			
			Polymorphism poly;
			//TODO Which bug?? is this neccessary??
			//TODO remove with fixed phylotree 8 BUG 2232.12A
			try {
				poly = new Polymorphism(polyString);
				phyloGeneticWeights.put(poly.toString(), phyloGeneticWeight);
				
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			line = flucFile.readLine();
			}
		
	}
	
	public double getPhylogeneticWeight(Polymorphism polyToCheck)
	{
		if(phyloGeneticWeights.containsKey(polyToCheck))
			return phyloGeneticWeights.get(polyToCheck);
		
		else
			return 0;

	}
	
	public List<ClusteredSearchResult> search(TestSample testSample) throws JDOMException, IOException, NumberFormatException, InvalidPolymorphismException {

		// Remove all polymorphisms which don`t appear in the phylo tree (e.g
		// unstable ones...)
		//testSample.getSample().filter(searchManager.getAllPolysUsedInPhylotree());

		// Start first search step
		ArrayList<SearchResult> results = searchPhylotreeWrapper(testSample);

		// Cluster search results with same rank together
		ArrayList<ClusteredSearchResult> clusteredResult = ClusteredSearchResult.createClusteredSearchResult(results,testSample.getExpectedHaplogroup());
		
		//set results to null (>20) to save memory. 
		results.clear();
		return clusteredResult;
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
		ArrayList<SearchResultPerNode> results = new ArrayList<SearchResultPerNode>();
		searchPhylotree(this.root, results, sample);

		
		return results;
	}

	private void searchPhylotree(PhyloTreeNode parent, ArrayList<SearchResultPerNode> results, TestSample sample){
		// First call to RECURSIVE search function
		searchPhylotree(this.root,new SearchResultPerNode(), results, sample);
	}
	/**
	 * Traverses the complete phylo tree beginning at the phylo tree root. For each child a
	 * new SerachResult object is created.
	 * 
	 * @param parent
	 *            
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
	private void searchPhylotree(PhyloTreeNode parent,SearchResultPerNode searchParent, ArrayList<SearchResultPerNode> results, TestSample sample){
		
		// Query all child haplogroup nodes
		List<PhyloTreeNode> allSubHaplogroups = parent.getSubHaplogroups();

		for (PhyloTreeNode currentPhyloTreeNode : allSubHaplogroups) {

			SearchResultPerNode searchResult = new SearchResultPerNode(searchParent);
			// Check all expected polys of the current haplogroup
			for (Polymorphism currentExpectedPoly : currentPhyloTreeNode.getExpectedPolys()) {		
				// Check whether polymorphism is in range
				if (sample.getSampleRanges().contains(currentExpectedPoly)) {
					//Poly is in range, thus we expect it to appear in the sample
					searchResult.addExpectedPhyloWeight(getPhylogeneticWeight(currentExpectedPoly));		
					
					// In case of a backmutation we must correct the current
					// result since a polymorhism is no longer expected
					if (currentExpectedPoly.isBackMutation()) {
						//TODO: Change the smample.contains function to check for backmutations
						Polymorphism newPoly = new Polymorphism(currentExpectedPoly);
						newPoly.setBackMutation(false);
						
						//The poly has back mutated. It's no longer expected to appear in sample
						searchResult.removeExpectedPhyloWeight(getPhylogeneticWeight(newPoly));

						//The poly does not appear in sample. We found a correct back mutation 
						if (!sample.getSample().contains(newPoly))
						{
							searchResult.addCorrectPhyloWeight(getPhylogeneticWeight(currentExpectedPoly));
						}
						//The poly has not back mutated (it appears in the sample). Remove the correct status
						//caused by earlier haplogroups
						else
							searchResult.removeCorrectPolyWeight(getPhylogeneticWeight(newPoly));	
					}

					// The sample contains the right polymorphism for this group
					else if (sample.getSample().contains(currentExpectedPoly)) {
						searchResult.addCorrectPhyloWeight(getPhylogeneticWeight(currentExpectedPoly));
					}	
				}
				
				//Polymorphism is not in sample range
				else{
					searchResult.addNotInRangePhyloWeight(getPhylogeneticWeight(currentExpectedPoly));	
				}
			}
			
			results.add(searchResult);
			// RECURSIVE call
			searchPhylotree(currentPhyloTreeNode,searchResult, results, sample);
		}
	}
}
