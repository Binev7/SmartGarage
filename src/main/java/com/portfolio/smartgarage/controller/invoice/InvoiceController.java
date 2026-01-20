package com.portfolio.smartgarage.controller.invoice;

import com.portfolio.smartgarage.dto.invoice.InvoiceDto;
import com.portfolio.smartgarage.service.impl.PdfServiceImpl;
import com.portfolio.smartgarage.service.interfaces.ServiceOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final PdfServiceImpl pdfService;
    private final ServiceOrderService serviceOrderService;

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/download/{orderId}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long orderId) {

        InvoiceDto invoice = serviceOrderService.getInvoiceData(orderId);

        byte[] pdfContent = pdfService.generateInvoicePdf(invoice);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("invoice_" + orderId + ".pdf")
                .build());

        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}