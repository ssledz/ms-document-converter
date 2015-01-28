package pl.softech.doc;

import java.io.OutputStream;

public interface DocumentConverter {
    void convertToPdf(AbstractDocument document, OutputStream out) throws Exception;
}
