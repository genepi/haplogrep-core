package dataVisualizers;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.fop.svg.AbstractFOPTranscoder;
import org.apache.fop.svg.PDFTranscoder;
import org.jdom.Document;
import org.jdom.Element;
import org.w3c.dom.DOMImplementation;

/**
 * Renders an overview tree image given by an XML root node.
 * 
 * @author Dominic Pacher, Sebastian Schšnherr, Hansi Weissensteiner
 * 
 */
public class PhylotreeRenderer {
	private Font polymprhismnFont = null;
	private Font sampleIDFont = null;
	private Font haplogroupFont = null;
	
	private Document xmlPhyloTree = null;
	private BufferedImage watermark = null;
	private final int linePadding = 2;
	
	private float dpi = 72;
	
	
	/**
	 * Creates a new PhyloTreeRenderer instance with a xml document 
	 * @param xmlPhyloTree representing the tree to render as xml document
	 */
	public PhylotreeRenderer(Document xmlPhyloTree)
	{
		this.xmlPhyloTree = xmlPhyloTree;
		
		polymprhismnFont = new Font("Arial",Font.PLAIN,12);
		sampleIDFont = new Font("Arial",Font.PLAIN,16);
		haplogroupFont = new Font("Arial",Font.BOLD,14);
	}
	
	/**
	 * Sets a watermark image 
	 * @param path The path to the watermark image file
	 * @throws IOException	Throw if the file is not found etc..
	 */
	public void setWatermark(URL path) throws IOException{
		watermark = ImageIO.read(path);
	}
	
	/**
	 * @return the set DPI of the renderer
	 */
	public float getDpi() {
		return dpi;
	}

	/**
	 * Sets the DPI of the image
	 * @param dpi The new DPI value
	 */
	public void setDpi(float dpi) {
		this.dpi = dpi;
	}

	/**
	 * @param format The format of image as string ('png' or 'svg')
	 * @param path The path the created image should be saved to 
	 * @param includeHotspots	True if hotspots should be include, false otherwise
	 * @return a file handle of the created file
	 */
	public File createImage(String format, String path, boolean includeHotspots){

		File newImage = null;

		try {
			 newImage = renderImage(format,path,includeHotspots);
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newImage;
	}
	
	//TODO The tree should be as compact as possible
	//renders the image...not finished...subject to change....
	private File renderImage(String format, String path, boolean includeHotspots) throws Exception {

		// Get a DOMImplementation.
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

		// Create an instance of org.w3c.dom.Document.
		String svgNS = "http://www.w3.org/2000/svg";
		org.w3c.dom.Document document = domImpl.createDocument(svgNS, "svg", null);

		SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
		ctx.setEmbeddedFontsOn(true);
		// Create an instance of the SVG Generator.
		SVGGraphics2D svgGraphics2D = new SVGGraphics2D(document);

		Graphics2D g2 = svgGraphics2D;// newImage.getGraphics();
		RecData r = traverseTree(g2, xmlPhyloTree.getRootElement(), 0, new RecData(0, 20, 0, 0), 0);

		int imageWidth = r.getCurrentPos() + r.getMaxWidth() / 2;

		if (imageWidth < 300)
			imageWidth = 300;

		int treeHeight = r.getMaxHeight() + 50;
		int imageHeight = treeHeight + g2.getFontMetrics().getHeight() * 6 + 10;

		document = domImpl.createDocument(svgNS, "svg", null);

		ctx = SVGGeneratorContext.createDefault(document);
		ctx.setEmbeddedFontsOn(true);
		// Create an instance of the SVG Generator.
		svgGraphics2D = new SVGGraphics2D(document);
		g2 = svgGraphics2D;// newImage.getGraphics();

		g2.setBackground(Color.white);

		svgGraphics2D.setSVGCanvasSize(new Dimension(imageWidth, imageHeight));
		g2.clearRect(0, 0, imageWidth, imageHeight);

		if (imageWidth == 300) {
			int treeWidth = r.getCurrentPos() + r.getMaxWidth() / 2;
			r = traverseTree(g2, xmlPhyloTree.getRootElement(), 0, new RecData(0, 20 + treeWidth / 2, 0, 0), treeHeight);
		} else
			r = traverseTree(g2, xmlPhyloTree.getRootElement(), 0, new RecData(0, 20, 0, 0), treeHeight);

		g2.setFont(haplogroupFont);

		int boxWidth = g2.getFontMetrics().stringWidth("@ = assumed back mutation") + 20;

		g2.drawString("KEY", 30, treeHeight + g2.getFontMetrics().getHeight());

		int boxY = treeHeight + g2.getFontMetrics().getHeight() * 2;
		if (includeHotspots) {
			g2.setColor(new Color(153, 204, 153));
			g2.drawString("Hotspot", 30, boxY);
			boxY += g2.getFontMetrics().getHeight();
		}

		g2.setColor(new Color(50, 180, 227));
		g2.drawString("Local private mutation", 30, boxY);
		boxY += g2.getFontMetrics().getHeight();
		g2.setColor(Color.red);
		g2.drawString("Global private mutation", 30, boxY);
		boxY += g2.getFontMetrics().getHeight();
		g2.drawString("@ = assumed back mutation", 30, boxY);
		boxY += g2.getFontMetrics().getHeight();
		g2.setColor(Color.black);
		g2.drawString("mis = missing mutation", 30, boxY);
		// boxY += g2.getFontMetrics().getHeight();

		g2.setColor(new Color(0, 0, 0));
		g2.draw3DRect(20, treeHeight, boxWidth, boxY + 5 - treeHeight, true);

		if (watermark != null && imageWidth - boxWidth > watermark.getWidth())
			g2.drawImage(watermark, imageWidth - watermark.getWidth(), imageHeight - watermark.getHeight(), null);

		if (format.equals("SVG")) {
			File resultFile = new File(path);
			FileOutputStream outFile = new FileOutputStream(resultFile);
			Writer out = new OutputStreamWriter(outFile, "UTF-8");
			svgGraphics2D.stream(out, true);

			return resultFile;
		} else if (format.equals("PDF")) {
			PDFTranscoder transcoder = new PDFTranscoder();
			ByteArrayOutputStream outb = new ByteArrayOutputStream();
			Writer out = new OutputStreamWriter(outb, "UTF-8");
			svgGraphics2D.stream(out, true);

			TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(outb.toByteArray()));// (new
																										// File("v.svg").toURL().toString());

			File resultFile = new File(path);
			FileOutputStream outFile = new FileOutputStream(resultFile);

			TranscoderOutput output = new TranscoderOutput(outFile);

			transcoder.addTranscodingHint(AbstractFOPTranscoder.KEY_STROKE_TEXT, new Boolean(false));

			transcoder.transcode(input, output);

			return resultFile;
		}

		else
			return rescale( svgGraphics2D, r.getCurrentPos() + r.getMaxWidth() / 2, r.getMaxHeight() + g2.getFontMetrics().getHeight() * 4, path);
	}
	
