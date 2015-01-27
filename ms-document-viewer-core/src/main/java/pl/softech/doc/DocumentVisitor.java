package pl.softech.doc;

public interface DocumentVisitor {

	void visit(WordDocument document);

	void visit(ExcelDocument document);

	void visit(PowerPointDocument document);

	void visit(PdfDocument document);

}
