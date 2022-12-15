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
import java.util.Collections;
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
import org.w3c.dom.DOMImplementation;

import phylotree.Phylotree;
import core.Polymorphism;
import core.Reference;

/**
 * Renders an overview tree image given by an XML root node.
 * 
 * @author Dominic Pacher, Sebastian Schoenherr, Hansi Weissensteiner
 * 
 */
public class PhylotreeRenderer {
	private Font polymorphismFont = null;
	private Font sampleIDFont = null;
	private Font haplogroupFont = null;

	private Phylotree phyloTree = null;
	private OverviewTree xmlPhyloTree = null;
	private BufferedImage watermark = null;
	private final int linePadding = 3;

	private float dpi = 72;
	private int numEndNode = 0;
	Reference reference;

	/**
	 * Creates a new PhyloTreeRenderer instance with a xml document
	 * 
	 * @param xmlPhyloTree
	 *            representing the tree to render as xml document
	 */
	public PhylotreeRenderer(Phylotree phyloTree, OverviewTree xmlPhyloTree, Reference reference) {
		this.xmlPhyloTree = xmlPhyloTree;
		this.phyloTree = phyloTree;
		this.reference = reference;

		polymorphismFont = new Font("Arial", Font.PLAIN, 12);
		sampleIDFont = new Font("Arial", Font.PLAIN, 16);
		haplogroupFont = new Font("Arial", Font.BOLD, 14);
	}

