package com.example.school_management.timeTable.service;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import com.example.school_management.timeTable.dto.ClassTimetableResponseDto;
import com.example.school_management.timeTable.dto.TimetableConfigRequestDto;
import com.example.school_management.timeTable.dto.TimetableGenerationResponseDto;
import com.example.school_management.timeTable.dto.TimetableSlotResponseDto;
import com.example.school_management.timeTable.entity.TimeTableConfig;
import com.example.school_management.timeTable.entity.TimetableSlot;
import com.example.school_management.timeTable.repository.TimetableConfigRepository;
import com.example.school_management.timeTable.repository.TimetableSlotRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimetableService {

    private final TimetableConfigRepository timetableConfigRepository;
    private final TimetableSlotRepository timetableSlotRepository;
    private final ClassSubjectTeacherRepository classSubjectTeacherRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final TeacherRepository teacherRepository;


    @Transactional
    public void saveConfig(TimetableConfigRequestDto request) {
        TimeTableConfig config = TimeTableConfig.builder()
                .workingDays(request.getWorkingDays())
                .schoolStartTime(request.getSchoolStartTime())
                .schoolEndTime(request.getSchoolEndTime())
                .periodDurationMinutes(request.getPeriodDurationMinutes())
                .breakAfterPeriod(request.getBreakAfterPeriod())
                .breakDurationMinutes(request.getBreakDurationMinutes())
                .academicYear(request.getAcademicYear())
                .build();

        timetableConfigRepository.save(config);
    }

    @Transactional
    public TimetableGenerationResponseDto generateTimetable(String academicYear) {

        // 1. fetch config
        TimeTableConfig config = timetableConfigRepository.findByAcademicYear(academicYear)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Timetable config not found for year: " + academicYear));

        // 2. calculate period start times
        List<LocalTime> periodStartTimes = calculatePeriodStartTimes(config);
        int totalPeriodsPerDay = periodStartTimes.size();
        List<DayOfWeek> workingDays = config.getWorkingDays();

        log.info("Total periods per day: {}, Working days: {}", totalPeriodsPerDay, workingDays.size());

        // 3. fetch all classes
        List<SchoolClass> allClasses = schoolClassRepository.findAll();

        // 4. shared teacher occupied map across ALL classes
        // teacherOccupied[teacherId][day][period]
        Map<Long, Map<DayOfWeek, Set<Integer>>> teacherOccupied = new HashMap<>();

        List<TimetableSlot> slotsToSave = new ArrayList<>();
        List<String> unscheduled = new ArrayList<>();

        // 5. process each class
        for (SchoolClass schoolClass : allClasses) {

            List<ClassSubjectTeacher> allocations = classSubjectTeacherRepository
                    .findBySchoolClassIdWithDetails(schoolClass.getId());

            if (allocations.isEmpty()) continue;

            int totalSlots = workingDays.size() * totalPeriodsPerDay;
            int totalSubjectHours = allocations.stream()
                    .mapToInt(a -> a.getSubject().getWeeklyHours())
                    .sum();

            if (totalSubjectHours != totalSlots) {
                throw new IllegalArgumentException(
                        "Total subject hours (" + totalSubjectHours + ") must equal " +
                                "total available slots (" + totalSlots + "). " +
                                "Please adjust subject weekly hours before generating timetable."
                );
            }

            // class occupied map
            Map<DayOfWeek, Set<Integer>> classOccupied = new HashMap<>();
            for (DayOfWeek day : workingDays) {
                classOccupied.put(day, new HashSet<>());
            }

            // init teacher maps
            for (ClassSubjectTeacher allocation : allocations) {
                teacherOccupied.putIfAbsent(allocation.getTeacher().getId(),
                        initDayMap(workingDays));
            }

            // remaining hours per allocation
            Map<Long, Integer> remainingHours = new HashMap<>();
            for (ClassSubjectTeacher allocation : allocations) {
                remainingHours.put(allocation.getId(),
                        allocation.getSubject().getWeeklyHours());
            }

            // 6. multi-pass scheduling
            boolean progressMade = true;
            while (hasRemainingHours(remainingHours) && progressMade) {
                progressMade = false;

                // track per day across entire pass — NOT inside day loop
                Map<DayOfWeek, Set<Long>> scheduledPerDayThisPass = new HashMap<>();
                for (DayOfWeek day : workingDays) {
                    scheduledPerDayThisPass.put(day, new HashSet<>());
                }

                List<DayOfWeek> shuffledDays = new ArrayList<>(workingDays);
                Collections.shuffle(shuffledDays);

                for (DayOfWeek day : shuffledDays) {

                    List<ClassSubjectTeacher> shuffledAllocations = new ArrayList<>(allocations);
                    Collections.shuffle(shuffledAllocations);

                    for (ClassSubjectTeacher allocation : shuffledAllocations) {

                        if (remainingHours.get(allocation.getId()) <= 0) continue;

                        // check if this subject already scheduled today in this pass
                        if (scheduledPerDayThisPass.get(day).contains(allocation.getId())) continue;

                        Teacher teacher = allocation.getTeacher();

                        Integer freePeriod = findFreePeriod(
                                day,
                                totalPeriodsPerDay,
                                classOccupied,
                                teacherOccupied.get(teacher.getId())
                        );

                        if (freePeriod == null) {
                            log.warn("No free period on {} for class {} subject {}",
                                    day,
                                    schoolClass.getClassName(),
                                    allocation.getSubject().getSubjectName());
                            continue;
                        }

                        classOccupied.get(day).add(freePeriod);
                        teacherOccupied.get(teacher.getId()).get(day).add(freePeriod);

                        // mark this subject as scheduled today in this pass
                        scheduledPerDayThisPass.get(day).add(allocation.getId());

                        remainingHours.put(allocation.getId(),
                                remainingHours.get(allocation.getId()) - 1);

                        LocalTime startTime = periodStartTimes.get(freePeriod - 1);
                        LocalTime endTime = startTime.plusMinutes(config.getPeriodDurationMinutes());

                        TimetableSlot slot = TimetableSlot.builder()
                                .schoolClass(schoolClass)
                                .teacher(teacher)
                                .subject(allocation.getSubject())
                                .dayOfWeek(day)
                                .periodNumber(freePeriod)
                                .startTime(startTime)
                                .endTime(endTime)
                                .academicYear(academicYear)
                                .build();

                        slotsToSave.add(slot);
                        progressMade = true;
                    }
                }
            }
            // 7. report unscheduled
            for (ClassSubjectTeacher allocation : allocations) {
                int remaining = remainingHours.get(allocation.getId());
                if (remaining > 0) {
                    unscheduled.add(String.format(
                            "Class: %s | Subject: %s | Unscheduled: %d periods",
                            schoolClass.getClassName(),
                            allocation.getSubject().getSubjectName(),
                            remaining
                    ));
                }
            }
        }

        // 8. save all slots
        timetableSlotRepository.saveAll(slotsToSave);

        log.info("Timetable generated: {} slots saved, {} unscheduled",
                slotsToSave.size(), unscheduled.size());

        return TimetableGenerationResponseDto.builder()
                .academicYear(academicYear)
                .totalSlotsGenerated(slotsToSave.size())
                .unscheduledSubjects(unscheduled)
                .build();
    }

    // fetch timetable for a specific class
    public ClassTimetableResponseDto getClassTimetable(Long classId, String academicYear) {

        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", classId));

        List<TimetableSlot> slots = timetableSlotRepository
                .findBySchoolClassIdAndAcademicYearOrderByDayOfWeekAscPeriodNumberAsc(
                        classId, academicYear);

        Map<DayOfWeek, List<TimetableSlotResponseDto>> schedule = new LinkedHashMap<>();

        for (TimetableSlot slot : slots) {
            schedule.computeIfAbsent(slot.getDayOfWeek(), k -> new ArrayList<>())
                    .add(TimetableSlotResponseDto.builder()
                            .className(schoolClass.getClassName())
                            .subjectName(slot.getSubject().getSubjectName())
                            .teacherName(slot.getTeacher().getFirstName()
                                    + " " + slot.getTeacher().getLastName())
                            .dayOfWeek(slot.getDayOfWeek())
                            .periodNumber(slot.getPeriodNumber())
                            .startTime(slot.getStartTime())
                            .endTime(slot.getEndTime())
                            .build());
        }

        return ClassTimetableResponseDto.builder()
                .className(schoolClass.getClassName())
                .academicYear(academicYear)
                .schedule(schedule)
                .build();
    }


    public ClassTimetableResponseDto getTeacherTimetable(Long teacherId, String academicYear) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", teacherId));

        List<TimetableSlot> slots = timetableSlotRepository
                .findByTeacherIdAndAcademicYearOrderByDayOfWeekAscPeriodNumberAsc(
                        teacherId, academicYear);

        Map<DayOfWeek, List<TimetableSlotResponseDto>> schedule = new LinkedHashMap<>();

        for (TimetableSlot slot : slots) {
            schedule.computeIfAbsent(slot.getDayOfWeek(), k -> new ArrayList<>())
                    .add(TimetableSlotResponseDto.builder()
                            .className(slot.getSchoolClass().getClassName())
                            .subjectName(slot.getSubject().getSubjectName())
                            .teacherName(teacher.getFirstName() + " " + teacher.getLastName())
                            .dayOfWeek(slot.getDayOfWeek())
                            .periodNumber(slot.getPeriodNumber())
                            .startTime(slot.getStartTime())
                            .endTime(slot.getEndTime())
                            .build());
        }

        return ClassTimetableResponseDto.builder()
                .className(teacher.getFirstName() + " " + teacher.getLastName())
                .academicYear(academicYear)
                .schedule(schedule)
                .build();
    }

    public byte[] generateTimetableExcel(ClassTimetableResponseDto timetable) throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Timetable");

            // ===================== STYLES =====================
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dayStyle = workbook.createCellStyle();
            Font dayFont = workbook.createFont();
            dayFont.setBold(true);
            dayStyle.setFont(dayFont);
            dayStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            dayStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            dayStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            centerStyle.setBorderBottom(BorderStyle.THIN);
            centerStyle.setBorderTop(BorderStyle.THIN);
            centerStyle.setBorderLeft(BorderStyle.THIN);
            centerStyle.setBorderRight(BorderStyle.THIN);

            // ===================== TITLE ROW =====================
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Timetable - " + timetable.getClassName()
                    + " | Academic Year: " + timetable.getAcademicYear());
            titleCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            // ===================== COLUMN HEADERS =====================
            Row headerRow = sheet.createRow(1);
            String[] headers = {"Day", "Period", "Start Time", "End Time", "Subject", "Teacher"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ===================== DATA ROWS =====================
            int rowNum = 2;
            for (Map.Entry<DayOfWeek, List<TimetableSlotResponseDto>> entry
                    : timetable.getSchedule().entrySet()) {

                DayOfWeek day = entry.getKey();
                List<TimetableSlotResponseDto> slots = entry.getValue();

                for (int i = 0; i < slots.size(); i++) {
                    TimetableSlotResponseDto slot = slots.get(i);
                    Row row = sheet.createRow(rowNum++);

                    // day column - only show on first period of each day
                    Cell dayCell = row.createCell(0);
                    if (i == 0) {
                        dayCell.setCellValue(day.name());
                        dayCell.setCellStyle(dayStyle);
                    } else {
                        dayCell.setCellStyle(centerStyle);
                    }

                    Cell periodCell = row.createCell(1);
                    periodCell.setCellValue("Period " + slot.getPeriodNumber());
                    periodCell.setCellStyle(centerStyle);

                    Cell startCell = row.createCell(2);
                    startCell.setCellValue(slot.getStartTime().toString());
                    startCell.setCellStyle(centerStyle);

                    Cell endCell = row.createCell(3);
                    endCell.setCellValue(slot.getEndTime().toString());
                    endCell.setCellStyle(centerStyle);

                    Cell subjectCell = row.createCell(4);
                    subjectCell.setCellValue(slot.getSubjectName());
                    subjectCell.setCellStyle(centerStyle);

                    Cell teacherCell = row.createCell(5);
                    teacherCell.setCellValue(slot.getTeacherName());
                    teacherCell.setCellStyle(centerStyle);
                }

                // empty row between days
                sheet.createRow(rowNum++);
            }

            // ===================== AUTO SIZE =====================
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
        catch (IOException e) {
            log.error("Failed to generate timetable Excel for class: {}. Error: {}",
                    timetable.getClassName(), e.getMessage());
            throw new IOException("Failed to generate timetable Excel: " + e.getMessage());
        }
    }


    //======================== HELPER METHODS ========================
    // check if any subject still has remaining hours
    private boolean hasRemainingHours(Map<Long, Integer> remainingHours) {
        return remainingHours.values().stream().anyMatch(h -> h > 0);
    }

    // find first free period for both class and teacher on a day
    private Integer findFreePeriod(
            DayOfWeek day,
            int totalPeriods,
            Map<DayOfWeek, Set<Integer>> classOccupied,
            Map<DayOfWeek, Set<Integer>> teacherOccupied) {

        for (int period = 1; period <= totalPeriods; period++) {
            boolean classIsFree = !classOccupied.get(day).contains(period);
            boolean teacherIsFree = !teacherOccupied.get(day).contains(period);
            if (classIsFree && teacherIsFree) {
                return period;
            }
        }
        return null;
    }

    // calculate period start times accounting for break
    private List<LocalTime> calculatePeriodStartTimes(TimeTableConfig config) {
        List<LocalTime> times = new ArrayList<>();
        LocalTime current = config.getSchoolStartTime();
        int periodCount = 0;

        while (current.plusMinutes(config.getPeriodDurationMinutes())
                .isBefore(config.getSchoolEndTime())
                || current.plusMinutes(config.getPeriodDurationMinutes())
                .equals(config.getSchoolEndTime())) {

            periodCount++;
            times.add(current);
            current = current.plusMinutes(config.getPeriodDurationMinutes());

            if (periodCount == config.getBreakAfterPeriod()) {
                current = current.plusMinutes(config.getBreakDurationMinutes());
            }
        }
        return times;
    }

    // initialize day map
    private Map<DayOfWeek, Set<Integer>> initDayMap(List<DayOfWeek> workingDays) {
        Map<DayOfWeek, Set<Integer>> map = new HashMap<>();
        for (DayOfWeek day : workingDays) {
            map.put(day, new HashSet<>());
        }
        return map;
    }
}