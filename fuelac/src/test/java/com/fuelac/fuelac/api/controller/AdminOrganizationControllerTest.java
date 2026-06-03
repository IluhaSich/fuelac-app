package com.fuelac.fuelac.api.controller;

import com.fuelac.fuelac.config.JwtTokenProvider;
import com.fuelac.fuelac.infrastructure.AppUserDetailsService;
import com.fuelac.fuelac.dto.request.OrganizationRequest;
import com.fuelac.fuelac.dto.response.OrganizationResponse;
import com.fuelac.fuelac.service.OrganizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminOrganizationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminOrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrganizationService organizationService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrganizationResponse createOrgResponse(UUID id, String name) {
        OrganizationResponse org = new OrganizationResponse();
        org.setId(id);
        org.setName(name);
        org.setOgrn("7720123456");
        org.setCodeOKPO("772001001");
        org.setCodeOKUD("1234567890123");
        org.setAddress("Address");
        return org;
    }

    @Test
    void search_ShouldReturnPage() throws Exception {
        OrganizationResponse org = createOrgResponse(UUID.randomUUID(), "Test Org");
        Page<OrganizationResponse> page = new PageImpl<>(List.of(org), PageRequest.of(0, 20), 1);
        when(organizationService.search(any())).thenReturn(page);

        mockMvc.perform(post("/api/admin/organizations/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new com.fuelac.fuelac.dto.search.SearchRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Test Org"));
    }

    @Test
    void findById_ShouldReturnOrganization() throws Exception {
        UUID id = UUID.randomUUID();
        OrganizationResponse org = createOrgResponse(id, "Test Org");
        when(organizationService.findById(id)).thenReturn(Optional.of(org));

        mockMvc.perform(get("/api/admin/organizations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Org"));
    }

    @Test
    void create_ShouldReturnCreatedOrganization() throws Exception {
        OrganizationRequest request = new OrganizationRequest();
        request.setName("New Org");
        request.setOgrn("7720123456");
        request.setCodeOKPO("772001001");
        request.setCodeOKUD("1234567890123");
        request.setAddress("New Address");
        
        OrganizationResponse response = createOrgResponse(UUID.randomUUID(), "New Org");
        when(organizationService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/admin/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Org"));
    }

    @Test
    void deleteById_ShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(organizationService).deleteById(id);

        mockMvc.perform(delete("/api/admin/organizations/{id}", id))
                .andExpect(status().isNoContent());
    }

}
