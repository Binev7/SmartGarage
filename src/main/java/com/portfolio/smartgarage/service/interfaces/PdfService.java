package com.portfolio.smartgarage.service.interfaces;

import com.portfolio.smartgarage.dto.invoice.InvoiceDto;

public interface PdfService {

    byte[] generateInvoicePdf(InvoiceDto invoice);
}
