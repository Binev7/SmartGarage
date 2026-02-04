package com.portfolio.smartgarage.controller.mvc.admin;

import com.portfolio.smartgarage.dto.vehicle.*;
import com.portfolio.smartgarage.dto.vehicle.client.ClientVehicleSearchDto;
import com.portfolio.smartgarage.service.interfaces.ClientVehicleService;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employee/vehicles")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
public class AdminVehicleControllerMvc {

    private final ClientVehicleService clientVehicleService;
    private final VehicleService vehicleService;

    @GetMapping
    public String listClientVehicles(@ModelAttribute ClientVehicleSearchDto criteria,
                                     @PageableDefault(size = 10) Pageable pageable,
                                     Model model) {
        model.addAttribute("vehicles", clientVehicleService.searchVehicles(criteria, pageable));
        model.addAttribute("searchCriteria", criteria);
        return "employee/client-vehicles";
    }

    @GetMapping("/catalog")
    public String showCatalog(Model model) {
        model.addAttribute("brands", vehicleService.getAllBrands());

        model.addAttribute("catalogEntries", vehicleService.getAllCatalogEntries());

        model.addAttribute("brandRequest", new BrandRequestDto());
        model.addAttribute("modelRequest", new ModelRequestDto());
        model.addAttribute("catalogRequest", new VehicleCatalogDto());

        return "employee/catalog";
    }

    @GetMapping("/brands/{brandId}/models")
    public ResponseEntity<List<ModelResponseDto>> getModels(@PathVariable Long brandId) {
        return ResponseEntity.ok(vehicleService.getModelsByBrand(brandId));
    }

    @GetMapping("/models/{modelId}/years")
    public ResponseEntity<List<Integer>> getYears(@PathVariable Long modelId) {
        return ResponseEntity.ok(vehicleService.getAvailableYearsForModel(modelId));
    }

    @GetMapping("/lookup")
    public ResponseEntity<Long> getCatalogId(@RequestParam Long modelId, @RequestParam Integer year) {
        return ResponseEntity.ok(vehicleService.getVehicleIdByModelAndYear(modelId, year));
    }

    @PostMapping("/catalog/brands/add")
    public String addBrand(@ModelAttribute BrandRequestDto dto, RedirectAttributes ra) {
        try {
            vehicleService.createBrand(dto);
            ra.addFlashAttribute("message", "Brand '" + dto.getName() + "' created.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/vehicles/catalog";
    }

    @PostMapping("/catalog/models/add")
    public String addModel(@ModelAttribute ModelRequestDto dto, RedirectAttributes ra) {
        try {
            vehicleService.createModel(dto);
            ra.addFlashAttribute("message", "Model added.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/vehicles/catalog";
    }

    @PostMapping("/catalog/entries/add")
    public String addYearToCatalog(@ModelAttribute("catalogRequest") VehicleCatalogDto dto, RedirectAttributes ra) {
        try {
            vehicleService.addVehicleToCatalog(dto);
            ra.addFlashAttribute("message", "Successfully added " + dto.getYear() + " to the catalog!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/employee/vehicles/catalog";
    }

    @PostMapping("/catalog/archive-brand/{id}")
    public String archiveBrand(@PathVariable Long id, RedirectAttributes ra) {
        vehicleService.archiveBrand(id);
        ra.addFlashAttribute("message", "Brand archived.");
        return "redirect:/employee/vehicles/catalog";
    }
}