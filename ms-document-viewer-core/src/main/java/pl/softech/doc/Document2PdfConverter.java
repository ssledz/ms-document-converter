package pl.softech.doc;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.docx4j.convert.out.pdf.PdfConversion;
import org.docx4j.convert.out.pdf.viaXSLFO.PdfSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;

@Service
public class Document2PdfConverter {

    private Pattern pattern = Pattern.compile("(.+)\\.[^\\.].+$");

    public AbstractDocument convert(AbstractDocument document) {
	DocumentVisitor visitor = new DocumentVisitor();
	document.accept(visitor);
	return visitor.getPdfDocument();
    }

    private class DocumentVisitor extends DocumentVisitorAdapter {

	private PdfDocument pdfDocument;

	public PdfDocument getPdfDocument() {
	    return pdfDocument;
	}

	@Override
	public void visit(WordDocument document) {

	    try {
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(document.getContentInputStream());
		
		PdfSettings pdfSettings = new PdfSettings();

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		PdfConversion converter = new org.docx4j.convert.out.pdf.viaXSLFO.Conversion(wordMLPackage);

		converter.output(out, pdfSettings);

		String fileName = String.format("%s.pdf", getFileNameWithoutExtension(document.getFileName()));

		pdfDocument = new PdfDocument(fileName, out.toByteArray());

	    } catch (Docx4JException e) {
		throw new RuntimeException(e);
	    }
	}
    }

    private String getFileNameWithoutExtension(String fileName) {
	Matcher m = pattern.matcher(fileName);
	if (m.find()) {
	    return m.group(1);
	}
	return fileName;
    }

}
