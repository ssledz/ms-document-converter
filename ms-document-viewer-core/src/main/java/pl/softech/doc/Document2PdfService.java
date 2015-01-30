package pl.softech.doc;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Document2PdfService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Document2PdfService.class);

    private final Mapper fontMapper = new IdentityPlusMapper();

    public Document2PdfService() {
	initFontsMapper();
    }

    @SuppressWarnings("deprecation")
    private void initFontsMapper() {

	LOGGER.info("Known fonts:");
	
	for (Map.Entry<String, PhysicalFont> e : PhysicalFonts.getPhysicalFonts().entrySet()) {
	    LOGGER.info("\t- {}", e.getKey());
	}

	PhysicalFont font = PhysicalFonts.get("FreeMono");
	if (font != null) {
	    fontMapper.put("Courier New", font);
	}

	font = PhysicalFonts.get("dejavu sans mono");
	if (font != null) {
	    fontMapper.put("Ubuntu Mono", font);
	}

	font = PhysicalFonts.get("standard symbols l");
	if (font != null) {
	    fontMapper.put("Symbol", font);
	}

    }

    public AbstractDocument convert(final AbstractDocument document) {
	final DocumentVisitor visitor = new DocumentVisitor();
	document.accept(visitor);
	return visitor.getPdfDocument();
    }

    private class DocumentVisitor extends DocumentVisitorAdapter {

	private PdfDocument pdfDocument;

	public PdfDocument getPdfDocument() {
	    return pdfDocument;
	}

	private PdfDocument createPdf(DocumentConverter converter, AbstractDocument document) {
	    try {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		converter.convertToPdf(document, out);
		final String fileName = String.format("%s.pdf", document.getFileNameWithoutExtension());
		return new PdfDocument(fileName, out.toByteArray());
	    } catch (final Exception e) {
		throw new RuntimeException(e);
	    }
	}

	@Override
	public void visit(PdfDocument document) {
	    pdfDocument = document;
	}

	@Override
	public void visit(PowerPointDocument document) {
	    PowerPointDocumentConverter converter = new PowerPointDocumentConverter();
	    pdfDocument = createPdf(converter, document);
	}

	@Override
	public void visit(ExcelDocument document) {
	    ExcelDocumentConverter converter = new ExcelDocumentConverter();
	    pdfDocument = createPdf(converter, document);
	}

	@Override
	public void visit(final WordDocument document) {
	    Docx4jWordDocumentConverter converter = new Docx4jWordDocumentConverter(fontMapper);
	    pdfDocument = createPdf(converter, document);
	}
    }

}
