package pl.softech.doc;

import java.awt.Color;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;



public class ExcelDocumentConverter implements DocumentConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDocumentConverter.class);

    private ThreadLocal<NumberFormat> numberFormat = new ThreadLocal<NumberFormat>() {
	protected NumberFormat initialValue() {
	    NumberFormat nb = NumberFormat.getNumberInstance();
	    nb.setMaximumFractionDigits(6);
	    nb.setGroupingUsed(true);
	    return nb;
	};
    };

    private Map<Integer, CellFormatter> cellType2Formatter = new HashMap<Integer, CellFormatter>();

    private final CellFormatter fallbackFormatter = new CellFormatter() {

	@Override
	public String format(Cell cell) {
	    return "";
	}

    };

    public ExcelDocumentConverter() {
	initDefaultCellFormatters();
    }

    private void initDefaultCellFormatters() {

	register(Cell.CELL_TYPE_BLANK, new CellFormatter() {
	    @Override
	    public String format(Cell cell) {
		return "";
	    }
	});
	register(Cell.CELL_TYPE_BOOLEAN, new CellFormatter() {
	    @Override
	    public String format(Cell cell) {
		return Boolean.toString(cell.getBooleanCellValue());
	    }
	});
	register(Cell.CELL_TYPE_NUMERIC, new CellFormatter() {
	    @Override
	    public String format(Cell cell) {
		return numberFormat.get().format(cell.getNumericCellValue());
	    }
	});
	register(Cell.CELL_TYPE_STRING, new CellFormatter() {
	    @Override
	    public String format(Cell cell) {
		return cell.getStringCellValue();
	    }
	});
	register(Cell.CELL_TYPE_FORMULA, new CellFormatter() {
	    @Override
	    public String format(Cell cell) {
		return "#FORMULA";
	    }
	});
	register(Cell.CELL_TYPE_ERROR, new CellFormatter() {
	    @Override
	    public String format(Cell cell) {
		return "#ERROR_CODE=" + cell.getErrorCellValue();
	    }
	});

    }

    public void register(int type, CellFormatter formatter) {
	cellType2Formatter.put(type, formatter);
    }

    @Override
    public void convertToPdf(AbstractDocument document, OutputStream out) throws Exception {

	Workbook wb = WorkbookFactory.create(document.getInputStream());
	Document pdf = new Document(PageSize.A4.rotate());
	PdfWriter.getInstance(pdf, out);

	pdf.open();

	for (int i = 0; i < wb.getNumberOfSheets(); i++) {
	    Sheet sheet = wb.getSheetAt(i);
	    processSheet(wb, sheet, i, pdf);
	}

	pdf.close();

    }

    private int scanForMaxCols(Sheet sheet) {
	int max = 0;

	Iterator<Row> rIt = sheet.iterator();
	while (rIt.hasNext()) {
	    Row row = rIt.next();
	    if (max < row.getLastCellNum()) {
		max = row.getLastCellNum();
	    }
	}
	return max + 1;
    }

    private void applyCellStyle(PdfPCell pdfCell, CellStyle style) {

	HSSFColor bc = HSSFColor.getIndexHash().get(style.getFillBackgroundColor());
	if (bc != null) {
	    short[] rgb = bc.getTriplet();
//	    pdfCell.setBackgroundColor(new Color(rgb[0], rgb[1], rgb[2]));
	}
    }

    private PdfPCell cerateCell(Workbook wb, CellStyle style, String value) throws Exception {

	org.apache.poi.ss.usermodel.Font wbFont = wb.getFontAt(style.getFontIndex());
	HSSFColor wbFc = HSSFColor.getIndexHash().get(wbFont.getColor());

	Color fc = Color.BLACK;

	if (wbFc != null) {
	    short[] rgb = wbFc.getTriplet();
	    fc = new Color(rgb[0], rgb[1], rgb[2]);
	}

//	Font font = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED),
//		(float) wbFont.getFontHeightInPoints(), Font.NORMAL, fc);

//	PdfPCell pdfCell = new PdfPCell(new Phrase(value, font));
	PdfPCell pdfCell = new PdfPCell(new Phrase(value));
	applyCellStyle(pdfCell, style);
	return pdfCell;
    }

    private void setupColumnsWidth(Sheet sheet, int columnNum, PdfPTable table) throws DocumentException {

	int[] widths = new int[columnNum];

	for (int i = 0; i < columnNum; i++) {
	    widths[i] = sheet.getColumnWidth(i);
	}

	table.setTotalWidth(100);
	table.setWidths(widths);

    }

    private void processSheet(Workbook wb, Sheet sheet, int sheedIndex, Document pdf) throws Exception {

	Anchor anchor = new Anchor(sheet.getSheetName());
	Chapter chapter = new Chapter(new Paragraph(anchor), sheedIndex);

	Iterator<Row> rIt = sheet.iterator();

	int cellsNum = scanForMaxCols(sheet);
	PdfPTable table = new PdfPTable(cellsNum);
	chapter.add(Chunk.NEWLINE);
	chapter.add(Chunk.NEWLINE);
	chapter.add(table);

	setupColumnsWidth(sheet, cellsNum, table);

	while (rIt.hasNext()) {
	    Row row = rIt.next();
	    
	    for(int i = 0; i < cellsNum; i++) {

		Cell cell = row.getCell(i);

		if(cell == null) {
		    table.addCell(" ");
		    continue;
		}
		
		CellFormatter formatter = cellType2Formatter.get(cell.getCellType());
		if (formatter == null) {
		    LOGGER.warn("No formatter for cell[{0}][{1}] type {2}", cell.getRowIndex(), cell.getColumnIndex(),
			    cell.getCellType());
		    formatter = fallbackFormatter;
		}
		table.addCell(cerateCell(wb, cell.getCellStyle(), formatter.format(cell)));
	    }

	}

	pdf.add(chapter);
    }

    public interface CellFormatter {
	String format(Cell cell);
    }

}
