package pl.softech.doc;

import java.io.ByteArrayOutputStream;

import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.springframework.stereotype.Service;

@Service
public class Document2PdfService {

    private final Mapper fontMapper = new IdentityPlusMapper();

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
	    Docx4jExcelDocumentConverter converter = new Docx4jExcelDocumentConverter();
	    pdfDocument = createPdf(converter, document);
	}

	@Override
	public void visit(final WordDocument document) {
	    Docx4jWordDocumentConverter converter = new Docx4jWordDocumentConverter(fontMapper);
	    pdfDocument = createPdf(converter, document);
	}
    }

}