	/**
	 * Sets a watermark image
	 * 
	 * @param path
	 *            The path to the watermark image file
	 * @throws IOException
	 *             Throw if the file is not found etc..
	 */
	public void setWatermark(URL path) throws IOException {
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
	 * 
	 * @param dpi
	 *            The new DPI value
	 */
	public void setDpi(float dpi) {
		this.dpi = dpi;
	}

	/**
	 * @param format
	 *            The format of image as string ('png' or 'svg')
	 * @param path
	 *            The path the created image should be saved to
	 * @param includeHotspots
	 *            True if hotspots should be include, false otherwise
	 * @return a file handle of the created file
	 */
	public File createImage(String format, String path, boolean includeHotspots, boolean includeAAC) {

		File newImage = null;

		try {
			newImage = renderImage(format, path, includeHotspots, includeAAC);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return newImage;
	}

	// TODO The tree should be as compact as possible
	// renders the image...not finished...subject to change....
	private File renderImage(String format, String path, boolean includeHotspots, boolean includeAAC) throws Exception {

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
		RecData r = traverseTree(g2, xmlPhyloTree.getRootNode(), 0, new RecData(0, 20, 0, 0), 0, includeAAC);

		int imageWidth = r.getCurrentPos() + r.getMaxWidth() / 2;

		if (imageWidth < 300)
			imageWidth = 300;

		int treeHeight = r.getMaxHeight() + 50;
		int imageHeight = treeHeight + g2.getFontMetrics().getHeight() * 7 + 10; // 7
																					// lines
																					// in
																					// Box

		document = domImpl.createDocument(svgNS, "svg", null);

		ctx = SVGGeneratorContext.createDefault(document);
		ctx.setEmbeddedFontsOn(true);
		// Create an instance of the SVG Generator.
		svgGraphics2D = new SVGGraphics2D(document);
		g2 = svgGraphics2D;// newImage.getGraphics();

		// VectorGraphics freehep = new SVGGraphics2D(new File(path) , new
		// Dimension(imageWidth, imageHeight));

		// freehep.startExport();
		// g2 = freehep;

		g2.setBackground(Color.white);

		svgGraphics2D.setSVGCanvasSize(new Dimension(imageWidth, imageHeight));
		g2.clearRect(0, 0, imageWidth, imageHeight);

		if (imageWidth == 300) {
			int treeWidth = r.getCurrentPos() + r.getMaxWidth() / 2;
			r = traverseTree(g2, xmlPhyloTree.getRootNode(), 0, new RecData(0, 20 + treeWidth / 2, 0, 0), treeHeight, includeAAC);
		} else
			r = traverseTree(g2, xmlPhyloTree.getRootNode(), 0, new RecData(0, 20, 0, 0), treeHeight, includeAAC);

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
		g2.setColor(Color.black);
		g2.drawString("@ = assumed back mutation", 30, boxY);
		boxY += g2.getFontMetrics().getHeight();
		g2.drawString("or missing mutation", 30 + g2.getFontMetrics().stringWidth("@ = "), boxY);
		boxY += g2.getFontMetrics().getHeight();
		g2.setColor(Color.gray);
		g2.drawString("Heteroplasmic mutation", 30, boxY);
		boxY += g2.getFontMetrics().getHeight();
		boxY += g2.getFontMetrics().getHeight();
//		g2.setColor(Color.black);
//		g2.drawString("mis = missing mutation", 30, boxY);
		// boxY += g2.getFontMetrics().getHeight();

		g2.setColor(new Color(0, 0, 0));
		g2.draw3DRect(20, treeHeight, boxWidth, boxY + 10 - treeHeight, true);

//		if (watermark != null && imageWidth - boxWidth > watermark.getWidth())
//			g2.drawImage(watermark, imageWidth - watermark.getWidth(), imageHeight - watermark.getHeight(), null);

		if (format.equals("SVG")) {

			File resultFile = new File(path);
			FileOutputStream outFile = new FileOutputStream(resultFile);
			Writer out = new OutputStreamWriter(outFile, "UTF-8");
			svgGraphics2D.stream(out, true);

			return resultFile;
		} else if (format.equals("PDF")) {
			File resultFile = new File(path);
			FileOutputStream outFile = new FileOutputStream(resultFile);
			PDFTranscoder transcoder = new PDFTranscoder();
			ByteArrayOutputStream outb = new ByteArrayOutputStream();
			Writer out = new OutputStreamWriter(outb, "UTF-8");
			svgGraphics2D.stream(out, true);
			TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(outb.toByteArray()));// (new
			TranscoderOutput output = new TranscoderOutput(outFile);
			transcoder.addTranscodingHint(AbstractFOPTranscoder.KEY_STROKE_TEXT, new Boolean(false));
			transcoder.transcode(input, output);
			return resultFile;
		} else
			return rescale(svgGraphics2D, r.getCurrentPos() + r.getMaxWidth() / 2, r.getMaxHeight() + g2.getFontMetrics().getHeight() * 4, path);
	}

	/**
	 * Rescales a svg graphic to a match a given DPI and rasters the image to
	 * png
	 * 
	 * @param svgGraphics2D
	 *            The svg graphics context
	 * @param widht
	 *            width of the image
	 * @param height
	 *            height of the image
	 * @param pathToSaveFile
	 *            The path the new iamge should be save to
	 * @return A handle to the image file
	 */
	private File rescale(SVGGraphics2D svgGraphics2D, int widht, int height, String pathToSaveFile) {
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

			dpi = 72;

			File resultFile = new File(pathToSaveFile);
			FileOutputStream outFile = new FileOutputStream(resultFile);

			TranscoderOutput output = new TranscoderOutput(outFile);
			transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER, (float) (25.4 / 150.f));
			transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) ((widht) * dpi / 72.0));
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
		} catch (TranscoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	int domi = 0;

	// traverses tree and renders image using the graphics context..also subject
	// to change
	private RecData traverseTree(Graphics2D g2d, TreeNode result, int depth, RecData recData, int treeHeight, boolean includeAAC) throws Exception {

		int oldDepth = depth;

		g2d.setFont(polymorphismFont);

		List<TreeNode> list = result.getChildren();

		if (result instanceof OverviewTreeInnerNode) {
			String s = "";
			for (int i = 0; i < treeHeight; i++) {
				s += " ";
			}

			int numPolys = ((OverviewTreeInnerNode) result).getExpectedPoly().size();
			int maxPolyWidth = getMaxStringWidthPolys(g2d, ((OverviewTreeInnerNode) result).getExpectedPoly());
			if (recData.getMaxWidth() < maxPolyWidth)
				recData.setMaxWidth(maxPolyWidth);

			depth += (1 + numPolys) * (g2d.getFontMetrics().getHeight() + linePadding) + 15;
			String haplogroupName = result.getPhyloTreeNode().getHaplogroup().toString();

			g2d.setFont(haplogroupFont);
			int maxHaplogroupWidth = g2d.getFontMetrics().stringWidth(haplogroupName) + 5;

			if (recData.getMaxWidth() < maxHaplogroupWidth)
				recData.setMaxWidth(maxHaplogroupWidth);
			g2d.setFont(polymorphismFont);

			RecData rNodeData = null;
			RecData lNodeData = null;

//			ArrayList<OverviewTreeNode> l1 = new ArrayList<OverviewTreeNode>();
//			ArrayList<OverviewTreeNode> l2 = new ArrayList<OverviewTreeNode>();
//			for(OverviewTreeNode currentElement : list){
//				if(currentElement.getAttributeValue("type").equals("Haplogroup"))
//					l1.add(currentElement);
//				else
//					l2.add(currentElement);
//					
//			}
//			
//			list.clear();
//			list.addAll(l1);
//			list.addAll(l2);
//			
			int maxdepth = 0;

			for (int i = 0; i < list.size(); i++) {

				if (list.size() > 1) {
					recData.setMaxWidth(0);
					recData = traverseTree(g2d, list.get(i), depth, recData, treeHeight, includeAAC);
				}

				else {

					recData = traverseTree(g2d, list.get(i), depth, recData, treeHeight, includeAAC);
				}

				if (maxdepth < recData.getMaxHeight())
					maxdepth = recData.getMaxHeight();

				if (i == 0) {
					lNodeData = recData;
				}

				if (i == list.size() - 1) {
					rNodeData = recData;
				}
			}

			// Calculate the x position of the supernode (average of child node
			// position)
			int superNodePosXCentered = lNodeData.getCenter() + (rNodeData.getCenter() - lNodeData.getCenter()) / 2;

			// Draw horizontal line
			g2d.drawLine(lNodeData.getCenter(), depth, rNodeData.getCenter(), depth);

			// Draw vertical line
			g2d.drawLine(superNodePosXCentered, oldDepth + 10, superNodePosXCentered, oldDepth);

			// Draw all polymorphisms
			drawPolymorhismn(g2d, (OverviewTreeInnerNode) result, superNodePosXCentered, depth);
			depth += 5;

			// Draw haplogroup
			drawHaplogroupNode(g2d, superNodePosXCentered, depth, haplogroupName);

			// attach the end node with unused polys and sampleId
			// if(result.getChild("DetailedResults") != null)
			// drawEndNode(g2d, result.getChild("DetailedResults"),
			// superNodePosXCentered,depth);

			return new RecData(superNodePosXCentered, recData.getCurrentPos(), recData.getMaxWidth(), maxdepth);
		}

		// list = result.getChildren("DetailedResults");

		// if(list.size() == 1){

		// s}

		else {
			int widthHgLabel = g2d.getFontMetrics().getHeight();

			int maxUnused = getMaxStringWidthUnusedPolys(g2d, (OverviewTreeLeafNode) result);

			if (recData.getMaxWidth() < maxUnused)
				recData.setMaxWidth(maxUnused);

			int delta = (recData.getMaxWidth() /*- (widthHgLabel) */);
			// Calc labels start and end position an save center pos
			int left = recData.getCurrentPos();
			int right = 0;

			if (delta < widthHgLabel)
				right = recData.getCurrentPos() + widthHgLabel + 10;
			else
				right = recData.getCurrentPos() + delta + 5;

			int center = left + (right - left) / 2;

			RecData newData = new RecData(center, right, recData.getMaxWidth(), depth);
			// Draw vertical line to from up-bottom
			g2d.setColor(Color.black);
			g2d.drawLine(newData.getCenter(), oldDepth, newData.getCenter(), oldDepth + 10);

			// g2d.drawLine(newData.getCenter(), oldDepth,
			// newData.getCenter(),depth + g2d.getFontMetrics().getHeight() +
			// 3);

			// Draw all polymorphisms
			// drawPolymorhismn(g2d, result, newData.getCenter(), depth);

			// Draw haplogroup
			// drawHaplogroupNode(g2d, newData.getCenter(),depth,
			// haplogroupName);

			// attach the end node with unused polys and sampleId
			// int y = drawEndNode(g2d, result, newData.getCenter(),depth);
			int y = drawEndNode(g2d, (OverviewTreeLeafNode) result, newData.getCenter(), depth, treeHeight, includeAAC, reference);
			newData.setMaxHeight(y);

			return newData;
		}

		// throw new Exception("Invalid data format!");
	}

//	//traverses tree and renderes image using the graphics context..also subject to change
//		private RecData traverseTree(Graphics2D g2d, OverviewTreeNode result, int depth,RecData recData, int treeHeight) throws Exception {
//			
//			int numPolys =  result.getChildren("Poly").size();
//			int oldDepth = depth;
//			
//			g2d.setFont(polymprhismnFont);
//			
//			
//			
//				
//			List<Element> list = result.getChildren("TreeNode");
//			
//					
//			if (result.getChildren("TreeNode").size() > 0) {
//				
//				int maxPolyWidth = getMaxStringWidthPolys(g2d,result.getChildren("Poly"));
//				if(recData.getMaxWidth() < maxPolyWidth)
//					recData.setMaxWidth(maxPolyWidth);
//				
//				
//				
//				depth+= (1+numPolys) * (g2d.getFontMetrics().getHeight()+linePadding) + 15;
//				String haplogroupName = result.getAttributeValue("name").toString();
//				
//				g2d.setFont(haplogroupFont);
//				int maxHaplogroupWidth = g2d.getFontMetrics().stringWidth(haplogroupName)+5;
//				
//				if(recData.getMaxWidth() < maxHaplogroupWidth)
//					recData.setMaxWidth(maxHaplogroupWidth);
//				g2d.setFont(polymprhismnFont);
//				
//				RecData rNodeData = null;
//				RecData lNodeData = null;
//				
//				ArrayList<Element> l1 = new ArrayList<Element>();
//				ArrayList<Element> l2 = new ArrayList<Element>();
//				for(Element currentElement : list){
//					if(currentElement.getAttributeValue("type").equals("Haplogroup"))
//						l1.add(currentElement);
//					else
//						l2.add(currentElement);
//						
//				}
//				
//				list.clear();
//				list.addAll(l1);
//				list.addAll(l2);
//				
//				int maxdepth = 0;
//				
//				for (int i = 0; i < list.size();i++){
//					
//					if( list.size() > 1){
//						recData.setMaxWidth(0);
//						recData = traverseTree(g2d, list.get(i), depth,recData,treeHeight);	}
//					
//					else{
//						
//						recData = traverseTree(g2d, list.get(i), depth,recData,treeHeight);	}
//					
//					if(maxdepth < recData.getMaxHeight())
//						maxdepth = recData.getMaxHeight();
//					
//					if(i == 0)
//					{				
//						lNodeData = recData;
//					}
//					
//					if(i == list.size()-1)
//					{				
//						rNodeData = recData;
//					}
//				}
//				
//				//Calculate the x position of the supernode (average of child node position)
//				int superNodePosXCentered = lNodeData.getCenter() + (rNodeData.getCenter()-lNodeData.getCenter())/2;
//				
//				//Draw horizontal line
//				g2d.drawLine(lNodeData.getCenter(), depth  ,rNodeData.getCenter(), depth);
//				
//				//Draw vertical line
//				g2d.drawLine(superNodePosXCentered, depth -g2d.getFontMetrics().getHeight(),superNodePosXCentered, oldDepth);
//				
//				//Draw all polymorphisms
//				drawPolymorhismn(g2d, result,superNodePosXCentered, depth);
//				depth += 5;
//				
//				//Draw haplogroup
//				drawHaplogroupNode(g2d, superNodePosXCentered,depth, haplogroupName);	
//					
//				//attach the end node with unused polys and sampleId 		
//				//if(result.getChild("DetailedResults") != null)
//				//drawEndNode(g2d, result.getChild("DetailedResults"), superNodePosXCentered,depth);
//				
//				
//				
//				return new RecData(superNodePosXCentered,recData.getCurrentPos(),recData.getMaxWidth(), maxdepth);
//			}
//			
//		
//			//list = result.getChildren("DetailedResults");
//			
//			//if(list.size() == 1){
//				
//			// s}
//			
//			else{
//				int widthHgLabel = g2d.getFontMetrics().getHeight();
//				
//				int maxUnused = getMaxStringWidthUnusedPolys(g2d,result);
//				
//				if(recData.getMaxWidth() < maxUnused)
//					recData.setMaxWidth(maxUnused);
//				
//				int delta = (recData.getMaxWidth() /*- (widthHgLabel) */);
//				//Calc labels start and end position an save center pos
//				int left = recData.getCurrentPos();
//				int right = 0;
//				
//				if(delta < widthHgLabel)
//					right = recData.getCurrentPos() + widthHgLabel + 10;
//				else
//					right = recData.getCurrentPos()  + delta + 5;
//				
//				int center = left + (right-left)/2;
//				
//				RecData newData = new RecData(center,right,recData.getMaxWidth(),depth);
//				//Draw vertical line to from up-bottom
//				g2d.setColor(Color.black);
//				g2d.drawLine(newData.getCenter(), oldDepth, newData.getCenter(),depth + g2d.getFontMetrics().getHeight() + 3);
//				
//				//Draw all polymorphisms
//				//drawPolymorhismn(g2d, result, newData.getCenter(), depth);
//						
//				//Draw haplogroup
//				//drawHaplogroupNode(g2d, newData.getCenter(),depth, haplogroupName);		
//					
//				//attach the end node with unused polys and sampleId 		
//				//int y = drawEndNode(g2d, result, newData.getCenter(),depth);
//				int y = drawEndNode(g2d, result, newData.getCenter(),depth,treeHeight);
//				newData.setMaxHeight(y);
//				
//				return newData;
//			}
//				
//			//throw new Exception("Invalid data format!");
//		}

	/**
	 * Returns the max width of a string in a polymorphisms column
	 * 
	 * @param g2d
	 *            The graphics context
	 * @param polys
	 *            The list of polymorphisms
	 * @return The max width of the polymorphims' strings
	 */
	private int getMaxStringWidthPolys(Graphics2D g2d, List<Polymorphism> polys) {
		int max = 0;
		int width = 0;
		for (Polymorphism currentPoly : polys) {
			width = g2d.getFontMetrics().stringWidth(currentPoly.toString());
			if (max < width) {
				max = width;
			}
		}
		return width;
	}

	private int getMaxStringWidthUnusedPolys(Graphics2D g2d, OverviewTreeLeafNode tree) {
		int max = 0;
		int width = 0;
		g2d.setFont(polymorphismFont);
		for (Polymorphism currentPoly : tree.getRemainingPolys()) {
			width = g2d.getFontMetrics().stringWidth(currentPoly.toString() + "mis"); // takes
																						// the
																						// 'mis'
																						// prefix
																						// into
																						// account
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
	private int drawEndNode(Graphics2D g2d, OverviewTreeLeafNode leafNode, int center, int depth, int treeHeight, boolean includeAAC, Reference reference) {

		g2d.setFont(polymorphismFont);
		depth += 10;

		Collections.sort(leafNode.getRemainingPolys());

		for (Polymorphism currentPoly : leafNode.getRemainingPolys()) {

			depth += g2d.getFontMetrics().getHeight() + linePadding;

			if (currentPoly.isMTHotspot(reference)) {
				g2d.setColor(new Color(153, 204, 153));
			} else if (currentPoly.isBackMutation()) {
				g2d.setColor(Color.black);
			} else if (currentPoly.isHeteroplasmy()) {
				g2d.setColor(Color.gray);
			}

			else if (phyloTree.getMutationRate(currentPoly) == 0) {
				g2d.setColor(Color.red);
			} else {
				g2d.setColor(new Color(50, 180, 227));
			}

			if (includeAAC) {
				try {
					if (currentPoly.getAnnotation() != null)
						drawCenteredNode(g2d, center, depth, Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()) + " "
								+ currentPoly.getAnnotation().getAminoAcidChange());
					else
						drawCenteredNode(g2d, center, depth, Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()));

				} catch (Exception e) {
				}
			} else
				drawCenteredNode(g2d, center, depth, Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()));

		}

		g2d.setColor(Color.black);
		g2d.drawLine(center, depth, center, treeHeight - 15);

//		for (Polymorphism currentPoly :  leafNode.getMissingPolys()) {
//			depth += g2d.getFontMetrics().getHeight() + linePadding;
//			g2d.setColor(new Color(0, 0, 0));
//			drawCenteredNode(g2d, center, depth, "mis" + currentPoly.toString());		
//		}

		drawSampleIDNode(g2d, leafNode.getTestSample().getSampleID(), center, treeHeight);

		g2d.setFont(sampleIDFont);
		return depth + g2d.getFontMetrics().stringWidth(leafNode.getTestSample().getSampleID()) + 20;

	}

	private void drawHaplogroupNode(Graphics2D g2d, int x, int y, String haplogroupName) {
		g2d.setFont(haplogroupFont);
		g2d.setColor(Color.black);
		drawCenteredNode(g2d, x, y - 2, haplogroupName);
		int width = Math.max(g2d.getFontMetrics().stringWidth(haplogroupName), 20);

		g2d.drawRect(x - width / 2 - 4, y - g2d.getFontMetrics().getHeight() - 7, width + 8, g2d.getFontMetrics().getHeight());
	}

	private void drawPolymorhismn(Graphics2D g2d, OverviewTreeInnerNode currentNode, int x, int y) {

		g2d.setFont(polymorphismFont);
		g2d.setColor(Color.black);
		y -= 5 + (currentNode.getExpectedPoly().size() * (g2d.getFontMetrics().getHeight() + linePadding));

		Collections.sort(currentNode.getExpectedPoly());

		for (Polymorphism currentPoly : currentNode.getExpectedPoly()) {

			drawCenteredNode(g2d, x, y, Polymorphism.convertToATBackmutation(currentPoly.toStringShortVersion()));
			y += g2d.getFontMetrics().getHeight() + linePadding;
		}

//		for (Polymorphism currentPoly :  currentNode.getMissingPolys()) {
//
//			drawCenteredNode(g2d, x, y, currentPoly.toString());
//			y += g2d.getFontMetrics().getHeight() + linePadding;
//		}

	}

	private void drawSampleIDNode(Graphics2D g2d, String sampleIDText, int x, int y) {

		g2d.setFont(sampleIDFont);
		g2d.setColor(Color.black);
		int widthSampleID = g2d.getFontMetrics().stringWidth(sampleIDText);

		y -= widthSampleID + 15;

		g2d.drawLine(x, y, x, y + 4);
		g2d.translate(x - 6, y + 5);
		g2d.rotate(Math.toRadians(90));
		g2d.clearRect(-5, -15, widthSampleID + 5, 19);
		int[] polylineX = { 0 - 6, 6 - 6, 6 + widthSampleID - 6, 12 + widthSampleID - 6, 6 + widthSampleID - 6, 6 - 6, 0 - 6 };
		int[] polylineY = { -g2d.getFontMetrics().getHeight() / 2 + 2, 6, 6, -g2d.getFontMetrics().getHeight() / 2 + 2, -g2d.getFontMetrics().getHeight(),
				-g2d.getFontMetrics().getHeight(), -g2d.getFontMetrics().getHeight() / 2 + 2 };

		g2d.drawPolyline(polylineX, polylineY, 7);

		g2d.drawString(sampleIDText, 2, 0);
		g2d.rotate(Math.toRadians(-90));
		g2d.translate(-(x - 6), -(y + 5));
	}

	private void drawCenteredNode(Graphics2D g2d, int x, int y, String text) {

		int stringWidth = g2d.getFontMetrics().stringWidth(text);

		// Clear background
		// HANSI removed white square
		// g2d.clearRect(x - stringWidth / 2, y - (int)
		// (g2d.getFontMetrics().getHeight() * 1.5), stringWidth,
		// g2d.getFontMetrics().getHeight() + 1);
		g2d.drawString(text, x - stringWidth / 2, y - g2d.getFontMetrics().getHeight() / 2);
	}
}
