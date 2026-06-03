package com.fuelac.fuelac.api.controller;

import com.fuelac.fuelac.config.JwtTokenProvider;
import com.fuelac.fuelac.infrastructure.AppUserDetailsService;
import com.fuelac.fuelac.dto.request.DriverRequest;
import com.fuelac.fuelac.dto.response.DriverResponse;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.User;
import com.fuelac.fuelac.model.enums.UserRole;
import com.fuelac.fuelac.service.DriverService;
import com.fuelac.fuelac.service.export.ExcelExportService;
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

@WebMvcTest(DriverController.class)
@AutoConfigureMockMvc(addFilters = false)
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DriverService driverService;

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
    void search_ShouldReturnList() throws Exception {
        DriverResponse driver = new DriverResponse(UUID.randomUUID(), "001", "Иванов", "Иван", "Иванович", "123-456-789 01", "77 01 123456", "B", LocalDate.now(), LocalDate.now().plusYears(10), true, LocalDateTime.now(), LocalDateTime.now());
        driver.setFullName("Иванов Иван Иванович");
        driver.setInitials("Иванов И.И.");
        Page<DriverResponse> page = new PageImpl<>(List.of(driver), PageRequest.of(0, 20), 1);
        when(driverService.search(any(), any())).thenReturn(page);

        mockMvc.perform(post("/api/drivers/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new com.fuelac.fuelac.dto.search.SearchRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content[0].fullName").value("Иванов Иван Иванович"));
    }

    @Test
    void findById_ShouldReturnDriver() throws Exception {
        UUID id = UUID.randomUUID();
        DriverResponse driver = new DriverResponse(id, "001", "Иванов", "Иван", "Иванович", "123-456-789 01", "77 01 123456", "B", LocalDate.now(), LocalDate.now().plusYears(10), true, LocalDateTime.now(), LocalDateTime.now());
        driver.setFullName("Иванов Иван Иванович");
        driver.setInitials("Иванов И.И.");
        when(driverService.findById(eq(id), any())).thenReturn(Optional.of(driver));

        mockMvc.perform(get("/api/drivers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Иванов Иван Иванович"));
    }

    @Test
    void create_ShouldReturnCreatedDriver() throws Exception {
        DriverRequest request = new DriverRequest("001", "Иванов", "Иван", "Иванович", "123-456-789 01", "77 01 123456", "B", LocalDate.now(), LocalDate.now().plusYears(10));
        DriverResponse response = new DriverResponse(UUID.randomUUID(), "001", "Иванов", "Иван", "Иванович", "123-456-789 01", "77 01 123456", "B", LocalDate.now(), LocalDate.now().plusYears(10), true, LocalDateTime.now(), LocalDateTime.now());
        response.setFullName("Иванов Иван Иванович");
        response.setInitials("Иванов И.И.");
        when(driverService.create(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Иванов Иван Иванович"));
    }

    @Test
    void deleteById_ShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(driverService).deleteById(eq(id), any());

        mockMvc.perform(delete("/api/drivers/{id}", id))
                .andExpect(status().isNoContent());
    }
}
