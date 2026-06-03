package com.fuelac.fuelac.api.controller;

import com.fuelac.fuelac.dto.report.FuelConsumptionReport;
import com.fuelac.fuelac.dto.request.WaybillRequest;
import com.fuelac.fuelac.dto.response.WaybillResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.service.ExcelReportService;
import com.fuelac.fuelac.service.WaybillService;
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
@RequestMapping("/api/waybills")
public class WaybillController {

    private final WaybillService waybillService;
    private final ExcelReportService excelReportService;

    public WaybillController(WaybillService waybillService, ExcelReportService excelReportService) {
        this.waybillService = waybillService;
        this.excelReportService = excelReportService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaybillResponse> findById(@PathVariable UUID id, @AuthenticationPrincipal AppUserDetails principal) {
        return waybillService.findById(id, principal)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<WaybillResponse> create(@Valid @RequestBody WaybillRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(waybillService.create(request, principal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaybillResponse> update(@PathVariable UUID id, @Valid @RequestBody WaybillRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(waybillService.update(id, request, principal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id, @AuthenticationPrincipal AppUserDetails principal) {
        waybillService.deleteById(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<WaybillResponse> closeWaybill(@PathVariable UUID id, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(waybillService.closeWaybill(id, principal));
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<FuelConsumptionReport> generateReport(
            @PathVariable UUID id,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(waybillService.generateReport(id, from, to, principal));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<WaybillResponse>> search(@RequestBody SearchRequest request, @AuthenticationPrincipal AppUserDetails principal) {
        return ResponseEntity.ok(waybillService.search(request, principal));
    }

    @GetMapping("/report/excel")
    public ResponseEntity<ByteArrayResource> downloadExcelReport(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @AuthenticationPrincipal AppUserDetails principal) {
        byte[] data = excelReportService.exportWaybills(principal, from, to);
        String filename = "waybills_" + (from != null ? from.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : "all")
                + "_" + (to != null ? to.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : "all") + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(data));
    }
}
