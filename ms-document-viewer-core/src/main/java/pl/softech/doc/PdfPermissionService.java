package pl.softech.doc;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

@Service
public class PdfPermissionService {

    private final String ownerPassword;

    @Autowired
    public PdfPermissionService(@Value("${pdf.owner.password}") String ownerPassword) {
	this.ownerPassword = ownerPassword;
    }

    public PdfDocument changeToReadOnly(final PdfDocument document) throws Exception {

	PdfReader reader = new PdfReader(document.getContent());

	ByteArrayOutputStream out = new ByteArrayOutputStream();
	PdfStamper stamper = new PdfStamper(reader, out);
	stamper.setEncryption(null, ownerPassword.getBytes(), 0, true);
	stamper.close();
	reader.close();
	return new PdfDocument(document.getFileName(), out.toByteArray());
    }

}
