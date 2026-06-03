package com.fuelac.fuelac.api.controller;

import com.fuelac.fuelac.dto.request.VehicleRequest;
import com.fuelac.fuelac.dto.response.VehicleResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.dto.utility.VehicleReadings;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.enums.VehicleType;
import com.fuelac.fuelac.service.ExcelReportService;
import com.fuelac.fuelac.service.VehicleService;
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
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final ExcelExportService excelExportService;
    private final ExcelReportService excelReportService;

    public VehicleController(VehicleService vehicleService, ExcelExportService excelExportService, ExcelReportService excelReportService) {
        this.vehicleService = vehicleService;
        this.excelExportService = excelExportService;
        this.excelReportService = excelReportService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> findById(@PathVariable UUID id, @AuthenticationPrincipal AppUserDetails principal) {
        return vehicleService.findById(id, principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(vehicleService.create(request, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(@PathVariable UUID id, @Valid @RequestBody VehicleRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(vehicleService.update(id, request, principal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id, @AuthenticationPrincipal AppUserDetails principal) {
        vehicleService.deleteById(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<Page<VehicleResponse>> search(
            @Valid @RequestBody SearchRequest request,
            @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(vehicleService.search(request, principal));
    }

    @GetMapping("/{id}/last-readings")
    public ResponseEntity<VehicleReadings> getLastReadings(@PathVariable UUID id, @AuthenticationPrincipal AppUserDetails principal) {
        return vehicleService.getLastWaybillReadings(id, principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/type")
    public ResponseEntity<VehicleType> getVehicleType(@PathVariable UUID id, @AuthenticationPrincipal AppUserDetails principal) {
        return vehicleService.getVehicleTypeById(id, principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/export")
    public ResponseEntity<ByteArrayResource> exportVehicles(
            @Valid @RequestBody SearchRequest request,
            @AuthenticationPrincipal AppUserDetails principal) {
        byte[] excelData = excelExportService.exportVehicles(request, principal);
        ByteArrayResource resource = new ByteArrayResource(excelData);
        String filename = String.format("Транспортные_средства_%s.xlsx",
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(excelData.length)
                .body(resource);
    }

    @GetMapping("/{id}/excel-report")
    public ResponseEntity<ByteArrayResource> downloadExcelReport(
            @PathVariable UUID id,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @AuthenticationPrincipal AppUserDetails principal) {
        byte[] excelData = excelReportService.generateFuelReport(principal, id, from, to);
        ByteArrayResource resource = new ByteArrayResource(excelData);
        String vehicleInfo = vehicleService.findById(id, principal)
                .map(v -> v.getRegistrationNumber() + "_" + v.getBrand() + "_" + v.getModel())
                .orElse(id.toString().substring(0, 8));
        // Убираем символы, запрещённые в именах файлов
        vehicleInfo = vehicleInfo.replaceAll("[\\\\/:*?\"<>|]", "_");
        String filename = String.format("Отчет_по_ТС_%s_%s_%s.xlsx",
                vehicleInfo,
                from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(excelData.length)
                .body(resource);
    }
}
