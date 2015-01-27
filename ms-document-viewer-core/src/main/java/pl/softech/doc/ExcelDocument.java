package pl.softech.doc;

public class ExcelDocument extends AbstractDocument {

	public ExcelDocument(final String fileName, final byte[] content) {
		super(fileName, content);
	}

	@Override
	public void accept(final DocumentVisitor visitor) {
		visitor.visit(this);
	}

}
