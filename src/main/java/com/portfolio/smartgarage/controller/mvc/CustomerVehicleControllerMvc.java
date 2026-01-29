package com.portfolio.smartgarage.controller.mvc;

import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleResponseDto;
import com.portfolio.smartgarage.dto.visit.VisitViewDto;
import com.portfolio.smartgarage.security.CustomUserDetails;
import com.portfolio.smartgarage.service.interfaces.ClientVehicleService;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import com.portfolio.smartgarage.service.interfaces.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/customer/vehicles")
@RequiredArgsConstructor
public class CustomerVehicleControllerMvc {

    private final VehicleService vehicleService;
    private final ClientVehicleService clientVehicleService;
    private final VisitService visitService;

    @GetMapping("/add/step1")
    public String selectBrand(Model model) {
        model.addAttribute("brands", vehicleService.getAllBrands());
        return "customer/add-vehicle-step1";
    }

    @GetMapping("/add/step2")
    public String selectModel(@RequestParam Long brandId, Model model) {
        model.addAttribute("models", vehicleService.getModelsByBrand(brandId));
        return "customer/add-vehicle-step2";
    }

    @GetMapping("/add/step3")
    public String selectYear(@RequestParam Long modelId, Model model) {
        model.addAttribute("years", vehicleService.getAvailableYearsForModel(modelId));
        model.addAttribute("modelId", modelId);
        return "customer/add-vehicle-step3";
    }

    @GetMapping("/add/step4")
    public String enterDetails(@RequestParam Long modelId,
                               @RequestParam Integer year,
                               Model model) {

        Long catalogVehicleId = vehicleService.getVehicleIdByModelAndYear(modelId, year);

        ClientVehicleRequestDto vehicleRequest = new ClientVehicleRequestDto();
        vehicleRequest.setVehicleId(catalogVehicleId);

        model.addAttribute("selectedYear", year);
        model.addAttribute("vehicleRequest", vehicleRequest);

        return "customer/add-vehicle-step4";
    }

    @PostMapping("/add")
    public String saveVehicle(
            @ModelAttribute("vehicleRequest") @Valid ClientVehicleRequestDto request,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "customer/add-vehicle-step4";
        }

        try {
            clientVehicleService.registerVehicle(request, userDetails.getId());
            return "redirect:/customer/dashboard?success=vehicle_added";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "customer/add-vehicle-step4";
        }
    }

    @GetMapping("/details")
    public String vehicleDetails(@RequestParam Long id,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model) {

        ClientVehicleResponseDto vehicle = clientVehicleService.getVehicleById(id);

        if (!vehicle.getOwnerId().equals(userDetails.getId())) {
            return "redirect:/customer/dashboard?error=unauthorized";
        }

        var visits = visitService.getVisitsByUser(userDetails.getId(), id);

        model.addAttribute("v", vehicle);
        model.addAttribute("visits", visits);

        return "customer/vehicle-details";
    }

    @GetMapping("/visits/{id}")
    public String showVisitDetails(@PathVariable Long id,
                                   @RequestParam(required = false, defaultValue = "BGN") String currency,
                                   Model model) {

        VisitViewDto visit = visitService.getVisitDetails(id, currency);

        model.addAttribute("visit", visit);
        model.addAttribute("selectedCurrency", currency);
        return "customer/vehicle-details";
    }

    @PostMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            clientVehicleService.deleteVehicle(id, userDetails.getId());
            return "redirect:/customer/dashboard?success=vehicle_deleted";
        } catch (AccessDeniedException ade) {
            return "redirect:/customer/dashboard?error=delete_unauthorized";
        } catch (Exception e) {
            return "redirect:/customer/dashboard?error=delete_failed";
        }
    }
}