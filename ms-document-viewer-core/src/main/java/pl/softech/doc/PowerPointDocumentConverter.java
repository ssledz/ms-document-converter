package pl.softech.doc;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.poi.util.Units;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.RectangleReadOnly;
import com.lowagie.text.pdf.PdfWriter;

public class PowerPointDocumentConverter implements DocumentConverter {

    private Graphics2D createGraphics(BufferedImage imgBuffer) {
	Graphics2D g2 = imgBuffer.createGraphics();
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	return g2;
    }

    @Override
    public void convertToPdf(AbstractDocument document, OutputStream out) throws Exception {

	XMLSlideShow ppt = new XMLSlideShow(document.getContentInputStream());
	Dimension pageSize = ppt.getPageSize();

	int margin = 36;

	float aWidth = (float) (pageSize.getWidth() - 2 * margin);
	float aHeight = (float) (pageSize.getHeight() - 2 * margin);
	
	int width = Units.toEMU(aWidth) / Units.EMU_PER_PIXEL;
	int height = Units.toEMU(aHeight) / Units.EMU_PER_PIXEL;

	Document pdf = new Document(new RectangleReadOnly((float) pageSize.getWidth(), (float) pageSize.getHeight()),
		margin, margin, margin, margin);

	PdfWriter.getInstance(pdf, out);

	pdf.open();

	for (XSLFSlide slide : ppt.getSlides()) {

	    BufferedImage imgBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2 = createGraphics(imgBuffer);
	    slide.draw(g2);
	    pdf.add(new Paragraph());
	    ByteArrayOutputStream otmp = new ByteArrayOutputStream();
	    ImageIO.write(imgBuffer, "png", otmp);
	    Image img = Image.getInstance(otmp.toByteArray());
	    img.setBorder(RectangleReadOnly.NO_BORDER);
	    img.scaleToFit(aWidth, aHeight);
	    img.setAlignment(Image.MIDDLE);
	    pdf.add(img);

	}

	pdf.close();

    }

}
