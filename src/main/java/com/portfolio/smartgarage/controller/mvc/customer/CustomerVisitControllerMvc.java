package com.portfolio.smartgarage.controller.mvc.customer;

import com.portfolio.smartgarage.dto.visit.CreateVisitDto;
import com.portfolio.smartgarage.helper.constant.BaseConstants;
import com.portfolio.smartgarage.security.CustomUserDetails;
import com.portfolio.smartgarage.service.interfaces.ClientVehicleService;
import com.portfolio.smartgarage.service.interfaces.ServiceService;
import com.portfolio.smartgarage.service.interfaces.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/customer/visits")
@RequiredArgsConstructor
public class CustomerVisitControllerMvc {

    private final VisitService visitService;
    private final ClientVehicleService clientVehicleService;
    private final ServiceService serviceService;

    @GetMapping("/book/step1")
    public String selectDate(@RequestParam("vehicleId") Long clientVehicleId, Model model) {
        var availability = visitService.getCalendarAvailability(LocalDate.now());

        model.addAttribute("availability", availability);
        model.addAttribute("clientVehicleId", clientVehicleId);

        model.addAttribute("times", BaseConstants.WORK_HOURS);

        return "customer/book-step1";
    }

    @GetMapping("/book/step2")
    public String selectServices(@RequestParam("vehicleId") Long clientVehicleId,
                                 @RequestParam("date") String date,
                                 Model model) {
        model.addAttribute("clientVehicleId", clientVehicleId);
        model.addAttribute("selectedDate", date);
        model.addAttribute("allServices", serviceService.getAllServices());

        return "customer/book-step2";
    }

    @GetMapping("/book/step3")
    public String finalStep(@RequestParam("vehicleId") Long clientVehicleId,
                            @RequestParam("date") String date,
                            @RequestParam("serviceIds") List<Long> serviceIds,
                            Model model) {

        CreateVisitDto visitDto = CreateVisitDto.builder()
                .clientVehicleId(clientVehicleId)
                .date(LocalDateTime.parse(date))
                .serviceIds(serviceIds)
                .build();

        model.addAttribute("visitDto", visitDto);
        model.addAttribute("vehicle", clientVehicleService.getVehicleById(clientVehicleId));

        return "customer/book-step3";
    }

    @PostMapping("/add")
    public String processBooking(@ModelAttribute("visitDto") @Valid CreateVisitDto dto,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicle", clientVehicleService.getVehicleById(dto.getClientVehicleId()));
            return "customer/book-step3";
        }

        try {
            visitService.createVisit(dto, userDetails.getId());
            return "redirect:/customer/dashboard?success=visit_booked";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vehicle", clientVehicleService.getVehicleById(dto.getClientVehicleId()));
            return "customer/book-step3";
        }
    }

    @GetMapping("/details/{id}")
    public String showVisitDetails(@PathVariable Long id,
                                   @AuthenticationPrincipal CustomUserDetails userDetails,
                                   Model model) {
        var visit = visitService.getVisitDetails(id, "BGN");

        model.addAttribute("visit", visit);
        return "customer/visit-details";
    }

    @PostMapping("/cancel/{id}")
    public String cancelVisit(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            visitService.deleteVisit(id);
            return "redirect:/customer/dashboard?success=visit_cancelled";
        } catch (Exception e) {
            return "redirect:/customer/visits/details/" + id + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/history")
    public String showHistory(@RequestParam(required = false) Long vehicleId,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model) {

        var visits = visitService.getVisitsByUser(userDetails.getId(), vehicleId);
        var vehicles = clientVehicleService.getMyVehicles(userDetails.getId());

        model.addAttribute("visits", visits);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("selectedVehicleId", vehicleId);

        return "customer/history";
    }
}