	/**
	 * Rescales a svg graphic to a match a given DPI and rasters the image to png
	 * @param svgGraphics2D The svg graphics context
	 * @param widht	width of the image 
	 * @param height height of the image
	 * @param pathToSaveFile The path the new iamge should be save to
	 * @return	A handle to the image file
	 */
	private File rescale( SVGGraphics2D svgGraphics2D, int widht, int height, String pathToSaveFile) {
		boolean useCSS = true; // we want to use CSS style attributes
		Writer out;

		try {
			ByteArrayOutputStream outb = new ByteArrayOutputStream();
			out = new OutputStreamWriter(outb, "UTF-8");

			svgGraphics2D.stream(out, useCSS);

			// Create a PNG transcoder
			PNGTranscoder transcoder = new PNGTranscoder();

			// Create the transcoder input

			TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(outb.toByteArray()));

			File resultFile = new File(pathToSaveFile);
			FileOutputStream outFile = new FileOutputStream(resultFile);

			TranscoderOutput output = new TranscoderOutput(outFile);
			transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, (float) (25.4 / 150.f));
			transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) ((widht) * dpi / 72.0) );
			transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) ((height) * dpi / 72.0));

			// Transform the svg document into a PNG image
			transcoder.transcode(input, output);

			return resultFile;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SVGGraphics2DIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (TranscoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	//traverses tree and renderes image using the graphics context..also subject to change
	private RecData traverseTree(Graphics2D g2d, Element result, int depth,RecData recData, int treeHeight) throws Exception {
		
		int numPolys =  result.getChildren("Poly").size();
		int oldDepth = depth;
		
		g2d.setFont(polymprhismnFont);
		
		
		
			
		List<Element> list = result.getChildren("TreeNode");
		
				
		if (result.getChildren("TreeNode").size() > 0) {
			
			int maxPolyWidth = getMaxStringWidthPolys(g2d,result.getChildren("Poly"));
			if(recData.getMaxWidth() < maxPolyWidth)
				recData.setMaxWidth(maxPolyWidth);
			
			
			
			depth+= (1+numPolys) * (g2d.getFontMetrics().getHeight()+linePadding) + 15;
			String haplogroupName = result.getAttributeValue("name").toString();
			
			g2d.setFont(haplogroupFont);
			int maxHaplogroupWidth = g2d.getFontMetrics().stringWidth(haplogroupName)+5;
			
			if(recData.getMaxWidth() < maxHaplogroupWidth)
				recData.setMaxWidth(maxHaplogroupWidth);
			g2d.setFont(polymprhismnFont);
			
			RecData rNodeData = null;
			RecData lNodeData = null;
			
			ArrayList<Element> l1 = new ArrayList<Element>();
			ArrayList<Element> l2 = new ArrayList<Element>();
			for(Element currentElement : list){
				if(currentElement.getAttributeValue("type").equals("Haplogroup"))
					l1.add(currentElement);
				else
					l2.add(currentElement);
					
			}
			
			list.clear();
			list.addAll(l1);
			list.addAll(l2);
			
			int maxdepth = 0;
			
			for (int i = 0; i < list.size();i++){
				
				if( list.size() > 1){
					recData.setMaxWidth(0);
					recData = traverseTree(g2d, list.get(i), depth,recData,treeHeight);	}
				
				else{
					
					recData = traverseTree(g2d, list.get(i), depth,recData,treeHeight);	}
				
				if(maxdepth < recData.getMaxHeight())
					maxdepth = recData.getMaxHeight();
				
				if(i == 0)
				{				
					lNodeData = recData;
				}
				
				if(i == list.size()-1)
				{				
					rNodeData = recData;
				}
			}
			
			//Calculate the x position of the supernode (average of child node position)
			int superNodePosXCentered = lNodeData.getCenter() + (rNodeData.getCenter()-lNodeData.getCenter())/2;
			
			//Draw horizontal line
			g2d.drawLine(lNodeData.getCenter(), depth  ,rNodeData.getCenter(), depth);
			
			//Draw vertical line
			g2d.drawLine(superNodePosXCentered, depth -g2d.getFontMetrics().getHeight(),superNodePosXCentered, oldDepth);
			
			//Draw all polymorphisms
			drawPolymorhismn(g2d, result,superNodePosXCentered, depth);
			depth += 5;
			
			//Draw haplogroup
			drawHaplogroupNode(g2d, superNodePosXCentered,depth, haplogroupName);	
				
			//attach the end node with unused polys and sampleId 		
			//if(result.getChild("DetailedResults") != null)
			//drawEndNode(g2d, result.getChild("DetailedResults"), superNodePosXCentered,depth);
			
			
			
			return new RecData(superNodePosXCentered,recData.getCurrentPos(),recData.getMaxWidth(), maxdepth);
		}
		
	
		//list = result.getChildren("DetailedResults");
		
		//if(list.size() == 1){
			
		// s}
		
		else{
			int widthHgLabel = g2d.getFontMetrics().getHeight();
			
			int maxUnused = getMaxStringWidthUnusedPolys(g2d,result);
			
			if(recData.getMaxWidth() < maxUnused)
				recData.setMaxWidth(maxUnused);
			
			int delta = (recData.getMaxWidth() /*- (widthHgLabel) */);
			//Calc labels start and end position an save center pos
			int left = recData.getCurrentPos();
			int right = 0;
			
			if(delta < widthHgLabel)
				right = recData.getCurrentPos() + widthHgLabel + 10;
			else
				right = recData.getCurrentPos()  + delta + 5;
			
			int center = left + (right-left)/2;
			
			RecData newData = new RecData(center,right,recData.getMaxWidth(),depth);
			//Draw vertical line to from up-bottom
			g2d.setColor(Color.black);
			g2d.drawLine(newData.getCenter(), oldDepth, newData.getCenter(),depth + g2d.getFontMetrics().getHeight() + 3);
			
			//Draw all polymorphisms
			//drawPolymorhismn(g2d, result, newData.getCenter(), depth);
					
			//Draw haplogroup
			//drawHaplogroupNode(g2d, newData.getCenter(),depth, haplogroupName);		
				
			//attach the end node with unused polys and sampleId 		
			//int y = drawEndNode(g2d, result, newData.getCenter(),depth);
			int y = drawEndNode(g2d, result, newData.getCenter(),depth,treeHeight);
			newData.setMaxHeight(y);
			
			return newData;
		}
			
		//throw new Exception("Invalid data format!");
	}

	/**
	 * Returns the max width of a string in a polymorphisms column
	 * @param g2d The graphics context
	 * @param polys The list of polymorphisms
	 * @return The max width of the polymorphims' strings
	 */
	private int getMaxStringWidthPolys(Graphics2D g2d, List<Element> polys) {
		int max = 0;
		int width = 0;
		for (Element currentPoly : (List<Element>) polys) {
			width = g2d.getFontMetrics().stringWidth(currentPoly.getText());
			if (max < width) {
				max = width;
			}
		}
		return width;
	}

	private int getMaxStringWidthUnusedPolys(Graphics2D g2d, Element child) {
		int max = 0;
		int width = 0;
		g2d.setFont(polymprhismnFont);
		for (Element currentPoly : (List<Element>) child.getChild("DetailedResults").getChildren("DetailedResult")) {
			width = g2d.getFontMetrics().stringWidth(currentPoly.getChildText("unused"));
			if (max < width) {
				max = width;
			}

		}
		return max;
	}
	
	/**
	 * @param g2d
	 * @param child
	 * @param center
	 * @param depth
	 * @param treeHeight
	 * @return
	 */
	private int drawEndNode(Graphics2D g2d, Element child, int center, int depth, int treeHeight) {

		g2d.drawLine(center, depth, center, treeHeight - 15);

		g2d.setFont(polymprhismnFont);
		depth += 10;

			
		for (Element currentPoly : (List<Element>) child.getChild("DetailedResults").getChildren("DetailedResult")) {

			depth += g2d.getFontMetrics().getHeight() + linePadding;
			;
			if (currentPoly.getChildText("reasonUnused").equals("hotspot")) {
				g2d.setColor(new Color(153, 204, 153));
			}
			if (currentPoly.getChildText("reasonUnused").equals("globalPrivateMutation")) {
				g2d.setColor(Color.red);
			}
			if (currentPoly.getChildText("reasonUnused").equals("localPrivateMutation")) {
				g2d.setColor(new Color(50, 180, 227));
			}

			drawCenteredNode(g2d, center, depth, currentPoly.getChildText("unused"));

		}

		drawSampleIDNode(g2d, child.getAttributeValue("id"), center, treeHeight);

		g2d.setFont(sampleIDFont);
		return depth + g2d.getFontMetrics().stringWidth(child.getAttributeValue("id")) + 20;

	}



	private void drawHaplogroupNode(Graphics2D g2d, int x, int y, String haplogroupName) {
		g2d.setFont(haplogroupFont);
		g2d.setColor(Color.black);
		drawCenteredNode(g2d, x, y, haplogroupName);
	}

	private void drawPolymorhismn(Graphics2D g2d, Element currentNode, int x, int y) {

		g2d.setFont(polymprhismnFont);
		g2d.setColor(Color.black);
		y -= 5 + (currentNode.getChildren("Poly").size() * (g2d.getFontMetrics().getHeight() + linePadding));

		for (Element currentPoly : (List<Element>) currentNode.getChildren("Poly")) {

			drawCenteredNode(g2d, x, y, currentPoly.getText());
			y += g2d.getFontMetrics().getHeight() + linePadding;
		}

	}

	private void drawSampleIDNode(Graphics2D g2d, String sampleIDText, int x, int y) {

		g2d.setFont(sampleIDFont);
		g2d.setColor(Color.black);
		int widthSampleID = g2d.getFontMetrics().stringWidth(sampleIDText);

		y -= widthSampleID + 15;

		g2d.drawLine(x, y, x, y + 4);
		g2d.translate(x - 6, y + 5);
		g2d.rotate(Math.toRadians(90));
		g2d.clearRect(0, -15, widthSampleID + 5, 19);
		g2d.drawString(sampleIDText, 2, 0);
		g2d.rotate(Math.toRadians(-90));
		g2d.translate(-(x - 6), -(y + 5));
	}

	private void drawCenteredNode(Graphics2D g2d, int x, int y, String text) {

		int stringWidth = g2d.getFontMetrics().stringWidth(text);

		// Clear background
		g2d.clearRect(x - stringWidth / 2, y - (int) (g2d.getFontMetrics().getHeight() * 1.5), stringWidth, g2d.getFontMetrics().getHeight() + 1);
		g2d.drawString(text, x - stringWidth / 2, y - g2d.getFontMetrics().getHeight() / 2);
	}
}
