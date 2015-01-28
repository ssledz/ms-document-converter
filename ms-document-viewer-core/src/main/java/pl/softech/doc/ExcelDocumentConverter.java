package pl.softech.doc;

import java.io.OutputStream;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;

public class ExcelDocumentConverter implements DocumentConverter {

    @Override
    public void convertToPdf(AbstractDocument document, OutputStream out) throws Exception {

	SpreadsheetMLPackage excelMLPackage = (SpreadsheetMLPackage) SpreadsheetMLPackage.load(document
		.getContentInputStream());


	
	final FOSettings foSettings = Docx4J.createFOSettings();

	foSettings.setWmlPackage(excelMLPackage);

	Docx4J.toFO(foSettings, out, Docx4J.FLAG_EXPORT_PREFER_XSL);
    }

}
