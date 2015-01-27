package pl.softech.doc;

public class PowerPointDocument extends AbstractDocument {

	public PowerPointDocument(final String fileName, final byte[] content) {
		super(fileName, content);
	}

	@Override
	public void accept(final DocumentVisitor visitor) {
		visitor.visit(this);
	}

}
