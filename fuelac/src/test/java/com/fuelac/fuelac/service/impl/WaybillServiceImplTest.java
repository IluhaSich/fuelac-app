package com.fuelac.fuelac.service.impl;

import com.fuelac.fuelac.dto.request.WaybillRequest;
import com.fuelac.fuelac.dto.response.WaybillResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.*;
import com.fuelac.fuelac.model.enums.*;
import com.fuelac.fuelac.repository.*;
import com.fuelac.fuelac.repository.specification.GenericSpecification;
import com.fuelac.fuelac.service.calculator.FuelCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaybillServiceImplTest {

    @Mock
    private WaybillRepository waybillRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleFuelNormsRepository normsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FuelCalculationService fuelCalculationService;

    @Mock
    private GenericSpecification genericSpecification;

    @InjectMocks
    private WaybillServiceImpl waybillService;

    private AppUserDetails principal;
    private Organization organization;
    private UUID waybillId;
    private Waybill waybill;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setId(UUID.randomUUID());

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setOrganization(organization);
        user.setRole(UserRole.DISPATCHER);
        principal = new AppUserDetails(user);

        waybillId = UUID.randomUUID();
        waybill = createWaybill(waybillId, "ПЛ-001", WaybillStatus.OPEN, 50.0);
    }

    private Waybill createWaybill(UUID id, String number, WaybillStatus status, Double fuelStart) {
        Waybill w = new Waybill();
        w.setId(id);
        w.setNumber(number);
        w.setStatus(status);
        w.setOrganization(organization);
        w.setVehicleType(VehicleType.LIGHT);
        w.setMessageType(MessageType.URBAN);
        w.setTransportationType(TransportationType.PASSENGER);
        w.setWorkStartDate(LocalDateTime.now());
        w.setFuelStart(fuelStart);
        w.setOdometerStart(10000.0);
        w.setMachineHoursStart(100.0);
        w.setEngineHoursStart(200.0);
        return w;
    }

    @Test
    void create_ShouldSaveWaybill() {
        WaybillRequest request = new WaybillRequest();
        request.setNumber("ПЛ-002");
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

        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(vehicleRepository.findByIdAndOrganization(any(), any())).thenReturn(Optional.of(new Vehicle()));
        when(driverRepository.findByIdAndOrganization(any(), any())).thenReturn(Optional.of(new Driver()));
        when(waybillRepository.save(any(Waybill.class))).thenAnswer(inv -> {
            Waybill w = inv.getArgument(0);
            w.setId(UUID.randomUUID());
            return w;
        });

        WaybillResponse result = waybillService.create(request, principal);

        assertNotNull(result);
        assertEquals("ПЛ-002", result.getNumber());
        verify(waybillRepository).save(any(Waybill.class));
    }

    @Test
    void findById_ShouldReturnWaybill_WhenExists() {
        when(waybillRepository.findByIdAndOrganization(waybillId, organization)).thenReturn(Optional.of(waybill));

        Optional<WaybillResponse> result = waybillService.findById(waybillId, principal);

        assertTrue(result.isPresent());
        assertEquals("ПЛ-001", result.get().getNumber());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        when(waybillRepository.findByIdAndOrganization(waybillId, organization)).thenReturn(Optional.empty());

        Optional<WaybillResponse> result = waybillService.findById(waybillId, principal);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_ShouldReturnPaginatedResults() {
        SearchRequest request = new SearchRequest();
        request.setPage(0);
        request.setSize(10);

        List<Waybill> waybills = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            waybills.add(createWaybill(UUID.randomUUID(), String.format("ПЛ-%03d", i + 1), WaybillStatus.CLOSED, 40.0 + i));
        }

        Page<Waybill> page = new PageImpl<>(waybills.subList(0, 10), PageRequest.of(0, 10), 25);

        when(genericSpecification.getSpecification(any())).thenReturn((root, query, cb) -> cb.conjunction());
        when(waybillRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Page<WaybillResponse> result = waybillService.search(request, principal);

        assertNotNull(result);
        assertEquals(10, result.getContent().size());
        assertEquals(25, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals("ПЛ-001", result.getContent().get(0).getNumber());
    }

    @Test
    void search_ShouldReturnSecondPage() {
        SearchRequest request = new SearchRequest();
        request.setPage(1);
        request.setSize(10);

        List<Waybill> waybills = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            waybills.add(createWaybill(UUID.randomUUID(), String.format("ПЛ-%03d", i + 1), WaybillStatus.CLOSED, 40.0 + i));
        }

        Page<Waybill> page = new PageImpl<>(waybills.subList(10, 20), PageRequest.of(1, 10), 25);

        when(genericSpecification.getSpecification(any())).thenReturn((root, query, cb) -> cb.conjunction());
        when(waybillRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Page<WaybillResponse> result = waybillService.search(request, principal);

        assertEquals(10, result.getContent().size());
        assertEquals(1, result.getNumber());
        assertEquals("ПЛ-011", result.getContent().get(0).getNumber());
    }

    @Test
    void closeWaybill_ShouldCalculateFuelAndSetStatus() {
        Waybill openWaybill = createWaybill(waybillId, "ПЛ-003", WaybillStatus.OPEN, 50.0);
        openWaybill.setOrganization(organization);
        openWaybill.setWorkEndDate(LocalDateTime.now().plusHours(8));
        openWaybill.setFuelEnd(20.0);
        openWaybill.setOdometerEnd(10200.0);
        openWaybill.setMachineHoursEnd(108.0);
        openWaybill.setEngineHoursEnd(208.0);

        VehicleFuelNorms norm = new VehicleFuelNorms();
        norm.setId(UUID.randomUUID());
        norm.setFuelNormPerKm(10.0);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());
        norm.setVehicle(vehicle);
        openWaybill.setAppliedFuelNorm(norm);

        when(waybillRepository.findByIdAndOrganization(waybillId, organization)).thenReturn(Optional.of(openWaybill));
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        doNothing().when(fuelCalculationService).calculateFuelConsumption(any(Waybill.class));
        when(waybillRepository.save(any(Waybill.class))).thenAnswer(inv -> inv.getArgument(0));

        WaybillResponse result = waybillService.closeWaybill(waybillId, principal);

        assertNotNull(result);
        assertEquals(WaybillStatus.CLOSED, result.getStatus());
        verify(fuelCalculationService).calculateFuelConsumption(any(Waybill.class));
        verify(waybillRepository).save(any(Waybill.class));
    }

    @Test
    void deleteById_ShouldDelete_WhenOpen() {
        Waybill openWaybill = createWaybill(waybillId, "ПЛ-004", WaybillStatus.OPEN, 50.0);

        when(waybillRepository.findByIdAndOrganization(waybillId, organization)).thenReturn(Optional.of(openWaybill));
        doNothing().when(waybillRepository).deleteByIdAndOrganization(waybillId, organization);

        waybillService.deleteById(waybillId, principal);

        verify(waybillRepository).deleteByIdAndOrganization(waybillId, organization);
    }

    @Test
    void deleteById_ShouldThrow_WhenClosed() {
        Waybill closedWaybill = createWaybill(waybillId, "ПЛ-005", WaybillStatus.CLOSED, 50.0);

        when(waybillRepository.findByIdAndOrganization(waybillId, organization)).thenReturn(Optional.of(closedWaybill));

        assertThrows(IllegalStateException.class, () -> waybillService.deleteById(waybillId, principal));
    }
}
