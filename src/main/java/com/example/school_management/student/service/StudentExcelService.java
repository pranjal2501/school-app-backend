package com.example.school_management.student.service;

import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.AadharOwnerType;
import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.enums.Role;
import com.example.school_management.parent.dto.ParentCreateRequestDto;
import com.example.school_management.student.dto.BulkAdmissionResponseDto;
import com.example.school_management.student.dto.StudentFromAdminRequestDto;
import com.example.school_management.student.dto.StudentResponseDto;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class StudentExcelService {

    private final StudentServiceImpl studentService;
    private final RoleVerifier roleVerifier;

    public byte[] generateTemplate() throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Student Admission");

            // ===================== HEADER STYLE =====================
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // ===================== OPTIONAL STYLE (greyed out) =====================
            CellStyle optionalStyle = workbook.createCellStyle();
            optionalStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            optionalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // ===================== DATE STYLE =====================
            CellStyle dateStyle = workbook.createCellStyle();
            DataFormat dataFormat = workbook.createDataFormat();
            dateStyle.setDataFormat(dataFormat.getFormat("yyyy-mm-dd"));

            // ===================== TEXT STYLE FOR AADHAR =====================
            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setDataFormat(dataFormat.getFormat("@")); // "@" means text format

// apply text style to Aadhar columns for rows 2-100
            int[] aadharCols = {5, 18}; // Student Aadhar(5), Parent Aadhar(18)
            for (int i = 2; i <= 100; i++) {
                Row row = sheet.getRow(i);
                if (row == null) row = sheet.createRow(i);
                row.createCell(5).setCellStyle(textStyle);   // student aadhar
                row.createCell(18).setCellStyle(textStyle);  // parent aadhar
            }

            // ===================== COLUMNS =====================
            String[] columns = {
                    "First Name*", "Middle Name", "Last Name*",
                    "Gender*", "DOB* (YYYY-MM-DD)", "Aadhar No*", "Standard",
                    "Parent ID (if existing, leave blank for new)",
                    "Father First Name*", "Father Middle Name*", "Father Last Name*", "Father Occupation",
                    "Mother First Name*", "Mother Middle Name*", "Mother Last Name*", "Mother Occupation",
                    "Mobile Primary*", "Email*", "Parent Aadhar No*", "Aadhar Owner Type*"
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
                    "John", "Kumar", "Doe",
                    "MALE", "2010-05-15", "123456789012", "10",
                    "",
                    "Robert", "Kumar", "Doe", "Engineer",
                    "Mary", "Ann", "Doe", "Teacher",
                    "9876543210", "robert.doe@example.com", "987654321012", "FATHER"
            };
            for (int i = 0; i < example.length; i++) {
                Cell cell = exampleRow.createCell(i);
                cell.setCellValue(example[i]);
                if (i == 4) {
                    cell.setCellStyle(dateStyle); // apply date style to DOB example
                }
            }

            // ===================== DROPDOWNS =====================
            // Gender - col 3
            addDropdown(sheet, new String[]{"MALE", "FEMALE"}, 3, 2, 100);

            // Aadhar Owner Type - col 19
            addDropdown(sheet, new String[]{"FATHER", "MOTHER"}, 19, 2, 100);

            // ===================== DATE FORMAT ON DOB COLUMN =====================
            // apply date style to DOB column (col 4) for rows 2-100
            for (int i = 2; i <= 100; i++) {
                Row row = sheet.getRow(i);
                if (row == null) row = sheet.createRow(i);
                Cell dobCell = row.createCell(4);
                dobCell.setCellStyle(dateStyle);
            }

            // ===================== OPTIONAL COLUMNS GREYED =====================
            // grey out optional columns in header: Middle Name(1), Father Occupation(11), Mother Occupation(15)
            int[] optionalCols = {1, 7, 11, 15};
            for (int col : optionalCols) {
                Cell cell = headerRow.getCell(col);
                if (cell != null) {
                    CellStyle greyHeader = workbook.createCellStyle();
                    Font greyFont = workbook.createFont();
                    greyFont.setBold(true);
                    greyFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
                    greyHeader.setFont(greyFont);
                    greyHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    greyHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    greyHeader.setAlignment(HorizontalAlignment.CENTER);
                    cell.setCellStyle(greyHeader);
                }
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
        }
    }


    public BulkAdmissionResponseDto admitFromExcel(MultipartFile file) throws IOException{

        if (!roleVerifier.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can perform bulk admission");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            throw new IllegalArgumentException("Only .xlsx files are supported");
        }

        List<StudentFromAdminRequestDto> requests = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // skip empty rows
                String firstName = getCellValue(row, 0);
                if (firstName == null || firstName.isBlank()) continue;

                try {
                    StudentFromAdminRequestDto dto = mapRowToDto(row);
                    requests.add(dto);
                } catch (Exception e) {
                    errors.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }
        }

        if (requests.isEmpty() && !errors.isEmpty()) {
            throw new IllegalArgumentException("No valid rows found. Errors: " + errors);
        }

        List<StudentResponseDto> admitted = studentService.admitStudentsBulk(requests);

        return BulkAdmissionResponseDto.builder()
                .totalRows(requests.size() + errors.size())
                .successCount(admitted.size())
                .skippedCount(requests.size() - admitted.size())
                .errors(errors)
                .admittedStudents(admitted)
                .build();

    }


    // ================== Helper Methods ================================
    private StudentFromAdminRequestDto mapRowToDto(Row row) {

       // required fields with null checks
        String firstName = getCellValue(row, 0);
        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("First Name is required");

        String lastName = getCellValue(row, 2);
        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("Last Name is required");

        String genderStr = getCellValue(row, 3);
        if (genderStr == null || genderStr.isBlank())
            throw new IllegalArgumentException("Gender is required");
        Gender gender = Gender.valueOf(genderStr.toUpperCase());

        String dobStr = getCellValue(row, 4);
        if (dobStr == null || dobStr.isBlank())
            throw new IllegalArgumentException("DOB is required");
        LocalDate dob = LocalDate.parse(dobStr);

        String aadharNo = getCellValue(row, 5);
        if (aadharNo == null || aadharNo.isBlank())
            throw new IllegalArgumentException("Aadhar No is required");

        // optional student fields
        String middleName = getCellValue(row, 1);
        String standard = getCellValue(row, 6);

        // check if existing parent
        String parentIdStr = getCellValue(row, 7);
        if (parentIdStr != null && !parentIdStr.isBlank()) {
            return StudentFromAdminRequestDto.builder()
                    .firstName(firstName)
                    .middleName(middleName)
                    .lastName(lastName)
                    .gender(gender)
                    .dob(dob)
                    .aadharNo(aadharNo)
                    .standard(standard)
                    .parentId(Long.parseLong(parentIdStr))
                    .build();
        }

        // new parent fields
        String fatherFirstName = getCellValue(row, 8);
        if (fatherFirstName == null || fatherFirstName.isBlank())
            throw new IllegalArgumentException("Father First Name is required");

        String fatherMiddleName = getCellValue(row, 9);
        if (fatherMiddleName == null || fatherMiddleName.isBlank())
            throw new IllegalArgumentException("Father Middle Name is required");

        String fatherLastName = getCellValue(row, 10);
        if (fatherLastName == null || fatherLastName.isBlank())
            throw new IllegalArgumentException("Father Last Name is required");

        String motherFirstName = getCellValue(row, 12);
        if (motherFirstName == null || motherFirstName.isBlank())
            throw new IllegalArgumentException("Mother First Name is required");

        String motherMiddleName = getCellValue(row, 13);
        if (motherMiddleName == null || motherMiddleName.isBlank())
            throw new IllegalArgumentException("Mother Middle Name is required");

        String motherLastName = getCellValue(row, 14);
        if (motherLastName == null || motherLastName.isBlank())
            throw new IllegalArgumentException("Mother Last Name is required");

        String mobilePrimary = getCellValue(row, 16);
        if (mobilePrimary == null || mobilePrimary.isBlank())
            throw new IllegalArgumentException("Mobile Primary is required");

        String email = getCellValue(row, 17);
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("Email is required");

        String parentAadhar = getCellValue(row, 18);
        if (parentAadhar == null || parentAadhar.isBlank())
            throw new IllegalArgumentException("Parent Aadhar No is required");

        String aadharOwnerStr = getCellValue(row, 19);
        if (aadharOwnerStr == null || aadharOwnerStr.isBlank())
            throw new IllegalArgumentException("Aadhar Owner Type is required");
        AadharOwnerType aadharOwnerType = AadharOwnerType.valueOf(aadharOwnerStr.toUpperCase());

        ParentCreateRequestDto parent = new ParentCreateRequestDto(
                fatherFirstName,
                fatherMiddleName,
                fatherLastName,
                getCellValue(row, 11),  // fatherOccupation - optional
                motherFirstName,
                motherMiddleName,
                motherLastName,
                getCellValue(row, 15),  // motherOccupation - optional
                mobilePrimary,
                email,
                null,                   // password - auto generated
                parentAadhar,
                aadharOwnerType
        );

        return StudentFromAdminRequestDto.builder()
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .gender(gender)
                .dob(dob)
                .aadharNo(aadharNo)
                .standard(standard)
                .parent(parent)
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
}
