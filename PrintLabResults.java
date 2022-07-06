/*
 * Class: PrintLabResults
 * Prints requested laboratory results
 * */

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.IOException;

public class PrintLabResults {

    public static void printPdf(String filename, String name, String sUID, String pUID, String rDate, String birthday, String gender, String phoneNo, int age, String t, String r)
            throws IOException {
        String currDir = System.getProperty("user.dir");
        String pdfPath = currDir + "/" + filename;
        float[] columnWidth = {PageSize.A4.getWidth()-10, PageSize.A4.getWidth()-10};

        PdfWriter writer = new PdfWriter(pdfPath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);
        PdfPage pdfPage = pdfDoc.addNewPage();

        ImageData imageData = ImageDataFactory.create(
                currDir + "/MP LOGO.png");

        Image img = new Image(imageData);
        img.setHorizontalAlignment(HorizontalAlignment.CENTER);

        Paragraph address = new Paragraph("2311 N. Los Robles Avenue Pasadena, CA");
        Paragraph telephone = new Paragraph("#8-7000");
        Table information = new Table(columnWidth);
        Table table = new Table(columnWidth);
        Table names = new Table(columnWidth);
        PdfCanvas canvas = new PdfCanvas(pdfPage);

        information.setBorder(Border.NO_BORDER);

        doc.add(img.scale(10,10));
        doc.add((address.setFontSize(12)).setTextAlignment(TextAlignment.CENTER));
        doc.add((telephone.setFontSize(12)).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("\n"));

        Cell pName = new Cell();
        Cell psID = new Cell();
        Cell pID = new Cell();
        Cell pDate = new Cell();
        Cell pAge = new Cell();
        Cell pBday = new Cell();
        Cell pGen = new Cell();
        Cell pNo = new Cell();

        pName.add(new Paragraph("Name: " + name).setFontSize(12));
        psID.add(new Paragraph("Specimen ID: " + sUID).setFontSize(12));
        pID.add(new Paragraph("Patient ID: " + pUID).setFontSize(12));
        pDate.add(new Paragraph("Collection Date: " + rDate).setFontSize(12));
        pAge.add(new Paragraph("Age: " + age).setFontSize(12));
        pBday.add(new Paragraph("Birthday: " + birthday).setFontSize(12));
        pGen.add(new Paragraph("Gender: " + gender).setFontSize(12));
        pNo.add(new Paragraph("Phone Number: " + phoneNo).setFontSize(12));

        information.addCell(pName.setBorder(Border.NO_BORDER));
        information.addCell(psID.setBorder(Border.NO_BORDER));
        information.addCell(pID.setBorder(Border.NO_BORDER));
        information.addCell(pDate.setBorder(Border.NO_BORDER));
        information.addCell(pAge.setBorder(Border.NO_BORDER));
        information.addCell(pBday.setBorder(Border.NO_BORDER));
        information.addCell(pGen.setBorder(Border.NO_BORDER));
        information.addCell(pNo.setBorder(Border.NO_BORDER));

        doc.add(information);
        addLine(canvas);
        doc(names);

        Cell test = new Cell();
        Cell result = new Cell();

        test.add((new Paragraph("Test").setFontSize(12)).setBold());
        result.add((new Paragraph("Results").setFontSize(12)).setBold());

        table.addCell(test);
        table.addCell(result);
        table.addCell(t);
        table.addCell(r);
        doc.add(new Paragraph("\n"));
        doc.add(table);
        doc.add(new Paragraph("\n\n\n\n\n"));
        doc.add(names);

        canvas.closePathStroke();

        doc.close();
    }

    private static void addLine(PdfCanvas canvas) {
        canvas.moveTo(35, 675);
        canvas.lineTo(555, 675);
        canvas.moveTo(35, 587);
        canvas.lineTo(555, 587);
        canvas.moveTo(35, 450);
        canvas.lineTo(555, 450);

    }

    private static void doc(Table table) {
        Cell doc1 = new Cell();
        Cell doc2 = new Cell();
        Cell prof1 = new Cell();
        Cell prof2 = new Cell();
        Cell lino1 = new Cell();
        Cell lino2 = new Cell();

        doc1.add(new Paragraph("Jane Doe"));
        doc2.add(new Paragraph("John Roe"));
        prof1.add(new Paragraph("Medical Technologist"));
        prof2.add(new Paragraph("Pathologist"));
        lino1.add(new Paragraph("Lic. # 123456789"));
        lino2.add(new Paragraph("Lic. # 987654321"));

        table.addCell(doc1.setBorder(Border.NO_BORDER));
        table.addCell(doc2.setBorder(Border.NO_BORDER));
        table.addCell(prof1.setBorder(Border.NO_BORDER));
        table.addCell(prof2.setBorder(Border.NO_BORDER));
        table.addCell(lino1.setBorder(Border.NO_BORDER));
        table.addCell(lino2.setBorder(Border.NO_BORDER));
    }
}