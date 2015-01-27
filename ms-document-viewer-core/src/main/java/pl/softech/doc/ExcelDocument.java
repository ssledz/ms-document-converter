package pl.softech.doc;

public class ExcelDocument extends AbstractDocument {

    public ExcelDocument(String fileName, byte[] content) {
	super(fileName, content);
    }

    @Override
    public void accept(DocumentVisitor visitor) {
	visitor.visit(this);
    }

}
