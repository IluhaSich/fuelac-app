package com.fuelac.fuelac.api.controller;

import com.fuelac.fuelac.dto.request.OrganizationRequest;
import com.fuelac.fuelac.dto.response.OrganizationResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.service.OrganizationService;
import com.fuelac.fuelac.util.PageableUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/organizations")
public class AdminOrganizationController {

    private final OrganizationService organizationService;

    public AdminOrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> findById(@PathVariable UUID id) {
        return organizationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> create(@Valid @RequestBody OrganizationRequest request) {
        return ResponseEntity.ok(organizationService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponse> update(@PathVariable UUID id, @Valid @RequestBody OrganizationRequest request) {
        return ResponseEntity.ok(organizationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        organizationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<Page<OrganizationResponse>> search(@Valid @RequestBody SearchRequest request) {
        return ResponseEntity.ok(organizationService.search(request));
    }
}
