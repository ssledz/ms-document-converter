package pl.softech.doc;

public class WordDocument extends AbstractDocument {

    public WordDocument(String fileName, byte[] content) {
	super(fileName, content);
    }

    @Override
    public void accept(DocumentVisitor visitor) {
	visitor.visit(this);
    }

}
