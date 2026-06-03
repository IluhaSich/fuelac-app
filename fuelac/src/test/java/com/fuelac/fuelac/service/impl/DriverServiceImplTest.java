package com.fuelac.fuelac.service.impl;

import com.fuelac.fuelac.dto.request.DriverRequest;
import com.fuelac.fuelac.dto.response.DriverResponse;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.Driver;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.User;
import com.fuelac.fuelac.model.enums.UserRole;
import com.fuelac.fuelac.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverServiceImpl driverService;

    private AppUserDetails principal;
    private Organization organization;
    private UUID driverId;
    private Driver driver;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setId(UUID.randomUUID());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setOrganization(organization);
        user.setRole(UserRole.ORGANIZATION_MANAGER);
        principal = new AppUserDetails(user);

        driverId = UUID.randomUUID();
        driver = new Driver();
        driver.setId(driverId);
        driver.setOrganization(organization);
        driver.setPersonnelNumber("001");
        driver.setLastName("Иванов");
        driver.setFirstName("Иван");
        driver.setPatronymic("Иванович");
    }

    @Test
    void create_ShouldSaveDriver() {
        DriverRequest request = new DriverRequest("002", "Иванов", "Иван", "Иванович", "123-456-789 02", "77 01 234567", "C", LocalDate.now(), LocalDate.now().plusYears(10));
        Driver saved = new Driver();
        saved.setId(UUID.randomUUID());
        saved.setOrganization(organization);
        saved.setPersonnelNumber("002");
        saved.setLastName("Петров");
        saved.setFirstName("Иван");

        when(driverRepository.save(any(Driver.class))).thenReturn(saved);

        DriverResponse result = driverService.create(request, principal);

        assertNotNull(result);
        assertEquals("Петров Иван", result.getFullName());
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    void findById_ShouldReturnDriver_WhenExists() {
        when(driverRepository.findByIdAndOrganization(driverId, organization)).thenReturn(Optional.of(driver));

        Optional<DriverResponse> result = driverService.findById(driverId, principal);

        assertTrue(result.isPresent());
        assertEquals("Иванов Иван Иванович", result.get().getFullName());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(driverRepository.findByIdAndOrganization(driverId, organization)).thenReturn(Optional.empty());

        Optional<DriverResponse> result = driverService.findById(driverId, principal);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllDrivers() {
        when(driverRepository.findByOrganization(organization)).thenReturn(List.of(driver));

        List<DriverResponse> result = driverService.findAll(principal);

        assertEquals(1, result.size());
        assertEquals("Иванов Иван Иванович", result.get(0).getFullName());
    }

    @Test
    void deleteById_ShouldCallRepository() {
        doNothing().when(driverRepository).deleteByIdAndOrganization(driverId, organization);

        driverService.deleteById(driverId, principal);

        verify(driverRepository).deleteByIdAndOrganization(driverId, organization);
    }

    @Test
    void findByPersonnelNumber_ShouldReturnDriver() {
        when(driverRepository.findByPersonnelNumberAndOrganization("001", organization)).thenReturn(Optional.of(driver));

        Optional<DriverResponse> result = driverService.findByPersonnelNumber("001", principal);

        assertTrue(result.isPresent());
        assertEquals("001", result.get().getPersonnelNumber());
    }
}
