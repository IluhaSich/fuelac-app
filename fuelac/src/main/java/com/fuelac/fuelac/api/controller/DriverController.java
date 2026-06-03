package com.fuelac.fuelac.api.controller;

import com.fuelac.fuelac.dto.request.DriverRequest;
import com.fuelac.fuelac.dto.response.DriverResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.service.DriverService;
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
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;
    private final ExcelExportService excelExportService;

    public DriverController(DriverService driverService, ExcelExportService excelExportService) {
        this.driverService = driverService;
        this.excelExportService = excelExportService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> findById(@PathVariable UUID id, @AuthenticationPrincipal AppUserDetails principal) {
        return driverService.findById(id, principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody DriverRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(driverService.create(request, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> update(@PathVariable UUID id, @Valid @RequestBody DriverRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(driverService.update(id, request, principal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id, @AuthenticationPrincipal AppUserDetails principal) {
        driverService.deleteById(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<Page<DriverResponse>> search(@RequestBody SearchRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(driverService.search(request, principal));
    }

    @PostMapping("/export")
    public ResponseEntity<ByteArrayResource> exportDrivers(
            @Valid @RequestBody SearchRequest request,
            @AuthenticationPrincipal AppUserDetails principal) {
        byte[] excelData = excelExportService.exportDrivers(request, principal);
        ByteArrayResource resource = new ByteArrayResource(excelData);
        String filename = String.format("Водители_%s.xlsx",
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(excelData.length)
                .body(resource);
    }
}
