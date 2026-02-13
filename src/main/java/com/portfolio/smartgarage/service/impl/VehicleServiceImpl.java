package com.portfolio.smartgarage.service.impl;

import com.portfolio.smartgarage.dto.vehicle.*;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.helper.mapper.BrandMapper;
import com.portfolio.smartgarage.helper.mapper.ModelMapper;
import com.portfolio.smartgarage.helper.util.CreateAndSaveHelper;
import com.portfolio.smartgarage.model.Brand;
import com.portfolio.smartgarage.model.Model;
import com.portfolio.smartgarage.model.Vehicle;
import com.portfolio.smartgarage.repository.BrandRepository;
import com.portfolio.smartgarage.repository.ModelRepository;
import com.portfolio.smartgarage.repository.VehicleRepository;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final VehicleRepository vehicleRepository;
    private final BrandMapper brandMapper;
    private final ModelMapper modelMapper;
    private final CreateAndSaveHelper createAndSaveHelper;


    @Override
    public List<BrandResponseDto> getAllBrands() {
        return brandRepository.findAllByActiveTrue().stream()
                .map(brandMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModelResponseDto> getModelsByBrand(Long brandId) {
        return modelRepository.findAllByBrandIdAndActiveTrue(brandId).stream()
                .map(m -> new ModelResponseDto(m.getId(), m.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getAvailableYearsForModel(Long modelId) {
        return vehicleRepository.findAllByModelIdAndActiveTrue(modelId).stream()
                .map(Vehicle::getYear)
                .distinct()
                .sorted((a, b) -> b - a)
                .collect(Collectors.toList());
    }

    @Override
    public Long getVehicleIdByModelAndYear(Long modelId, Integer year) {
        return vehicleRepository.findByModelIdAndYearAndActiveTrue(modelId, year)
                .map(Vehicle::getId)
                .orElseThrow(() -> new EntityNotFoundException("This vehicle configuration is no longer active in our catalog."));
    }

    @Override
    @Transactional
    public BrandResponseDto createBrand(BrandRequestDto dto) {
        Brand brand = createAndSaveHelper.findOrCreateBrand(dto);
        return brandMapper.toResponseDto(brand);
    }

    @Override
    @Transactional
    public ModelResponseDto createModel(ModelRequestDto dto) {
        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        Model model = createAndSaveHelper.findOrCreateModel(dto, brand);
        return modelMapper.toResponseDto(model);
    }

    @Override
    @Transactional
    public void addVehicleToCatalog(VehicleCatalogDto dto) {
        Model model = modelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new ResourceNotFoundException("Model not found"));

        createAndSaveHelper.findOrCreateVehicle(dto, model);
    }

    @Override
    @Transactional
    public void archiveVehicleFromCatalog(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catalog entry not found"));

        vehicle.setActive(false);
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void archiveModel(Long id) {
        Model model = modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Model not found"));

        model.setActive(false);
        modelRepository.save(model);

        vehicleRepository.deactivateAllByModelId(id);
    }

    @Override
    @Transactional
    public void archiveBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        brand.setActive(false);
        brandRepository.save(brand);

        modelRepository.deactivateAllByBrandId(id);

        vehicleRepository.deactivateAllByBrandId(id);
    }


    @Override
    public List<VehicleResponseDto> getAllCatalogEntries() {
        return vehicleRepository.findAllByActiveTrue().stream()
                .map(v -> new VehicleResponseDto(
                        v.getId(),
                        v.getModel().getBrand().getName(),
                        v.getModel().getName(),
                        v.getYear()))
                .collect(Collectors.toList());
    }
}