package pl.softech.doc;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Service;

@Service
public class Document2PdfConverter {

	private final Mapper fontMapper = new IdentityPlusMapper();

	private final Pattern pattern = Pattern.compile("(.+)\\.[^\\.].+$");

	public AbstractDocument convert(final AbstractDocument document) {
		final DocumentVisitor visitor = new DocumentVisitor();
		document.accept(visitor);
		return visitor.getPdfDocument();
	}

	private class DocumentVisitor extends DocumentVisitorAdapter {

		private PdfDocument pdfDocument;

		public PdfDocument getPdfDocument() {
			return pdfDocument;
		}

		@Override
		public void visit(final WordDocument document) {

			try {
				final WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(document.getContentInputStream());

				wordMLPackage.setFontMapper(fontMapper);

				final FOSettings foSettings = Docx4J.createFOSettings();

				foSettings.setWmlPackage(wordMLPackage);

				final ByteArrayOutputStream out = new ByteArrayOutputStream();

				Docx4J.toFO(foSettings, out, Docx4J.FLAG_EXPORT_PREFER_XSL);

				final String fileName = String.format("%s.pdf", getFileNameWithoutExtension(document.getFileName()));

				pdfDocument = new PdfDocument(fileName, out.toByteArray());

			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private String getFileNameWithoutExtension(final String fileName) {
		final Matcher m = pattern.matcher(fileName);
		if (m.find()) {
			return m.group(1);
		}
		return fileName;
	}

}
