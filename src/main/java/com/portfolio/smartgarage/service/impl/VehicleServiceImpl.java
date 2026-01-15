package com.portfolio.smartgarage.service.impl;

import com.portfolio.smartgarage.dto.vehicle.*;
import com.portfolio.smartgarage.exception.ResourceAlreadyExistsException;
import com.portfolio.smartgarage.exception.ResourceNotFoundException;
import com.portfolio.smartgarage.helper.mapper.BrandMapper;
import com.portfolio.smartgarage.helper.mapper.ModelMapper;
import com.portfolio.smartgarage.helper.mapper.VehicleMapper;
import com.portfolio.smartgarage.model.Brand;
import com.portfolio.smartgarage.model.Model;
import com.portfolio.smartgarage.model.Vehicle;
import com.portfolio.smartgarage.repository.BrandRepository;
import com.portfolio.smartgarage.repository.ModelRepository;
import com.portfolio.smartgarage.repository.VehicleRepository;
import com.portfolio.smartgarage.service.interfaces.VehicleService;
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
    private final VehicleMapper vehicleMapper;
    private final BrandMapper brandMapper;
    private final ModelMapper modelMapper;


    @Override
    public List<BrandResponseDto> getAllBrands() {
        return brandRepository.findAllByActiveTrue().stream()
                .map(brandMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<ModelResponseDto> getModelsByBrand(Long brandId) {
        return modelRepository.findAllByBrandIdAndActiveTrue(brandId).stream()
                .map(m -> new ModelResponseDto(m.getId(), m.getName()))
                .collect(Collectors.toList());
    }

    public List<Integer> getAvailableYearsForModel(Long modelId) {
        return vehicleRepository.findAllByModelId(modelId).stream()
                .map(Vehicle::getYear)
                .distinct()
                .sorted((a, b) -> b - a)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BrandResponseDto createBrand(BrandRequestDto dto) {
        brandRepository.findByNameIgnoreCase(dto.getName()).ifPresent(b -> {
            throw new ResourceAlreadyExistsException("Brand already exists");
        });

        Brand brand = brandMapper.toEntity(dto);
        Brand saved = brandRepository.save(brand);
        return brandMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ModelResponseDto createModel(ModelRequestDto dto) {
        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        if (modelRepository.existsByNameAndBrandId(dto.getName(), brand.getId())) {
            throw new ResourceAlreadyExistsException("Model already exists");
        }

        Model model = modelMapper.toEntity(dto, brand);
        Model saved = modelRepository.save(model);
        return modelMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void addVehicleToCatalog(VehicleCatalogDto dto) {

        Model model = modelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new ResourceNotFoundException("Model not found"));

        if (vehicleRepository.existsByModelIdAndYear(dto.getModelId(), dto.getYear())) {
            throw new ResourceAlreadyExistsException("This model year already exists in catalog");
        }

        Vehicle vehicle = Vehicle.builder()
                .model(model)
                .year(dto.getYear())
                .build();

        vehicleRepository.save(vehicle);
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
                .orElseThrow(() -> new ResourceNotFoundException("Model not found"));

        brand.setActive(false);
        brandRepository.save(brand);

        vehicleRepository.deactivateAllByModelId(id);
    }
}