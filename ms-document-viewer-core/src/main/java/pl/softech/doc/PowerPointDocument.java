package pl.softech.doc;

public class PowerPointDocument extends AbstractDocument {

    public PowerPointDocument(String fileName, byte[] content) {
	super(fileName, content);
    }

    @Override
    public void accept(DocumentVisitor visitor) {
	visitor.visit(this);
    }

}
