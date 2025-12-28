package com.portfolio.smartgarage.controller;

import com.portfolio.smartgarage.dto.CreateVisitDto;
import com.portfolio.smartgarage.dto.VisitViewDto;
import com.portfolio.smartgarage.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<VisitViewDto> createVisit(@Valid @RequestBody CreateVisitDto dto) {
        VisitViewDto createdVisit = visitService.createVisit(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVisit);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VisitViewDto>> getVisitsByUser(@PathVariable Long userId) {
        List<VisitViewDto> visits = visitService.getVisitsByUser(userId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<VisitViewDto>> getVisitsByVehicle(@PathVariable Long vehicleId) {
        List<VisitViewDto> visits = visitService.getVisitsByVehicle(vehicleId);
        return ResponseEntity.ok(visits);
    }

    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> deleteVisit(@PathVariable Long visitId) {
        visitService.deleteVisit(visitId);
        return ResponseEntity.noContent().build();
    }
}
