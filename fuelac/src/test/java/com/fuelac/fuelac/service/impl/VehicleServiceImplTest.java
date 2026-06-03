package com.fuelac.fuelac.service.impl;

import com.fuelac.fuelac.dto.request.VehicleRequest;
import com.fuelac.fuelac.dto.response.VehicleResponse;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.User;
import com.fuelac.fuelac.model.entity.Vehicle;
import com.fuelac.fuelac.model.enums.UserRole;
import com.fuelac.fuelac.model.enums.VehicleType;
import com.fuelac.fuelac.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private AppUserDetails principal;
    private Organization organization;
    private UUID vehicleId;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setId(UUID.randomUUID());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setOrganization(organization);
        user.setRole(UserRole.ORGANIZATION_MANAGER);
        principal = new AppUserDetails(user);

        vehicleId = UUID.randomUUID();
        vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setOrganization(organization);
        vehicle.setType(VehicleType.LIGHT);
        vehicle.setBrand("Lada");
        vehicle.setModel("Vesta");
        vehicle.setRegistrationNumber("А123БВ777");
    }

    @Test
    void create_ShouldSaveVehicle() {
        VehicleRequest request = new VehicleRequest(VehicleType.TRUCK, "KAMAZ", "65115", 2022, "В456КМ777", "Г02");
        Vehicle saved = new Vehicle();
        saved.setId(UUID.randomUUID());
        saved.setOrganization(organization);
        saved.setBrand("KAMAZ");

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(saved);

        VehicleResponse result = vehicleService.create(request, principal);

        assertNotNull(result);
        assertEquals("KAMAZ", result.getBrand());
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void findById_ShouldReturnVehicle_WhenExists() {
        when(vehicleRepository.findByIdAndOrganization(vehicleId, organization)).thenReturn(Optional.of(vehicle));

        Optional<VehicleResponse> result = vehicleService.findById(vehicleId, principal);

        assertTrue(result.isPresent());
        assertEquals("Lada", result.get().getBrand());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(vehicleRepository.findByIdAndOrganization(vehicleId, organization)).thenReturn(Optional.empty());

        Optional<VehicleResponse> result = vehicleService.findById(vehicleId, principal);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllVehicles() {
        when(vehicleRepository.findByOrganization(organization)).thenReturn(List.of(vehicle));

        List<VehicleResponse> result = vehicleService.findAll(principal);

        assertEquals(1, result.size());
        assertEquals("Lada", result.get(0).getBrand());
    }

    @Test
    void findByType_ShouldReturnFilteredVehicles() {
        when(vehicleRepository.findByOrganizationAndType(organization, VehicleType.LIGHT)).thenReturn(List.of(vehicle));

        List<VehicleResponse> result = vehicleService.findByType(VehicleType.LIGHT, principal);

        assertEquals(1, result.size());
        assertEquals(VehicleType.LIGHT, result.get(0).getType());
    }

    @Test
    void deleteById_ShouldCallRepository() {
        doNothing().when(vehicleRepository).deleteByIdAndOrganization(vehicleId, organization);

        vehicleService.deleteById(vehicleId, principal);

        verify(vehicleRepository).deleteByIdAndOrganization(vehicleId, organization);
    }
}
