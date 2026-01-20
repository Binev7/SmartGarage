package com.portfolio.smartgarage.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.portfolio.smartgarage.dto.invoice.InvoiceDto;
import com.portfolio.smartgarage.service.interfaces.PdfService;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
public class PdfServiceImpl implements PdfService {

    @Override
    public byte[] generateInvoicePdf(InvoiceDto invoice) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);

        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("INVOICE: " + invoice.getInvoiceNumber(), fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("\nClient: " + invoice.getClientName()));
        document.add(new Paragraph("Vehicle: " + invoice.getVehicleModel() + " [" + invoice.getLicensePlate() + "]"));
        document.add(new Paragraph("------------------------------------------------------------------\n"));

        invoice.getItems().forEach(item -> {
            document.add(new Paragraph(item.getServiceName() + " : $" + item.getPrice()));
        });

        document.add(new Paragraph("\nTOTAL AMOUNT: $" + invoice.getTotalAmount(), fontTitle));

        document.close();
        return out.toByteArray();
    }
}