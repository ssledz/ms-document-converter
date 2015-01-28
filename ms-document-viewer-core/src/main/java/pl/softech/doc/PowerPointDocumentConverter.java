package pl.softech.doc;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class PowerPointDocumentConverter implements DocumentConverter {

    @Override
    public void convertToPdf(AbstractDocument document, OutputStream out) throws Exception {

	Document pdf = new Document();
	PdfWriter writer = PdfWriter.getInstance(pdf, out);

	pdf.open();

	XMLSlideShow ppt = new XMLSlideShow(document.getContentInputStream());
	Dimension pageSize = ppt.getPageSize();
	BufferedImage imgBuffer = new BufferedImage((int) pageSize.getWidth(), (int) pageSize.getHeight(),
		BufferedImage.TYPE_INT_ARGB);

	Graphics2D g2 = imgBuffer.createGraphics();
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
	g2.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	
	for (XSLFSlide slide : ppt.getSlides()) {
	    slide.draw(g2);
	    pdf.add(new Paragraph());
	    ByteArrayOutputStream otmp = new ByteArrayOutputStream();
	    ImageIO.write(imgBuffer, "png", otmp);
	    pdf.add(Image.getInstance(otmp.toByteArray()));
	}

	pdf.close();

    }

}
