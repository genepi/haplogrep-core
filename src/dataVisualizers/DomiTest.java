//package dataVisualizers;
//
//
//
//import java.awt.Graphics2D;
//import java.io.FileOutputStream;
//
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.FontFactory;
//import com.itextpdf.text.List;
//import com.itextpdf.text.ListItem;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Phrase;
//import com.itextpdf.text.pdf.DefaultFontMapper;
//import com.itextpdf.text.pdf.PdfContentByte;
//import com.itextpdf.text.pdf.PdfTemplate;
//import com.itextpdf.text.pdf.PdfWriter;
//
//public class DomiTest {
//
//	public static Font fontModule;
//
//	public static Font fontQuestion;
//
//	public static Font fontAnswer;
//
//	public static Font fontMissingQuestion;
//
//	public static Font fontSkippedQuestion;
//
//	private PdfWriter writer;
//
//	private int width;
//
//	private int height;
//
//	static {
//
//		fontModule = FontFactory.getFont("Arial", 14);
//		fontModule.setStyle(Font.BOLD);
//
//		fontQuestion = FontFactory.getFont("Arial", 10);
//		fontQuestion.setStyle(Font.BOLD);
//
//		fontAnswer = FontFactory.getFont("Arial", 10);
//
//		fontMissingQuestion = FontFactory.getFont("Arial", 10);
//		fontMissingQuestion.setStyle(Font.BOLD);
//		fontMissingQuestion.setColor(255, 0, 0);
//		fontMissingQuestion.setStyle(Font.UNDERLINE);
//
//		fontSkippedQuestion = FontFactory.getFont("Arial", 10);
//		fontSkippedQuestion.setColor(200, 200, 200);
//
//	}
//
//	public DomiTest(int width, int height) {
//
//		this.width = width;
//		this.height = height;
//
//	}
//
//	public void draw(Graphics2D g2d) {
//
//	}
//
//	public void save(String filename) {
//
//		try {
//
//			Document document = new Document();
//
//			writer = PdfWriter.getInstance(document, new FileOutputStream(
//					filename));
//			document.open();
//
//			PdfContentByte cb = writer.getDirectContent();
//			PdfTemplate tp = cb.createTemplate(width, height);
//			Graphics2D g2 = tp.createGraphics(width, height,
//					new DefaultFontMapper());
//
//			draw(g2);
//
//			g2.dispose();
//			cb.addTemplate(tp, 30, 250);
//
//			document.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//}
