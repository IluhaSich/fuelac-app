package com.fuelac.fuelac.service;

import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.*;
import com.fuelac.fuelac.model.enums.WaybillStatus;
import com.fuelac.fuelac.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExcelReportService {

    private final WaybillRepository waybillRepository;
    private final VehicleRepository vehicleRepository;
    private final OrganizationRepository organizationRepository;

    public ExcelReportService(WaybillRepository waybillRepository, VehicleRepository vehicleRepository, OrganizationRepository organizationRepository) {
        this.waybillRepository = waybillRepository;
        this.vehicleRepository = vehicleRepository;
        this.organizationRepository = organizationRepository;
    }

    public byte[] generateFuelReport(AppUserDetails principal, UUID vehicleId, LocalDate from, LocalDate to) {
        Organization org = principal.getOrganization();
        if (org == null) {
            throw new IllegalStateException("Организация обязательна для формирования отчета");
        }

        Vehicle vehicle = vehicleRepository.findByIdAndOrganization(vehicleId, org)
                .orElseThrow(() -> new RuntimeException("Транспортное средство не найдено с id: " + vehicleId));

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();

        List<Waybill> vehicleWaybills = waybillRepository.findByVehicleAndDateRange(org, vehicle, fromDateTime, toDateTime)
                .stream()
                .filter(w -> w.getStatus() == WaybillStatus.CLOSED)
                .sorted((w1, w2) -> w1.getWorkStartDate().compareTo(w2.getWorkStartDate()))
                .collect(Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Отчет по расходу топлива");

            // Стили
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle subHeaderStyle = createSubHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            int rowNum = 0;

            // Заголовок
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Отчет по учету работы транспортного средства");
            titleCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 28));

            // Период
            Row periodRow = sheet.createRow(rowNum++);
            Cell periodCell = periodRow.createCell(0);
            periodCell.setCellValue(String.format("за период с %s по %s",
                    from.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    to.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
            periodCell.setCellStyle(subHeaderStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 28));

            // Пустая строка
            rowNum++;

            // Информация о ТС
            Row vehicleRow1 = sheet.createRow(rowNum++);
            vehicleRow1.createCell(0).setCellValue("Гос. №");
            vehicleRow1.createCell(1).setCellValue(vehicle.getRegistrationNumber());

            Row vehicleRow2 = sheet.createRow(rowNum++);
            vehicleRow2.createCell(0).setCellValue("Гаражный №");
            vehicleRow2.createCell(1).setCellValue(vehicle.getGarageNumber() != null ? vehicle.getGarageNumber() : "-");

            Row vehicleRow3 = sheet.createRow(rowNum++);
            vehicleRow3.createCell(0).setCellValue("Марка");
            vehicleRow3.createCell(1).setCellValue(vehicle.getBrand() + " " + vehicle.getModel());

            rowNum++;

            // Заголовки таблицы
            String[] headers = {
                    "Номер\nпутевого листа", "ФИО\nводителя", "Начало выполнения\nработ, дата",
                    "Окончание выполнения\nработ, дата", "Остаток в баке\nна начало смены, л",
                    "Остаток в баке\nна конец смены, л", "Заправка\nпо п/л, л",
                    "Показания одометра\nна начало смены, км", "Показания одометра\nна конец смены, км",
                    "Показания счетчика\nмашиночасов на начало\nсмены, маш/ч",
                    "Показания счетчика\nмашиночасов на конец\nсмены, маш/ч",
                    "Показания счетчика\nмоточасов на начало\nсмены, м/ч",
                    "Показания счетчика\nмоточасов на конец\nсмены, м/ч",
                    "Пробег\nТС, км", "Машиночасы,\nмаш/ч", "Моточасы,\nм/ч", "Холостой\nход, ч",
                    "Прогрев\nдвигателя, ч", "Работа\nкондиционера, ч",
                    "Норма расхода топлива\nна пробег ТС (л/100 км)",
                    "Норма расхода топлива\nна 1 маш/ч (л/ч)",
                    "Норма расхода топлива\nна 1 м/ч (л/ч)",
                    "Норма расхода топлива\nна х/х (л/ч)",
                    "Норма расхода топлива\nна прогрев двигателя (л/ч)",
                    "Норма расхода топлива\nна работу кондиционера (л/ч)",
                    "Отработано\nчасов, ч", "Нормативный\nрасход топлива, л",
                    "Фактический\nрасход топлива, л", "Экономия (+) /\nПерерасход (-) за смену"
            };

            Row headerRow = sheet.createRow(rowNum++);
            headerRow.setHeightInPoints(60);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Данные
            for (Waybill waybill : vehicleWaybills) {
                Row dataRow = sheet.createRow(rowNum++);
                int col = 0;

                dataRow.createCell(col++).setCellValue(waybill.getNumber());
                dataRow.createCell(col++).setCellValue(waybill.getDriver() != null ? waybill.getDriver().getFullName() : "");
                dataRow.createCell(col++).setCellValue(formatDateTime(waybill.getWorkStartDate()));
                dataRow.createCell(col++).setCellValue(formatDateTime(waybill.getWorkEndDate()));
                dataRow.createCell(col++).setCellValue(waybill.getFuelStart() != null ? waybill.getFuelStart() : 0);
                dataRow.createCell(col++).setCellValue(waybill.getFuelEnd() != null ? waybill.getFuelEnd() : 0);
                dataRow.createCell(col++).setCellValue(waybill.getFuelRefilled() != null ? waybill.getFuelRefilled() : 0);
                dataRow.createCell(col++).setCellValue(waybill.getOdometerStart() != null ? waybill.getOdometerStart() : 0);
                dataRow.createCell(col++).setCellValue(waybill.getOdometerEnd() != null ? waybill.getOdometerEnd() : 0);
                dataRow.createCell(col++).setCellValue(waybill.getMachineHoursStart() != null ? waybill.getMachineHoursStart() : 0);
                dataRow.createCell(col++).setCellValue(waybill.getMachineHoursEnd() != null ? waybill.getMachineHoursEnd() : 0);
                dataRow.createCell(col++).setCellValue(waybill.getEngineHoursStart() != null ? waybill.getEngineHoursStart() : 0);
                dataRow.createCell(col++).setCellValue(waybill.getEngineHoursEnd() != null ? waybill.getEngineHoursEnd() : 0);

                double mileage = waybill.getOdometerEnd() != null && waybill.getOdometerStart() != null
                        ? waybill.getOdometerEnd() - waybill.getOdometerStart() : 0;
                double machineHours = waybill.getMachineHoursEnd() != null && waybill.getMachineHoursStart() != null
                        ? waybill.getMachineHoursEnd() - waybill.getMachineHoursStart() : 0;
                double engineHours = waybill.getEngineHoursEnd() != null && waybill.getEngineHoursStart() != null
                        ? waybill.getEngineHoursEnd() - waybill.getEngineHoursStart() : 0;

                dataRow.createCell(col++).setCellValue(mileage);
                dataRow.createCell(col++).setCellValue(machineHours);
                dataRow.createCell(col++).setCellValue(engineHours);
                dataRow.createCell(col++).setCellValue(waybill.getIdleTime() != null ? waybill.getIdleTime() : 0);
                dataRow.createCell(col++).setCellValue(waybill.getEngineWarmUpTime() != null ? waybill.getEngineWarmUpTime() : 0);
                dataRow.createCell(col++).setCellValue(waybill.getAirConditionerTime() != null ? waybill.getAirConditionerTime() : 0);

                VehicleFuelNorms norm = waybill.getAppliedFuelNorm();
                if (norm != null) {
                    dataRow.createCell(col++).setCellValue(norm.getFuelNormPerKm() != null ? norm.getFuelNormPerKm() : 0);
                    dataRow.createCell(col++).setCellValue(norm.getFuelNormPerMachineHour() != null ? norm.getFuelNormPerMachineHour() : 0);
                    dataRow.createCell(col++).setCellValue(norm.getFuelNormPerEngineHour() != null ? norm.getFuelNormPerEngineHour() : 0);
                    dataRow.createCell(col++).setCellValue(norm.getFuelNormIdle() != null ? norm.getFuelNormIdle() : 0);
                    dataRow.createCell(col++).setCellValue(norm.getFuelNormWarmUp() != null ? norm.getFuelNormWarmUp() : 0);
                    dataRow.createCell(col++).setCellValue(norm.getFuelNormAirConditioner() != null ? norm.getFuelNormAirConditioner() : 0);
                } else {
                    for (int i = 0; i < 6; i++) {
                        dataRow.createCell(col++).setCellValue(0);
                    }
                }

                double hoursWorked = waybill.getWorkEndDate() != null && waybill.getWorkStartDate() != null
                        ? java.time.Duration.between(waybill.getWorkStartDate(), waybill.getWorkEndDate()).toMinutes() / 60.0
                        : 0;

                dataRow.createCell(col++).setCellValue(Math.round(hoursWorked * 10) / 10.0);
                dataRow.createCell(col++).setCellValue(waybill.getCalculatedNormativeFuel() != null ? waybill.getCalculatedNormativeFuel() : 0);
                dataRow.createCell(col++).setCellValue(waybill.getCalculatedActualFuel() != null ? waybill.getCalculatedActualFuel() : 0);
                dataRow.createCell(col++).setCellValue(waybill.getCalculatedDeviation() != null ? waybill.getCalculatedDeviation() : 0);
                for (int i = 0; i < headers.length; i++) {
                    dataRow.getCell(i).setCellStyle(dataStyle);
                }
            }

            // Автоширина колонок
            for (int i = 0; i < 29; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при генерации Excel-отчета", e);
        }
    }

    public byte[] exportWaybills(AppUserDetails principal, LocalDate from, LocalDate to) {
        Organization org = principal.getOrganization();
        if (org == null) {
            throw new IllegalStateException("Организация обязательна для экспорта");
        }

        LocalDateTime fromDt = from != null ? from.atStartOfDay() : LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime toDt = to != null ? to.plusDays(1).atStartOfDay() : LocalDateTime.now().plusDays(1);

        List<Waybill> waybills = waybillRepository.findByDateRange(org, fromDt, toDt)
                .stream()
                .sorted((a, b) -> a.getWorkStartDate().compareTo(b.getWorkStartDate()))
                .collect(Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Путевые листы");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            String[] headers = {
                    "Номер ПЛ", "Статус", "ТС (гос. №)", "Водитель",
                    "Дата начала", "Дата окончания",
                    "Одометр начало", "Одометр конец", "Пробег, км",
                    "Топливо начало, л", "Топливо конец, л", "Заправка, л",
                    "Норм. расход, л", "Факт. расход, л", "Отклонение, л"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(30);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Waybill w : waybills) {
                Row row = sheet.createRow(rowNum++);
                int col = 0;
                row.createCell(col++).setCellValue(w.getNumber() != null ? w.getNumber() : "");
                row.createCell(col++).setCellValue(w.getStatus() != null ? w.getStatus().getDisplayName() : "");
                row.createCell(col++).setCellValue(w.getVehicle() != null ? w.getVehicle().getRegistrationNumber() : "");
                row.createCell(col++).setCellValue(w.getDriver() != null ? w.getDriver().getFullName() : "");
                row.createCell(col++).setCellValue(formatDateTime(w.getWorkStartDate()));
                row.createCell(col++).setCellValue(formatDateTime(w.getWorkEndDate()));
                row.createCell(col++).setCellValue(w.getOdometerStart() != null ? w.getOdometerStart() : 0);
                row.createCell(col++).setCellValue(w.getOdometerEnd() != null ? w.getOdometerEnd() : 0);
                double mileage = w.getOdometerEnd() != null && w.getOdometerStart() != null
                        ? w.getOdometerEnd() - w.getOdometerStart() : 0;
                row.createCell(col++).setCellValue(mileage);
                row.createCell(col++).setCellValue(w.getFuelStart() != null ? w.getFuelStart() : 0);
                row.createCell(col++).setCellValue(w.getFuelEnd() != null ? w.getFuelEnd() : 0);
                row.createCell(col++).setCellValue(w.getFuelRefilled() != null ? w.getFuelRefilled() : 0);
                row.createCell(col++).setCellValue(w.getCalculatedNormativeFuel() != null ? w.getCalculatedNormativeFuel() : 0);
                row.createCell(col++).setCellValue(w.getCalculatedActualFuel() != null ? w.getCalculatedActualFuel() : 0);
                row.createCell(col++).setCellValue(w.getCalculatedDeviation() != null ? w.getCalculatedDeviation() : 0);
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
            throw new RuntimeException("Ошибка при генерации Excel-экспорта путевых листов", e);
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
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

    private CellStyle createSubHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
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

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
}
