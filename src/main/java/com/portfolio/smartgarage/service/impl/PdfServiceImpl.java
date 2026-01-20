package com.portfolio.smartgarage.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.portfolio.smartgarage.dto.invoice.InvoiceDto;
import com.portfolio.smartgarage.service.interfaces.PdfService;
import com.portfolio.smartgarage.service.interfaces.CurrencyService; // Твоят сървис
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final CurrencyService currencyService;

    private static final String LABEL_INVOICE = "INVOICE: ";
    private static final String LABEL_TOTAL = "TOTAL AMOUNT: ";
    private static final String SEPARATOR = "------------------------------------------------------------------";

    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 12);

    @Override
    public byte[] generateInvoicePdf(InvoiceDto invoice) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            addHeader(document, invoice.getInvoiceNumber());
            addBody(document, invoice);
            addFooter(document, invoice);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
        return out.toByteArray();
    }

    private void addHeader(Document document, String invoiceNum) throws DocumentException {
        Paragraph p = new Paragraph(LABEL_INVOICE + invoiceNum, FONT_TITLE);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(20);
        document.add(p);
    }

    private void addBody(Document document, InvoiceDto invoice) throws DocumentException {
        document.add(new Paragraph("Client: " + invoice.getClientName(), FONT_NORMAL));
        document.add(new Paragraph("Vehicle: " + invoice.getVehicleModel(), FONT_NORMAL));
        document.add(new Paragraph(SEPARATOR));

        for (var item : invoice.getItems()) {
            String price = currencyService.format(item.getPrice(), invoice.getCurrencyCode());
            document.add(new Paragraph(item.getServiceName() + " : " + price, FONT_NORMAL));
        }
    }

    private void addFooter(Document document, InvoiceDto invoice) throws DocumentException {
        document.add(new Paragraph(SEPARATOR));
        String total = currencyService.format(invoice.getTotalAmount(), invoice.getCurrencyCode());
        Paragraph p = new Paragraph(LABEL_TOTAL + total, FONT_TITLE);
        p.setAlignment(Element.ALIGN_RIGHT);
        document.add(p);
    }
}