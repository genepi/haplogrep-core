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
				newNode.addExpectedPoly(new Polymorphism(currentPolyElement.getValue()));
				buildPhylotree(newNode, currentChildElement);
			}
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
		ArrayList<SearchResult> results = new ArrayList<SearchResult>();

		// Start at root node
		SearchResult rootResult = new SearchResult(this.root,this, sample);
//		Element node = searchManager.getPhyloTree().getRootElement();

		// First call to RECURSIVE search function
		searchPhylotree(this.root, results, sample, rootResult);

		return results;
	}

	/**
	 * Traverses the complete phylo tree beginning at the rCRS. For each child a
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
	private void searchPhylotree(PhyloTreeNode parent, ArrayList<SearchResult> results, TestSample sample, SearchResult parentResult) throws NumberFormatException,
	InvalidPolymorphismException {
		// Query all child haplogroup nodes
		List<PhyloTreeNode> children = parent.getSubHaplogroups();

		for (PhyloTreeNode currentElement : children) {
			SearchResult newResult = new SearchResult(currentElement,parentResult);

			List<Element> polys = currentElement.getChild("details").getChildren("poly");
			//H2a2a has no polys
			//if(polys.size() > 0){
			SearchResultPerNode newNode = new SearchResultPerNode(null,new Haplogroup(currentElement.getAttributeValue("name")));
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
