package search;

import exceptions.PolyDoesNotExistException;
import exceptions.parse.sample.InvalidBaseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import com.sun.media.sound.InvalidFormatException;

import core.Haplogroup;
import core.Polymorphism;

public final class HaploSearchManager {

	
	private ArrayList<Polymorphism> allPolysUsedinPhylotree = null;
	private Document phyloTree= null;
	private String phylotreeString;
	private String fluctRates;
	
	public HaploSearchManager(String phylotree, String weights)
	{
		this.phylotreeString=phylotree;
		allPolysUsedinPhylotree = new ArrayList<Polymorphism>();
			  
		// Create a JDOM document out of the phylotree XML
			SAXBuilder builder = new SAXBuilder();
			try {
				//for CLAP protocol:
				InputStream phyloFile = this.getClass().getClassLoader().getResourceAsStream(phylotree);
				InputStream flucRates = this.getClass().getClassLoader().getResourceAsStream(weights);
				phyloTree = builder.build(phyloFile);
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
		System.out.println(line);
		//Read-in each line
		int i=0;
		while(line != null)
		{
			StringTokenizer mainTokenizer = new StringTokenizer(line,"\t");
			
			String polyString = mainTokenizer.nextToken();
			double phyloGeneticWeight = Double.parseDouble(mainTokenizer.nextToken());
			
			
			Polymorphism poly;
			//TODO remove with fixed phylotree 8 BUG 2232.12A
			try {
				poly = new Polymorphism(polyString);
				Polymorphism.changePhyloGeneticWeight(poly,phylotreeString ,phyloGeneticWeight);
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

	public void changePoly(Haplogroup hg, Polymorphism polyOld,Polymorphism polyNew) throws JDOMException, PolyDoesNotExistException
	{
		List<Element> e = getPolysOfHg(hg);
		
		for (Element ce : e) {
			if (ce.getText().equals(polyOld.toString())) {
				ce.setText(polyNew.toString());
				return;
			}
		}
		
		throw new PolyDoesNotExistException();
	}

	public List<Element> getPolysOfHg(Haplogroup hg) throws JDOMException {
		Element titleNode =  (Element) XPath.selectSingleNode( phyloTree, "//haplogroup[@name=\""+ hg.toString()+"\"]/details");
		
		List<Element> e = titleNode.getChildren("poly");
		return e;
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

	public Document getPhyloTree() {
		return phyloTree;
	}
	
	public void setPhyloTree(Document phylotree) {
		this.phyloTree= phylotree;
	}

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