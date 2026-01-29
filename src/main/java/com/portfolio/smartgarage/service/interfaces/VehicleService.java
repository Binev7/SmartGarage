package com.portfolio.smartgarage.service.interfaces;

import com.portfolio.smartgarage.dto.vehicle.*;

import java.util.List;

public interface VehicleService {

    List<BrandResponseDto> getAllBrands();

    List<ModelResponseDto> getModelsByBrand(Long brandId);

    List<Integer> getAvailableYearsForModel(Long modelId);

    Long getVehicleIdByModelAndYear(Long modelId, Integer year);

    BrandResponseDto createBrand(BrandRequestDto dto);

    ModelResponseDto createModel(ModelRequestDto dto);

    void addVehicleToCatalog(VehicleCatalogDto dto);

    void archiveVehicleFromCatalog(Long id);

    void archiveModel(Long id);

    void archiveBrand(Long id);


}