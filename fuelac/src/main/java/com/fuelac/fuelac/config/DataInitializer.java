package com.fuelac.fuelac.config;

import com.fuelac.fuelac.dto.request.*;
import com.fuelac.fuelac.dto.response.*;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.*;
import com.fuelac.fuelac.model.enums.*;
import com.fuelac.fuelac.repository.*;
import com.fuelac.fuelac.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final VehicleFuelNormsService normsService;
    private final WaybillService waybillService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(OrganizationRepository organizationRepository,
                           UserRepository userRepository,
                           VehicleService vehicleService,
                           DriverService driverService,
                           VehicleFuelNormsService normsService,
                           WaybillService waybillService,
                           PasswordEncoder passwordEncoder) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.vehicleService = vehicleService;
        this.driverService = driverService;
        this.normsService = normsService;
        this.waybillService = waybillService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (organizationRepository.count() > 0) {
            return;
        }

        createAdmins();

        List<Organization> organizations = createOrganizations();

        System.out.println("=".repeat(60));
        System.out.println("FUELAC - ТЕСТОВЫЕ УЧЕТНЫЕ ЗАПИСИ");
        System.out.println("=".repeat(60));
        System.out.println("АДМИНИСТРАТОРЫ:");
        System.out.println("  admin1@fuelac.ru / admin123");
        System.out.println("  admin2@fuelac.ru / admin123");
        System.out.println();

        for (Organization org : organizations) {
            User manager = createManager(org);
            User dispatcher = createDispatcher(org);
            AppUserDetails managerPrincipal = new AppUserDetails(manager);
            AppUserDetails dispatcherPrincipal = new AppUserDetails(dispatcher);

            System.out.println("ОРГАНИЗАЦИЯ: " + org.getName());
            System.out.println("  Менеджер:     " + manager.getEmail() + " / manager123");
            System.out.println("  Диспетчер:    " + dispatcher.getEmail() + " / dispatcher123");
            System.out.println();

            List<VehicleResponse> vehicles = createVehicles(org, managerPrincipal);
            List<DriverResponse> drivers = createDrivers(org, managerPrincipal);
            List<VehicleFuelNormsResponse> norms = createFuelNorms(org, vehicles, managerPrincipal);
            createWaybills(org, vehicles, drivers, norms, dispatcherPrincipal);
        }
        System.out.println("=".repeat(60));
        System.out.println("Инициализация завершена. Всего организаций: " + organizations.size());
        System.out.println("=".repeat(60));
    }

    private void createAdmins() {
        for (int i = 1; i <= 2; i++) {
            User admin = new User();
            admin.setLastName("Администратор");
            admin.setFirstName("" + i);
            admin.setEmail("admin" + i + "@fuelac.ru");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);
        }
    }

    private List<Organization> createOrganizations() {
        String[][] orgData = {
            {"ООО \"Транспорт-1\"", "7720123456", "772001001", "1234567890123", "г. Москва, ул. Ленина, д. 1"},
            {"ООО \"Логистика-Юг\"", "7720123457", "772001002", "1234567890124", "г. Ростов-на-Дону, ул. Советская, д. 10"}
        };

        List<Organization> result = new ArrayList<>();
        for (String[] data : orgData) {
            OrganizationRequest req = new OrganizationRequest();
            req.setName(data[0]);
            req.setOgrn(data[1]);
            req.setCodeOKPO(data[2]);
            req.setCodeOKUD(data[3]);
            req.setAddress(data[4]);
            Organization org = organizationRepository.save(req.toEntity());
            result.add(org);
        }
        return result;
    }

    private User createManager(Organization org) {
        User manager = new User();
        manager.setLastName("Иванов");
        manager.setFirstName("Иван");
        manager.setPatronymic("Иванович");
        manager.setEmail("manager@" + org.getId().toString().substring(0, 8) + ".ru");
        manager.setPassword(passwordEncoder.encode("manager123"));
        manager.setRole(UserRole.ORGANIZATION_MANAGER);
        manager.setOrganization(org);
        return userRepository.save(manager);
    }

    private User createDispatcher(Organization org) {
        User dispatcher = new User();
        dispatcher.setLastName("Смирнова");
        dispatcher.setFirstName("Анна");
        dispatcher.setPatronymic("Александровна");
        dispatcher.setEmail("dispatcher@" + org.getId().toString().substring(0, 8) + ".ru");
        dispatcher.setPassword(passwordEncoder.encode("dispatcher123"));
        dispatcher.setRole(UserRole.DISPATCHER);
        dispatcher.setOrganization(org);
        return userRepository.save(dispatcher);
    }

    private List<VehicleResponse> createVehicles(Organization org, AppUserDetails principal) {
        String[][] vehicleData = {
            {"Lada", "Vesta", "LIGHT", "А123БВ" + org.getId().toString().substring(0, 4), "Г01"},
            {"KAMAZ", "65115", "TRUCK", "В456КМ" + org.getId().toString().substring(0, 4), "Г02"},
            {"GAZ", "3302", "TRUCK", "Е789НО" + org.getId().toString().substring(0, 4), "Г03"}
        };

        List<VehicleResponse> result = new ArrayList<>();
        for (String[] data : vehicleData) {
            VehicleRequest req = new VehicleRequest();
            req.setType(VehicleType.valueOf(data[2]));
            req.setBrand(data[0]);
            req.setModel(data[1]);
            req.setYear(2020 + new Random().nextInt(5));
            req.setRegistrationNumber(data[3]);
            req.setGarageNumber(data[4]);
            result.add(vehicleService.create(req, principal));
        }
        return result;
    }

    private List<DriverResponse> createDrivers(Organization org, AppUserDetails principal) {
        String[][] driverData = {
            {"001", "Алексеев",  "Алексей",  "Алексеевич",  "123-456-789 00", "77 01 123456", "B",   "2020-01-15", "2030-01-15"},
            {"002", "Борисов",   "Борис",    "Борисович",   "123-456-789 01", "77 01 234567", "C",   "2019-03-20", "2029-03-20"},
            {"003", "Викторов",  "Виктор",   "Викторович",  "123-456-789 02", "77 01 345678", "B,C", "2021-05-10", "2031-05-10"},
            {"004", "Григорьев", "Григорий", "Григорьевич", "123-456-789 03", "77 01 456789", "C",   "2022-07-15", "2032-07-15"},
            {"005", "Дмитриев",  "Дмитрий",  "Дмитриевич",  "123-456-789 04", "77 01 567890", "B,C,E","2023-02-20","2033-02-20"}
        };

        List<DriverResponse> result = new ArrayList<>();
        for (String[] data : driverData) {
            DriverRequest req = new DriverRequest();
            req.setPersonnelNumber(data[0] + "-" + org.getId().toString().substring(0, 4));
            req.setLastName(data[1]);
            req.setFirstName(data[2]);
            req.setPatronymic(data[3]);
            req.setSnils(data[4]);
            req.setLicenseNumber(data[5]);
            req.setLicenseCategory(data[6]);
            req.setLicenseIssueDate(LocalDate.parse(data[7]));
            req.setLicenseExpirationDate(LocalDate.parse(data[8]));
            result.add(driverService.create(req, principal));
        }
        return result;
    }

    private List<VehicleFuelNormsResponse> createFuelNorms(Organization org, List<VehicleResponse> vehicles, AppUserDetails principal) {
        Random random = new Random();
        List<VehicleFuelNormsResponse> result = new ArrayList<>();
        for (VehicleResponse vehicle : vehicles) {
            // Историческая норма 2024–2025
            VehicleFuelNormsRequest hist = buildNormRequest(vehicle, random,
                    LocalDate.of(2024, 1, 1), LocalDate.of(2025, 12, 31), "Историческая норма");
            normsService.create(hist, principal);

            // Актуальная норма 2026 (используется для закрытия ПЛ)
            VehicleFuelNormsResponse current = normsService.create(
                    buildNormRequest(vehicle, random,
                            LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), "Текущая норма"),
                    principal);
            result.add(current);
        }
        return result;
    }

    private VehicleFuelNormsRequest buildNormRequest(VehicleResponse vehicle, Random random,
                                                      LocalDate from, LocalDate to, String desc) {
        VehicleFuelNormsRequest req = new VehicleFuelNormsRequest();
        req.setVehicleId(vehicle.getId());
        req.setValidFrom(from);
        req.setValidTo(to);
        req.setDescription(desc + " для " + vehicle.getBrand() + " " + vehicle.getModel());
        if (vehicle.getType() == VehicleType.LIGHT) {
            req.setFuelNormPerKm(round1(8.5 + random.nextDouble() * 2));
            req.setFuelNormIdle(round1(0.8 + random.nextDouble() * 0.3));
        } else if (vehicle.getType() == VehicleType.TRUCK) {
            req.setFuelNormPerKm(round1(25.0 + random.nextDouble() * 5));
            req.setFuelNormPerMachineHour(round1(3.5 + random.nextDouble()));
            req.setFuelNormIdle(round1(1.5 + random.nextDouble() * 0.5));
        } else {
            req.setFuelNormPerMachineHour(round1(5.0 + random.nextDouble() * 2));
            req.setFuelNormIdle(round1(2.0 + random.nextDouble() * 0.5));
        }
        req.setFuelNormWarmUp(round1(0.5 + random.nextDouble() * 0.3));
        req.setFuelNormAirConditioner(round1(0.3 + random.nextDouble() * 0.2));
        return req;
    }

    private void createWaybills(Organization org, List<VehicleResponse> vehicles, List<DriverResponse> drivers,
                                List<VehicleFuelNormsResponse> norms, AppUserDetails principal) {
        Map<UUID, Double> lastOdometer = new HashMap<>();
        Map<UUID, Double> lastMachineHours = new HashMap<>();
        Map<UUID, Double> lastEngineHours = new HashMap<>();
        Random random = new Random();

        for (int vIdx = 0; vIdx < vehicles.size(); vIdx++) {
            VehicleResponse vehicle = vehicles.get(vIdx);
            VehicleFuelNormsResponse norm = norms.get(vIdx);
            // Два разных водителя для каждого ТС (чередуются по путевым листам)
            DriverResponse driverA = drivers.get(vIdx % drivers.size());
            DriverResponse driverB = drivers.get((vIdx + 2) % drivers.size());

            double baseOdometer = 10000.0 + vIdx * 5000;
            double baseMachineHours = 500.0 + vIdx * 50;
            double baseEngineHours = 800.0 + vIdx * 40;

            lastOdometer.put(vehicle.getId(), baseOdometer);
            lastMachineHours.put(vehicle.getId(), baseMachineHours);
            lastEngineHours.put(vehicle.getId(), baseEngineHours);

            for (int i = 0; i < 9; i++) {
                double startOdometer = lastOdometer.get(vehicle.getId());
                double tripDistance = 80 + random.nextInt(170);
                double endOdometer = startOdometer + tripDistance;

                double startMachineHours = lastMachineHours.get(vehicle.getId());
                double machineHoursDelta = 6 + random.nextDouble() * 4;
                double endMachineHours = startMachineHours + machineHoursDelta;

                double startEngineHours = lastEngineHours.get(vehicle.getId());
                double engineHoursDelta = 5 + random.nextDouble() * 5;
                double endEngineHours = startEngineHours + engineHoursDelta;

                boolean isOpen = (i == 8);

                // Путевые листы за 2026 год (попадают в актуальные нормы)
                int month = 1 + (i % 5);
                int day = 1 + (i * 3) % 25;

                double fuelStart = 40.0 + random.nextInt(21);
                double fuelConsumed = tripDistance / 10.0 + random.nextDouble() * 5;
                double fuelEnd = Math.max(5.0, fuelStart - fuelConsumed);
                double fuelRefilled = random.nextInt(31);

                WaybillRequest req = new WaybillRequest();
                // Чередуем двух водителей на одном ТС
                DriverResponse driver = (i % 2 == 0) ? driverA : driverB;
                req.setNumber(String.format("ПЛ-%02d-%s-%s", i + 1, org.getId().toString().substring(0, 4), vehicle.getRegistrationNumber().substring(0, 4)));
                req.setStatus(WaybillStatus.OPEN);
                req.setVehicleType(vehicle.getType());
                req.setMessageType(MessageType.values()[random.nextInt(MessageType.values().length)]);
                req.setTransportationType(TransportationType.values()[random.nextInt(TransportationType.values().length)]);
                req.setDriverId(driver.getId());
                req.setVehicleId(vehicle.getId());
                req.setWorkStartDate(LocalDateTime.of(2026, month, day, 8, 0));
                req.setFuelStart(round1(fuelStart));
                req.setFuelRefilled(round1(fuelRefilled));
                req.setOdometerStart(round1(startOdometer));
                req.setMachineHoursStart(round1(startMachineHours));
                req.setEngineHoursStart(round1(startEngineHours));

                if (!isOpen) {
                    req.setWorkEndDate(req.getWorkStartDate().plusHours(8).plusMinutes(random.nextInt(120)));
                    req.setFuelEnd(round1(fuelEnd));
                    req.setOdometerEnd(round1(endOdometer));
                    req.setMachineHoursEnd(round1(endMachineHours));
                    req.setEngineHoursEnd(round1(endEngineHours));
                    req.setIdleTime(round1(random.nextDouble() * 1.5));
                    req.setEngineWarmUpTime(round1(random.nextDouble() * 0.5));
                    req.setAirConditionerTime(round1(random.nextDouble() * 0.5));
                    req.setAppliedFuelNormId(norm.getId());
                }

                WaybillResponse created = waybillService.create(req, principal);

                if (!isOpen) {
                    waybillService.closeWaybill(created.getId(), principal);

                    lastOdometer.put(vehicle.getId(), endOdometer);
                    lastMachineHours.put(vehicle.getId(), endMachineHours);
                    lastEngineHours.put(vehicle.getId(), endEngineHours);
                } else {
                    lastOdometer.put(vehicle.getId(), startOdometer);
                    lastMachineHours.put(vehicle.getId(), startMachineHours);
                    lastEngineHours.put(vehicle.getId(), startEngineHours);
                }
            }
        }
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}

