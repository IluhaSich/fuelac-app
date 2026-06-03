package com.fuelac.fuelac.api.controller;

import com.fuelac.fuelac.config.JwtTokenProvider;
import com.fuelac.fuelac.infrastructure.AppUserDetailsService;
import com.fuelac.fuelac.dto.request.VehicleFuelNormsRequest;
import com.fuelac.fuelac.dto.response.VehicleFuelNormsResponse;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.User;
import com.fuelac.fuelac.model.enums.UserRole;
import com.fuelac.fuelac.service.VehicleFuelNormsService;
import com.fuelac.fuelac.service.export.ExcelExportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleFuelNormsController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehicleFuelNormsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleFuelNormsService normsService;

    @MockitoBean
    private ExcelExportService excelExportService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private AppUserDetails principal;

    @BeforeEach
    void setUp() {
        Organization org = new Organization();
        org.setId(UUID.randomUUID());
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@fuelac.ru");
        user.setPassword("password");
        user.setRole(UserRole.ORGANIZATION_MANAGER);
        user.setOrganization(org);
        principal = new AppUserDetails(user);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void search_ShouldReturnPage() throws Exception {
        VehicleFuelNormsResponse norm = new VehicleFuelNormsResponse(UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2024, 1, 1), LocalDate.of(2025, 12, 31), "Test norm", 8.5, null, null, 0.8, 0.5, 0.3, LocalDateTime.now(), LocalDateTime.now());
        Page<VehicleFuelNormsResponse> page = new PageImpl<>(List.of(norm), PageRequest.of(0, 20), 1);
        when(normsService.search(any(), any())).thenReturn(page);

        mockMvc.perform(post("/api/fuel-norms/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new com.fuelac.fuelac.dto.search.SearchRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content[0].description").value("Test norm"));
    }

    @Test
    void findById_ShouldReturnNorm() throws Exception {
        UUID id = UUID.randomUUID();
        VehicleFuelNormsResponse norm = new VehicleFuelNormsResponse(id, UUID.randomUUID(), LocalDate.of(2024, 1, 1), LocalDate.of(2025, 12, 31), "Test norm", 8.5, null, null, 0.8, 0.5, 0.3, LocalDateTime.now(), LocalDateTime.now());
        when(normsService.findById(eq(id), any())).thenReturn(Optional.of(norm));

        mockMvc.perform(get("/api/fuel-norms/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Test norm"));
    }

    @Test
    void create_ShouldReturnCreatedNorm() throws Exception {
        VehicleFuelNormsRequest request = new VehicleFuelNormsRequest(UUID.randomUUID(), LocalDate.of(2024, 1, 1), LocalDate.of(2025, 12, 31), "New norm", 8.5, null, null, 0.8, 0.5, 0.3);
        VehicleFuelNormsResponse response = new VehicleFuelNormsResponse(UUID.randomUUID(), UUID.randomUUID(), LocalDate.of(2024, 1, 1), LocalDate.of(2025, 12, 31), "New norm", 8.5, null, null, 0.8, 0.5, 0.3, LocalDateTime.now(), LocalDateTime.now());
        when(normsService.create(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/fuel-norms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("New norm"));
    }

    @Test
    void deleteById_ShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(normsService).deleteById(eq(id), any());

        mockMvc.perform(delete("/api/fuel-norms/{id}", id))
                .andExpect(status().isNoContent());
    }
}
