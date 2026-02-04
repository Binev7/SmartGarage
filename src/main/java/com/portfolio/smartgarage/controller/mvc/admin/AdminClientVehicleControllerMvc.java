package com.portfolio.smartgarage.controller.mvc.admin;

import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleRequestDto;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleResponseDto;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleSearchDto;
import com.portfolio.smartgarage.service.interfaces.ClientVehicleService;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee/client-vehicles")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class AdminClientVehicleControllerMvc {

    private final ClientVehicleService clientVehicleService;
    private final VehicleService vehicleService;

    @GetMapping
    public String listVehicles(
            @ModelAttribute("searchCriteria") ClientVehicleSearchDto criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<ClientVehicleResponseDto> vehiclePage = clientVehicleService.searchVehicles(criteria, PageRequest.of(page, size));

        model.addAttribute("vehicles", vehiclePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vehiclePage.getTotalPages());
        model.addAttribute("brands", vehicleService.getAllBrands());

        return "employee/client-vehicles";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ClientVehicleResponseDto dto = clientVehicleService.getVehicleById(id);

        ClientVehicleRequestDto requestDto = new ClientVehicleRequestDto(
                dto.getLicensePlate(),
                dto.getVin(),
                null
        );

        model.addAttribute("vehicleRequest", requestDto);
        model.addAttribute("vehicleId", id);
        model.addAttribute("brands", vehicleService.getAllBrands());

        return "employee/client-vehicle-edit";
    }

    @PostMapping("/edit/{id}")
    public String processEdit(
            @PathVariable Long id,
            @Valid @ModelAttribute("vehicleRequest") ClientVehicleRequestDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicleId", id);
            model.addAttribute("brands", vehicleService.getAllBrands());
            return "employee/client-vehicle-edit";
        }

        try {
            clientVehicleService.updateVehicle(id, dto);
            redirectAttributes.addFlashAttribute("message", "Vehicle updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/employee/client-vehicles/edit/" + id;
        }

        return "redirect:/employee/client-vehicles";
    }
}