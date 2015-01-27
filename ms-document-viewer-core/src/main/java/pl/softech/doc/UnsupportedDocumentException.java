package pl.softech.doc;

public class UnsupportedDocumentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnsupportedDocumentException(String message) {
	super(message);
    }

}
