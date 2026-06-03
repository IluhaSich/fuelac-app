package com.fuelac.fuelac.api.controller;

import com.fuelac.fuelac.dto.request.VehicleFuelNormsRequest;
import com.fuelac.fuelac.dto.response.VehicleFuelNormsResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.service.VehicleFuelNormsService;
import com.fuelac.fuelac.service.export.ExcelExportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/fuel-norms")
public class VehicleFuelNormsController {

    private final VehicleFuelNormsService normsService;
    private final ExcelExportService excelExportService;

    public VehicleFuelNormsController(VehicleFuelNormsService normsService, ExcelExportService excelExportService) {
        this.normsService = normsService;
        this.excelExportService = excelExportService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleFuelNormsResponse> findById(@PathVariable UUID id, @AuthenticationPrincipal AppUserDetails principal) {
        return normsService.findById(id, principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VehicleFuelNormsResponse> create(@Valid @RequestBody VehicleFuelNormsRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(normsService.create(request, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleFuelNormsResponse> update(@PathVariable UUID id, @Valid @RequestBody VehicleFuelNormsRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(normsService.update(id, request, principal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id, @AuthenticationPrincipal AppUserDetails principal) {
        normsService.deleteById(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<Page<VehicleFuelNormsResponse>> search(
            @Valid @RequestBody SearchRequest request,
            @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(normsService.search(request, principal));
    }

    @GetMapping("/active")
    public ResponseEntity<VehicleFuelNormsResponse> findActiveByVehicleAndDate(
            @RequestParam UUID vehicleId,
            @RequestParam(required = false) LocalDate date,
            @AuthenticationPrincipal AppUserDetails principal) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return normsService.findActiveByVehicleAndDate(vehicleId, targetDate, principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/export")
    public ResponseEntity<ByteArrayResource> exportVehicleFuelNorms(
            @Valid @RequestBody SearchRequest request,
            @AuthenticationPrincipal AppUserDetails principal) {
        byte[] excelData = excelExportService.exportVehicleFuelNorms(request, principal);
        ByteArrayResource resource = new ByteArrayResource(excelData);
        String filename = String.format("Нормы_расхода_топлива_%s.xlsx",
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(excelData.length)
                .body(resource);
    }
}
