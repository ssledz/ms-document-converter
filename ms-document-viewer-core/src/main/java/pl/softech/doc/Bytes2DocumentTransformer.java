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

    private Map<MediaType, DocumentFactory> mediaType2docFactory = new ImmutableMap.Builder<MediaType, DocumentFactory>()
	    .put(MediaType.application("vnd.openxmlformats-officedocument.wordprocessingml.document"),
		    wordDocumentFactory()).build();

    public AbstractDocument transform(byte[] content, @Header("fileName") String fileName) {

	Tika tika = new Tika();
	
	MediaType type = MediaType.parse(tika.detect(content, fileName));
	MediaType superType = mediaTypeRegistry.getSupertype(type);

	LOGGER.debug("Transforming {0}[size={1}B] of type {2} to Document", fileName, content.length, type.getType());
	
	if (!MediaType.application("x-tika-ooxml").equals(superType)) {
	    throw new UnsupportedMediaTypeException(String.format("Only Open XML Document is supported. Got %s",
		    type.getType()));
	}

	DocumentFactory factory = mediaType2docFactory.get(type);

	if (factory == null) {
	    throw new UnsupportedDocumentException(String.format("Document of type %s is unsupported", type.getType()));
	}

	return factory.create(content, fileName);
    }

    private DocumentFactory wordDocumentFactory() {
	return new DocumentFactory() {
	    @Override
	    public AbstractDocument create(byte[] content, String fileName) {
		return new WordDocument(fileName, content);
	    }
	};
    }

    private interface DocumentFactory {
	AbstractDocument create(byte[] content, String fileName);
    }

}
