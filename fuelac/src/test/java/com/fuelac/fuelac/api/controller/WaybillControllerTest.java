package com.fuelac.fuelac.api.controller;

import com.fuelac.fuelac.config.JwtTokenProvider;
import com.fuelac.fuelac.infrastructure.AppUserDetailsService;
import com.fuelac.fuelac.dto.request.WaybillRequest;
import com.fuelac.fuelac.dto.response.WaybillResponse;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.User;
import com.fuelac.fuelac.model.enums.*;
import com.fuelac.fuelac.service.ExcelReportService;
import com.fuelac.fuelac.service.WaybillService;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

@WebMvcTest(WaybillController.class)
@AutoConfigureMockMvc(addFilters = false)
class WaybillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WaybillService waybillService;

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
        user.setRole(UserRole.DISPATCHER);
        user.setOrganization(org);
        principal = new AppUserDetails(user);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(principal);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private WaybillResponse createWaybillResponse(UUID id, String number, WaybillStatus status, Double fuelStart) {
        WaybillResponse r = new WaybillResponse();
        r.setId(id);
        r.setNumber(number);
        r.setStatus(status);
        r.setVehicleType(VehicleType.LIGHT);
        r.setMessageType(MessageType.URBAN);
        r.setTransportationType(TransportationType.PASSENGER);
        r.setWorkStartDate(LocalDateTime.now());
        r.setFuelStart(fuelStart);
        r.setOdometerStart(10000.0);
        r.setMachineHoursStart(100.0);
        r.setEngineHoursStart(200.0);
        r.setCreatedAt(LocalDateTime.now());
        r.setUpdatedAt(LocalDateTime.now());
        return r;
    }

    @Test
    void search_ShouldReturnList() throws Exception {
        WaybillResponse waybill = createWaybillResponse(UUID.randomUUID(), "ПЛ-1", WaybillStatus.OPEN, 50.0);
        Page<WaybillResponse> page = new PageImpl<>(List.of(waybill), PageRequest.of(0, 20), 1);
        when(waybillService.search(any(), any())).thenReturn(page);

        mockMvc.perform(post("/api/waybills/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new com.fuelac.fuelac.dto.search.SearchRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content[0].number").value("ПЛ-1"));
    }

    @Test
    void search_ShouldReturnPaginatedResults_With25Items() throws Exception {
        java.util.List<WaybillResponse> waybills = new java.util.ArrayList<>();
        for (int i = 0; i < 25; i++) {
            waybills.add(createWaybillResponse(UUID.randomUUID(), String.format("ПЛ-%03d", i + 1), WaybillStatus.CLOSED, 40.0 + i));
        }
        Page<WaybillResponse> page = new PageImpl<>(waybills.subList(0, 10), PageRequest.of(0, 10), 25);
        when(waybillService.search(any(), any())).thenReturn(page);

        mockMvc.perform(post("/api/waybills/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new com.fuelac.fuelac.dto.search.SearchRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(10))
                .andExpect(jsonPath("$.totalElements").value(25))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.content[0].number").value("ПЛ-001"));
    }

    @Test
    void findById_ShouldReturnWaybill() throws Exception {
        UUID id = UUID.randomUUID();
        WaybillResponse waybill = new WaybillResponse();
        waybill.setId(id);
        waybill.setNumber("ПЛ-2");
        waybill.setStatus(WaybillStatus.CLOSED);
        waybill.setVehicleType(VehicleType.TRUCK);
        waybill.setMessageType(MessageType.INTERCITY);
        waybill.setTransportationType(TransportationType.CARGO);
        waybill.setWorkStartDate(LocalDateTime.now());
        waybill.setWorkEndDate(LocalDateTime.now().plusHours(8));
        waybill.setFuelStart(50.0);
        waybill.setFuelEnd(20.0);
        waybill.setFuelRefilled(30.0);
        waybill.setOdometerStart(10000.0);
        waybill.setOdometerEnd(10150.0);
        waybill.setClosedAt(LocalDateTime.now().plusHours(8));
        waybill.setCreatedAt(LocalDateTime.now());
        waybill.setUpdatedAt(LocalDateTime.now());
        when(waybillService.findById(eq(id), any())).thenReturn(Optional.of(waybill));

        mockMvc.perform(get("/api/waybills/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("ПЛ-2"));
    }

    @Test
    void create_ShouldReturnCreatedWaybill() throws Exception {
        WaybillRequest request = new WaybillRequest();
        request.setNumber("ПЛ-3");
        request.setStatus(WaybillStatus.OPEN);
        request.setVehicleType(VehicleType.LIGHT);
        request.setMessageType(MessageType.URBAN);
        request.setTransportationType(TransportationType.PASSENGER);
        request.setDriverId(UUID.randomUUID());
        request.setVehicleId(UUID.randomUUID());
        request.setWorkStartDate(LocalDateTime.now());
        request.setFuelStart(50.0);
        request.setOdometerStart(10000.0);
        request.setMachineHoursStart(100.0);
        request.setEngineHoursStart(200.0);
        WaybillResponse response = createWaybillResponse(UUID.randomUUID(), "ПЛ-3", WaybillStatus.OPEN, 50.0);
        when(waybillService.create(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/waybills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("ПЛ-3"));
    }

}
