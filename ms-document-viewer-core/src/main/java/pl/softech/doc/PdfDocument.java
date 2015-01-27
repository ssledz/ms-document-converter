package pl.softech.doc;

public class PdfDocument extends AbstractDocument {

	public PdfDocument(final String fileName, final byte[] content) {
		super(fileName, content);
	}

	@Override
	public void accept(final DocumentVisitor visitor) {
		visitor.visit(this);
	}

}
