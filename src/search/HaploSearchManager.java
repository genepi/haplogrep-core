//package search;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.StringTokenizer;
//
//import org.jdom.Document;
//import org.jdom.Element;
//import org.jdom.JDOMException;
//import org.jdom.input.SAXBuilder;
//import org.jdom.xpath.XPath;
//
//import com.sun.media.sound.InvalidFormatException;
//
//import core.Haplogroup;
//import core.Polymorphism;
//import exceptions.PolyDoesNotExistException;
//import exceptions.parse.sample.InvalidBaseException;
//
//public final class HaploSearchManager {
//
//	
//	private ArrayList<Polymorphism> allPolysUsedinPhylotree = null;
//	private Document phyloTree= null;
//	private String phylotreeString;
//	private String fluctRates;
//	
//	/**
//	 * @param phylotree Name of xml file which contains the phylotree
//	 * @param polyWeights Name of file with polygenetic weights
//	 */
//	public HaploSearchManager(String phylotree, String polyWeights)
//	{
//		this.phylotreeString=phylotree;
//		allPolysUsedinPhylotree = new ArrayList<Polymorphism>();
//			  
//		// Create a JDOM document out of the phylotree XML
//			SAXBuilder builder = new SAXBuilder();
//			try {
//				//for CLAP protocol:
//				InputStream phyloFile = this.getClass().getClassLoader().getResourceAsStream(phylotree);
//				InputStream flucRates = this.getClass().getClassLoader().getResourceAsStream(polyWeights);
//				phyloTree = builder.build(phyloFile);
//				// parses and sets the polygenetic weights
//				setPolygeneticWeights(flucRates);
//				
//				
//			} catch (JDOMException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (NumberFormatException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (InvalidBaseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
//		
//	}
//	
//
//	/**
//	 * Traverses the entire phylo tree and saves all appearing phylo types
//	 * @throws JDOMException
//	 * @throws InvalidBaseException
//	 * @throws InvalidFormatException
//	 */
//	/*private void extractAllPolysFromPhylotree() throws JDOMException, InvalidPolymorphismException, InvalidFormatException {
//		List<Element>  nameList = XPath.selectNodes( phyloTree, "//poly" );
//		int i=0;
//		for ( Element a : nameList ) 
//		{ 
//			if(i++<20)
//				System.out.println(a.getValue());
//			allPolysUsedinPhylotree.add(new Polymorphism(a.getValue()));
//		}
//	}*/
//	
//	
//	
//	/**
//	 * Searches and renames a certain haplogroup in the XML tree
//	 * @param oldName old name
//	 * @param newName new name
//	 * @throws JDOMException
//	 */
//
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
//
//	public List<Element> getPolysOfHg(Haplogroup hg) throws JDOMException {
//		Element titleNode =  (Element) XPath.selectSingleNode( phyloTree, "//haplogroup[@name=\""+ hg.toString()+"\"]/details");
//		
//		List<Element> e = titleNode.getChildren("poly");
//		return e;
//	}
//
//	/*
//	public final HaploSearch createNewSearch()
//	{
//		return new HaploSearch(instance);
//	}*/
//	/*
//	public static HaploSearchManager getInstance() 
//    {
//        return new HaploSearchManager();
//    }*/
//
//	public Document getPhyloTree() {
//		return phyloTree;
//	}
//	
//	public void setPhyloTree(Document phylotree) {
//		this.phyloTree= phylotree;
//	}
//
//	public void setPhylotreeString(String phylotree) {
//		this.phylotreeString = phylotree;
//	}
//
//
//	public String getPhylotreeString() {
//		return phylotreeString;
//	}
//
//
//	public void setFluctRates(String fluctRates) {
//		this.fluctRates = fluctRates;
//	}
//
//
//	public String getFluctRates() {
//		return fluctRates;
//	}
//	
//}