package pl.softech.doc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public abstract class AbstractDocument {

    private final String fileName;
    private final byte[] content;

    public AbstractDocument(String fileName, byte[] content) {
	this.fileName = fileName;
	this.content = content;
    }

    public String getFileName() {
	return fileName;
    }

    public byte[] getContent() {
	return content;
    }
    
    public InputStream getContentInputStream() {
	return new ByteArrayInputStream(content);
    }

    public abstract void accept(DocumentVisitor visitor);

}
