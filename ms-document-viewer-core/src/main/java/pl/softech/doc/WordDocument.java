package pl.softech.doc;

public class WordDocument extends AbstractDocument {

	public WordDocument(final String fileName, final byte[] content) {
		super(fileName, content);
	}

	@Override
	public void accept(final DocumentVisitor visitor) {
		visitor.visit(this);
	}

}
