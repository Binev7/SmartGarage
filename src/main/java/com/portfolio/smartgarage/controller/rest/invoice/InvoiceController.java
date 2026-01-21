package com.portfolio.smartgarage.controller.rest.invoice;

import com.portfolio.smartgarage.dto.invoice.InvoiceDto;
import com.portfolio.smartgarage.service.impl.PdfServiceImpl;
import com.portfolio.smartgarage.service.interfaces.ServiceOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "Endpoints for generating and downloading financial documents")
public class InvoiceController {

    private final PdfServiceImpl pdfService;
    private final ServiceOrderService serviceOrderService;

    @Operation(
            summary = "Download invoice PDF",
            description = "Retrieves order data, generates a formatted PDF invoice, and returns it as a downloadable file."
    )
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/download/{orderId}")
    public ResponseEntity<byte[]> downloadInvoice(
            @Parameter(description = "ID of the service order to generate an invoice for")
            @PathVariable Long orderId) {

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