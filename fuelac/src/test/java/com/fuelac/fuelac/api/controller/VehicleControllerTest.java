package com.fuelac.fuelac.api.controller;

import com.fuelac.fuelac.config.JwtTokenProvider;
import com.fuelac.fuelac.infrastructure.AppUserDetailsService;
import com.fuelac.fuelac.dto.request.VehicleRequest;
import com.fuelac.fuelac.dto.response.VehicleResponse;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.User;
import com.fuelac.fuelac.model.enums.UserRole;
import com.fuelac.fuelac.model.enums.VehicleType;
import com.fuelac.fuelac.service.VehicleService;
import com.fuelac.fuelac.service.export.ExcelExportService;
import com.fuelac.fuelac.service.ExcelReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private ExcelExportService excelExportService;

    @MockitoBean
    private ExcelReportService excelReportService;

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
    void search_ShouldReturnList() throws Exception {
        VehicleResponse vehicle = new VehicleResponse(UUID.randomUUID(), VehicleType.LIGHT, "Lada", "Vesta", 2023, "А123БВ777", "Г01", LocalDateTime.now(), LocalDateTime.now());
        Page<VehicleResponse> page = new PageImpl<>(List.of(vehicle), PageRequest.of(0, 20), 1);
        when(vehicleService.search(any(), any())).thenReturn(page);

        mockMvc.perform(post("/api/vehicles/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new com.fuelac.fuelac.dto.search.SearchRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content[0].brand").value("Lada"));
    }

    @Test
    void findById_ShouldReturnVehicle() throws Exception {
        UUID id = UUID.randomUUID();
        VehicleResponse vehicle = new VehicleResponse(id, VehicleType.TRUCK, "KAMAZ", "65115", 2022, "В456КМ777", "Г02", LocalDateTime.now(), LocalDateTime.now());
        when(vehicleService.findById(eq(id), any())).thenReturn(Optional.of(vehicle));

        mockMvc.perform(get("/api/vehicles/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("KAMAZ"));
    }

    @Test
    void create_ShouldReturnCreatedVehicle() throws Exception {
        VehicleRequest request = new VehicleRequest(VehicleType.LIGHT, "Toyota", "Camry", 2023, "К012МР777", "Г04");
        VehicleResponse response = new VehicleResponse(UUID.randomUUID(), VehicleType.LIGHT, "Toyota", "Camry", 2023, "К012МР777", "Г04", LocalDateTime.now(), LocalDateTime.now());
        when(vehicleService.create(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Toyota"));
    }

    @Test
    void deleteById_ShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(vehicleService).deleteById(eq(id), any());

        mockMvc.perform(delete("/api/vehicles/{id}", id))
                .andExpect(status().isNoContent());
    }
}
