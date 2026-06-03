package com.fuelac.fuelac.service.export;

import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.*;
import com.fuelac.fuelac.repository.DriverRepository;
import com.fuelac.fuelac.repository.VehicleFuelNormsRepository;
import com.fuelac.fuelac.repository.VehicleRepository;
import com.fuelac.fuelac.repository.specification.GenericSpecification;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final VehicleFuelNormsRepository normsRepository;
    private final GenericSpecification genericSpecification;

    public ExcelExportService(VehicleRepository vehicleRepository, DriverRepository driverRepository, VehicleFuelNormsRepository normsRepository, GenericSpecification genericSpecification) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.normsRepository = normsRepository;
        this.genericSpecification = genericSpecification;
    }

    public byte[] exportVehicles(SearchRequest request, AppUserDetails principal) {
        Organization org = principal.getOrganization();
        if (org == null) {
            throw new IllegalStateException("Организация обязательна для экспорта");
        }
        Specification<Vehicle> orgSpec = (root, query, cb) -> cb.equal(root.get("organization"), org);
        Specification<Vehicle> filterSpec = genericSpecification.getSpecification(request.getFilters());
        List<Vehicle> vehicles = vehicleRepository.findAll(Specification.where(orgSpec).and(filterSpec));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Транспортные средства");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            String[] headers = {"Тип", "Марка", "Модель", "Год\nвыпуска", "Гос.\nномер", "Гаражный\nномер"};
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(30);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Vehicle v : vehicles) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getType() != null ? v.getType().getDisplayName() : "");
                row.createCell(1).setCellValue(v.getBrand());
                row.createCell(2).setCellValue(v.getModel());
                row.createCell(3).setCellValue(v.getYear() != null ? v.getYear() : 0);
                row.createCell(4).setCellValue(v.getRegistrationNumber());
                row.createCell(5).setCellValue(v.getGarageNumber() != null ? v.getGarageNumber() : "");
                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            // Минимальные ширины: Тип ~100px, Гос. номер ~90px
            if (sheet.getColumnWidth(0) < 3700) sheet.setColumnWidth(0, 3700);
            if (sheet.getColumnWidth(4) < 3300) sheet.setColumnWidth(4, 3300);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при генерации Excel", e);
        }
    }

    public byte[] exportDrivers(SearchRequest request, AppUserDetails principal) {
        Organization org = principal.getOrganization();
        if (org == null) {
            throw new IllegalStateException("Организация обязательна для экспорта");
        }
        Specification<Driver> orgSpec = (root, query, cb) -> cb.equal(root.get("organization"), org);
        Specification<Driver> filterSpec = genericSpecification.getSpecification(request.getFilters());
        List<Driver> drivers = driverRepository.findAll(Specification.where(orgSpec).and(filterSpec));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Водители");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            String[] headers = {"Табельный\nномер", "Фамилия", "Имя", "Отчество", "СНИЛС", "Водительское\nудостоверение", "Категория", "Дата\nвыдачи", "Дата\nокончания"};
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(45);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Driver d : drivers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(d.getPersonnelNumber());
                row.createCell(1).setCellValue(d.getLastName());
                row.createCell(2).setCellValue(d.getFirstName());
                row.createCell(3).setCellValue(d.getPatronymic() != null ? d.getPatronymic() : "");
                row.createCell(4).setCellValue(d.getSnils() != null ? d.getSnils() : "");
                DriverLicense license = d.getLicense();
                row.createCell(5).setCellValue(license != null ? license.getNumber() : "");
                row.createCell(6).setCellValue(license != null ? license.getCategory() : "");
                row.createCell(7).setCellValue(license != null && license.getIssueDate() != null ? license.getIssueDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "");
                row.createCell(8).setCellValue(license != null && license.getExpirationDate() != null ? license.getExpirationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "");
                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            // Минимальная ширина для Фамилия, Имя, Отчество ~110px
            for (int col = 1; col <= 3; col++) {
                if (sheet.getColumnWidth(col) < 4000) sheet.setColumnWidth(col, 4000);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при генерации Excel", e);
        }
    }

    public byte[] exportVehicleFuelNorms(SearchRequest request, AppUserDetails principal) {
        Organization org = principal.getOrganization();
        if (org == null) {
            throw new IllegalStateException("Организация обязательна для экспорта");
        }
        Specification<VehicleFuelNorms> orgSpec = (root, query, cb) -> cb.equal(root.get("organization"), org);
        Specification<VehicleFuelNorms> filterSpec = genericSpecification.getSpecification(request.getFilters());
        List<VehicleFuelNorms> norms = normsRepository.findAll(Specification.where(orgSpec).and(filterSpec));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Нормы расхода топлива");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            String[] headers = {"Гос. номер\nТС", "Описание", "Действует\nс", "Действует\nпо",
                    "Норма\nл/100км", "Норма\nмаш/ч", "Норма\nм/ч", "Норма\nх/х", "Норма\nпрогрев", "Норма\nкондиционер"};
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(45);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (VehicleFuelNorms n : norms) {
                Row row = sheet.createRow(rowNum++);
                Vehicle vehicle = n.getVehicle();
                row.createCell(0).setCellValue(vehicle != null ? vehicle.getRegistrationNumber() : "");
                row.createCell(1).setCellValue(n.getDescription() != null ? n.getDescription() : "");
                row.createCell(2).setCellValue(n.getValidFrom() != null ? n.getValidFrom().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "");
                row.createCell(3).setCellValue(n.getValidTo() != null ? n.getValidTo().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "");
                row.createCell(4).setCellValue(n.getFuelNormPerKm() != null ? n.getFuelNormPerKm() : 0);
                row.createCell(5).setCellValue(n.getFuelNormPerMachineHour() != null ? n.getFuelNormPerMachineHour() : 0);
                row.createCell(6).setCellValue(n.getFuelNormPerEngineHour() != null ? n.getFuelNormPerEngineHour() : 0);
                row.createCell(7).setCellValue(n.getFuelNormIdle() != null ? n.getFuelNormIdle() : 0);
                row.createCell(8).setCellValue(n.getFuelNormWarmUp() != null ? n.getFuelNormWarmUp() : 0);
                row.createCell(9).setCellValue(n.getFuelNormAirConditioner() != null ? n.getFuelNormAirConditioner() : 0);
                for (int i = 0; i < headers.length; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при генерации Excel", e);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
