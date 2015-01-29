package pl.softech.doc;

import java.util.Map;

import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

@Component
public class Bytes2DocumentTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bytes2DocumentTransformer.class);

    private MediaTypeRegistry mediaTypeRegistry = MediaTypeRegistry.getDefaultRegistry();

    private Map<MediaType, DocumentFactory> mediaType2docFactory = new ImmutableMap.Builder<MediaType, DocumentFactory>()//
	    .put(MediaType.application("vnd.openxmlformats-officedocument.wordprocessingml.document"),
		    wordDocumentFactory())//
	    .put(MediaType.application("vnd.openxmlformats-officedocument.spreadsheetml.sheet"), excelDocumentFactory())//
	    .put(MediaType.application("vnd.openxmlformats-officedocument.presentationml.presentation"),
		    presentationDocumentFactory())//
	    .put(MediaType.application("pdf"), pdfDocumentFactory())//
	    .build();

    public AbstractDocument transform(final byte[] content, @Header("fileName") final String fileName) {

	final Tika tika = new Tika();

	final MediaType type = MediaType.parse(tika.detect(content, fileName));
	final MediaType superType = mediaTypeRegistry.getSupertype(type);

	LOGGER.debug("Transforming {0}[size={1}B] of type {2} to Document", fileName, content.length, type.getType());

	if (!MediaType.application("pdf").equals(type) && !MediaType.application("x-tika-ooxml").equals(superType)) {
	    throw new UnsupportedMediaTypeException(String.format("Only Open XML Document is supported. Got %s",
		    type.getType()));
	}

	final DocumentFactory factory = mediaType2docFactory.get(type);

	if (factory == null) {
	    throw new UnsupportedDocumentException(String.format("Document of type %s is unsupported", type.getType()));
	}

	return factory.create(content, fileName);
    }

    private DocumentFactory wordDocumentFactory() {
	return new DocumentFactory() {
	    @Override
	    public AbstractDocument create(final byte[] content, final String fileName) {
		return new WordDocument(fileName, content);
	    }
	};
    }

    private DocumentFactory pdfDocumentFactory() {
	return new DocumentFactory() {
	    @Override
	    public AbstractDocument create(final byte[] content, final String fileName) {
		return new PdfDocument(fileName, content);
	    }
	};
    }

    private DocumentFactory presentationDocumentFactory() {
	return new DocumentFactory() {
	    @Override
	    public AbstractDocument create(final byte[] content, final String fileName) {
		return new PowerPointDocument(fileName, content);
	    }
	};
    }

    private DocumentFactory excelDocumentFactory() {
	return new DocumentFactory() {
	    @Override
	    public AbstractDocument create(final byte[] content, final String fileName) {
		return new ExcelDocument(fileName, content);
	    }
	};
    }

    private interface DocumentFactory {
	AbstractDocument create(byte[] content, String fileName);
    }

}
