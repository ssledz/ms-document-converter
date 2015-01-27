package pl.softech.doc;

public class PdfDocument extends AbstractDocument {

    public PdfDocument(String fileName, byte[] content) {
	super(fileName, content);
    }

    @Override
    public void accept(DocumentVisitor visitor) {
	visitor.visit(this);
    }

}
