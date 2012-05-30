package phylotree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.sun.media.sound.InvalidFormatException;

import core.Polymorphism;
import exceptions.parse.sample.InvalidBaseException;

import search.PhyloTreeNode;

public class PhyloTree {
	String name;
	PhyloTreeNode root;
	

	public PhyloTree(String phylotree, String phyloWeights) {
		SAXBuilder builder = new SAXBuilder();
			//for CLAP protocol:
			InputStream phyloFile = this.getClass().getClassLoader().getResourceAsStream(phylotree);
			InputStream flucRates = this.getClass().getClassLoader().getResourceAsStream(phyloWeights);
			try {
				Document phyloTreeDoc = builder.build(phyloFile);
				buildPhylotree(phyloTreeDoc);
				
				// parses and sets the polygenetic weights
				setPolygeneticWeights(flucRates);
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidBaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
}
