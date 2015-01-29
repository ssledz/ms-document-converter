package pl.softech.doc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractDocument {

    private static final Pattern PATTERN = Pattern.compile("(.+)\\.[^\\.].+$");
    
    private final String fileName;
    private final byte[] content;

    public AbstractDocument(final String fileName, final byte[] content) {
	this.fileName = fileName;
	this.content = content;
    }

    public String getFileName() {
	return fileName;
    }

    public byte[] getContent() {
	return content;
    }

    public InputStream getInputStream() {
	return new ByteArrayInputStream(content);
    }
    
    public String getFileNameWithoutExtension() {
	final Matcher m = PATTERN.matcher(fileName);
	if (m.find()) {
	    return m.group(1);
	}
	return fileName;
    }

    public abstract void accept(DocumentVisitor visitor);

}
