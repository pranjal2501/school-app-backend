package com.example.school_management.teacher.service;

import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.enums.Role;
import com.example.school_management.teacher.dto.BulkTeacherAdmissionResponseDto;
import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import com.example.school_management.teacher.dto.response.TeacherResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherExcelService {

    private final RoleVerifier roleVerifier;
    private final TeacherService teacherService;

        public byte[] generateTeacherTemplate() throws IOException {

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Teacher Onboarding");

                // ===================== HEADER STYLE =====================
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);

                // ===================== DATE STYLE =====================
                CellStyle dateStyle = workbook.createCellStyle();
                DataFormat dataFormat = workbook.createDataFormat();
                dateStyle.setDataFormat(dataFormat.getFormat("yyyy-mm-dd"));

                // ===================== TEXT STYLE =====================
                CellStyle textStyle = workbook.createCellStyle();
                textStyle.setDataFormat(dataFormat.getFormat("@"));

                // ===================== COLUMNS =====================
                String[] columns = {
                        "First Name*", "Middle Name*", "Last Name*",
                        "Email*", "Mobile*", "Gender*",
                        "DOB* (YYYY-MM-DD)", "Joining Date* (YYYY-MM-DD)",
                        "Years of Experience*", "Aadhar No*", "PAN No*"
                };

                // ===================== HEADER ROW =====================
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerStyle);
                }

                // ===================== EXAMPLE ROW =====================
                Row exampleRow = sheet.createRow(1);
                String[] example = {
                        "Alice", "Mary", "Smith",
                        "alice.smith@school.com", "9876543210", "FEMALE",
                        "1990-05-15", "2020-06-01",
                        "5", "123456789012", "ABCDE1234F"
                };
                for (int i = 0; i < example.length; i++) {
                    Cell cell = exampleRow.createCell(i);
                    cell.setCellValue(example[i]);
                    if (i == 6 || i == 7) {
                        cell.setCellStyle(dateStyle); // DOB and Joining Date
                    }
                    if (i == 9) {
                        cell.setCellStyle(textStyle); // Aadhar
                    }
                }

                // ===================== DROPDOWNS =====================
                addDropdown(sheet, new String[]{"MALE", "FEMALE"}, 5, 2, 100);

                // ===================== DATE FORMAT ON DOB AND JOINING DATE COLUMNS =====================
                for (int i = 2; i <= 100; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) row = sheet.createRow(i);
                    row.createCell(6).setCellStyle(dateStyle);  // DOB
                    row.createCell(7).setCellStyle(dateStyle);  // Joining Date
                }

                // ===================== TEXT FORMAT ON AADHAR COLUMN =====================
                for (int i = 2; i <= 100; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) row = sheet.createRow(i);
                    row.createCell(9).setCellStyle(textStyle);  // Aadhar
                    row.createCell(10).setCellStyle(textStyle); // PAN
                }

                // ===================== AUTO SIZE COLUMNS =====================
                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                // ===================== FREEZE HEADER ROW =====================
                sheet.createFreezePane(0, 1);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                return outputStream.toByteArray();

            } catch (IOException e) {
                log.error("Failed to generate teacher template. Error: {}", e.getMessage());
                throw new IOException("Failed to generate teacher Excel template: " + e.getMessage());
            }
        }

    private void addDropdown(Sheet sheet, String[] options, int colIndex, int firstRow, int lastRow) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, colIndex, colIndex);
        DataValidationConstraint constraint = helper.createExplicitListConstraint(options);
        DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox("Invalid Value", "Please select a valid option from the dropdown.");
        sheet.addValidationData(validation);
    }

    public BulkTeacherAdmissionResponseDto admitTeachersFromExcel(MultipartFile file) throws IOException {

        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can perform bulk teacher admission");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            throw new IllegalArgumentException("Only .xlsx files are supported");
        }

        List<TeacherFromAdminRequestDto> requests = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String firstName = getCellValue(row, 0);
                if (firstName == null || firstName.isBlank()) continue;

                try {
                    TeacherFromAdminRequestDto dto = mapRowToTeacherDto(row);
                    requests.add(dto);
                } catch (Exception e) {
                    errors.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
        }

        if (requests.isEmpty() && !errors.isEmpty()) {
            throw new IllegalArgumentException("No valid rows found. Errors: " + errors);
        }

        // use bulk method with null username since admin is verified via roleVerifier
        List<TeacherResponseDto> admitted = teacherService.onboardTeachersBulk(requests, null);

        return BulkTeacherAdmissionResponseDto.builder()
                .totalRows(requests.size() + errors.size())
                .successCount(admitted.size())
                .skippedCount(requests.size() - admitted.size())
                .errors(errors)
                .admittedTeachers(admitted)
                .build();
    }

    private TeacherFromAdminRequestDto mapRowToTeacherDto(Row row) {

        String firstName = getCellValue(row, 0);
        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("First Name is required");

        String middleName = getCellValue(row, 1);
        if (middleName == null || middleName.isBlank())
            throw new IllegalArgumentException("Middle Name is required");

        String lastName = getCellValue(row, 2);
        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("Last Name is required");

        String email = getCellValue(row, 3);
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is required");

        String mobile = getCellValue(row, 4);
        if (mobile == null || mobile.isBlank())
            throw new IllegalArgumentException("Mobile is required");

        String genderStr = getCellValue(row, 5);
        if (genderStr == null || genderStr.isBlank())
            throw new IllegalArgumentException("Gender is required");
        Gender gender = Gender.valueOf(genderStr.toUpperCase());

        String dobStr = getCellValue(row, 6);
        if (dobStr == null || dobStr.isBlank())
            throw new IllegalArgumentException("DOB is required");
        LocalDate dob = LocalDate.parse(dobStr);

        String joiningDateStr = getCellValue(row, 7);
        if (joiningDateStr == null || joiningDateStr.isBlank())
            throw new IllegalArgumentException("Joining Date is required");
        LocalDate joiningDate = LocalDate.parse(joiningDateStr);

        String yearsOfExpStr = getCellValue(row, 8);
        if (yearsOfExpStr == null || yearsOfExpStr.isBlank())
            throw new IllegalArgumentException("Years of Experience is required");
        Integer yearsOfExperience = Integer.parseInt(yearsOfExpStr);

        String aadharNo = getCellValue(row, 9);
        if (aadharNo == null || aadharNo.isBlank())
            throw new IllegalArgumentException("Aadhar No is required");

        String panNo = getCellValue(row, 10);
        if (panNo == null || panNo.isBlank())
            throw new IllegalArgumentException("PAN No is required");

        return TeacherFromAdminRequestDto.builder()
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .email(email)
                .mobile(mobile)
                .gender(gender)
                .dob(dob)
                .joiningDate(joiningDate)
                .yearsOfExperienceInCurrentSchool(yearsOfExperience)
                .aadharNo(aadharNo)
                .panNo(panNo)
                .build();
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue()
                            .toLocalDate()
                            .toString(); // returns YYYY-MM-DD
                }
                yield String.valueOf((long) cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }
}
