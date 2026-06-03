package com.fuelac.fuelac.api.controller;

import com.fuelac.fuelac.dto.request.UserRequest;
import com.fuelac.fuelac.dto.response.UserResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.service.impl.AdminUserService;
import com.fuelac.fuelac.util.PageableUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
        return adminUserService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(adminUserService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id, @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(adminUserService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        adminUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<Page<UserResponse>> search(@Valid @RequestBody SearchRequest request) {
        return ResponseEntity.ok(adminUserService.search(request));
    }
}
