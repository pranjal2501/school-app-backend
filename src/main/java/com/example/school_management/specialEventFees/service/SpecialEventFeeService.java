package com.example.school_management.specialEventFees.service;

import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.FeeType;
import com.example.school_management.common.enums.notify.AudienceType;
import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.notify.repository.NotificationRepository;
import com.example.school_management.notify.repository.UserNotificationRepository;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.schoolClass.repo.SchoolClassRepository;
import com.example.school_management.specialEventFees.dto.*;
import com.example.school_management.specialEventFees.entity.SpecialEventFee;
import com.example.school_management.specialEventFees.entity.SpecialEventFeePayment;
import com.example.school_management.specialEventFees.mapper.SpecialEventFeeMapper;
import com.example.school_management.specialEventFees.repository.SpecialEventFeePaymentRepository;
import com.example.school_management.specialEventFees.repository.SpecialEventFeeRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpecialEventFeeService {

    private final SpecialEventFeeRepository feeRepository;
    private final SpecialEventFeePaymentRepository paymentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final RoleVerifier roleVerifier;
    private final SpecialEventFeeMapper mapper;

    @Transactional
    public SpecialEventFeeResponseDto createFee(CreateSpecialEventFeeRequestDto request) {

        String currentUser = roleVerifier.getCurrentUsername();
        Teacher teacher = teacherRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("Teacher session invalid"));

        SchoolClass schoolClass = schoolClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", request.getClassId()));

        SpecialEventFee fee = SpecialEventFee.builder()
                .eventName(request.getEventName())
                .description(request.getDescription())
                .feeAmount(request.getFeeAmount())
                .feeType(FeeType.ACTIVITY_FEES)
                .paymentDeadline(request.getPaymentDeadline())
                .eventDateTime(request.getEventDateTime())
                .schoolClass(schoolClass)
                .createdByTeacher(teacher)
                .build();

        feeRepository.save(fee);


        List<Student> students = studentRepository.findByAssignedClassId(schoolClass.getId());
        List<SpecialEventFeePayment> payments = new ArrayList<>();
        List<Parent> parents= new ArrayList<>();

        for (Student student : students) {
            if (student.getParent() == null) continue;

            SpecialEventFeePayment payment = SpecialEventFeePayment.builder()
                    .specialEventFee(fee)
                    .student(student)
                    .parent(student.getParent())
                    .feeAmount(request.getFeeAmount())
                    .feePaid(false)
                    .build();

            payments.add(payment);

            if(parents.contains(student.getParent()))
            {
                continue;
            }
            parents.add(student.getParent());
        }

        paymentRepository.saveAll(payments);

        int totalPaid = 0;
        int totalUnpaid = payments.size();

        Notification notification = Notification.builder()
                .title("Special Event at School")
                .description(request.getDescription())
                .shortInfo(request.getEventName())
                .audienceType(AudienceType.PARENT)
                .notificationType(NotificationType.SPECIAL_EVENT)
                .eventDateTime(LocalDateTime.now()) // immediate event
                .scheduledDispatchTime(LocalDateTime.now())
                .dispatched(true)
                .build();

        notificationRepository.save(notification);

        fee.setNotification(notification);
        feeRepository.save(fee);

        for(Parent parent:parents) {
            UserNotification userNotification = UserNotification.builder()
                    .notification(notification)
                    .parent(parent)
                    .deliveredAt(LocalDateTime.now())
                    .build();

            userNotificationRepository.save(userNotification);
        }


        return mapper.toResponseDto(fee, totalPaid, totalUnpaid);
    }

    public List<SpecialEventFeeResponseDto> getMyFees() {

        String currentUser = roleVerifier.getCurrentUsername();
        Teacher teacher = teacherRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("Teacher session invalid"));

        return feeRepository.findByCreatedByTeacher(teacher)
                .stream()
                .map(fee -> {
                    int totalPaid = paymentRepository
                            .countBySpecialEventFeeAndFeePaid(fee, true);
                    int totalUnpaid = paymentRepository
                            .countBySpecialEventFeeAndFeePaid(fee, false);
                    return mapper.toResponseDto(fee, totalPaid, totalUnpaid);
                })
                .collect(Collectors.toList());
    }

    public List<SpecialEventFeePaymentResponseDto> getFeeSummary(Long feeId) {

        String currentUser = roleVerifier.getCurrentUsername();
        Teacher teacher = teacherRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("Teacher session invalid"));

        SpecialEventFee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new ResourceNotFoundException("Fee", "id", feeId));

        if (!fee.getCreatedByTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedAccessException("You can only view summary of your own fees");
        }

        return paymentRepository.findBySpecialEventFee(fee)
                .stream()
                .map(mapper::toPaymentResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public String markFeePaid(Long paymentId) {

        String currentUser = roleVerifier.getCurrentUsername();
        Teacher teacher = teacherRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("Teacher session invalid"));

        SpecialEventFeePayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        if (!payment.getSpecialEventFee().getCreatedByTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedAccessException("You can only mark fees for your own events");
        }

        if (LocalDateTime.now().isAfter(payment.getSpecialEventFee().getPaymentDeadline())) {
            throw new IllegalStateException("Payment deadline has passed");
        }

        if (Boolean.TRUE.equals(payment.getFeePaid())) {
            throw new IllegalStateException("Fee already marked as paid");
        }

        payment.setFeePaid(true);
        payment.setFeePaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return "Fee marked as paid for: " + payment.getStudent().getFirstName()
                + " " + payment.getStudent().getLastName();
    }

    public List<SpecialEventParentResponseDto> getAllEventsForChild(String RegId) {

        Student stud= studentRepository.findByRegId(RegId).get();

        List<SpecialEventFeePayment> eventList= paymentRepository.findByStudent(stud);

        List<SpecialEventParentResponseDto> responseDtos= new ArrayList<>();
        for(SpecialEventFeePayment fee : eventList)
        {
            responseDtos.add(mapper.toParentResponseDto(fee));
        }
        return responseDtos;
    }

    public TeacherTotalSummaryResponse getDetailedInfoGraphics() {

        String currentUser = roleVerifier.getCurrentUsername();
        Teacher teacher = teacherRepository.findByEmail(currentUser)
                .orElseThrow(() -> new UnauthorizedAccessException("Teacher session invalid"));

        List<SpecialEventFee> fees = feeRepository.findByCreatedByTeacher(teacher);

        double grandTotalExpected = 0;
        double grandTotalCollected = 0;

        List<DetailedFeeResponse> feeRequests = new ArrayList<>();

        for (SpecialEventFee fee : fees) {

            List<SpecialEventFeePayment> payments = paymentRepository.findBySpecialEventFee(fee);

            int totalStudents = payments.size();
            int totalPaid = (int) payments.stream()
                    .filter(p -> Boolean.TRUE.equals(p.getFeePaid())).count();
            int totalUnpaid = totalStudents - totalPaid;

            double totalExpected = fee.getFeeAmount() * totalStudents;
            double totalCollected = fee.getFeeAmount() * totalPaid;
            double totalPending = fee.getFeeAmount() * totalUnpaid;
            float collectionPercent = totalStudents > 0
                    ? (totalPaid * 100.0f) / totalStudents : 0f;

            grandTotalExpected += totalExpected;
            grandTotalCollected += totalCollected;

            List<SpecialEventFeePaymentResponseDto> summary = payments.stream()
                    .map(mapper::toPaymentResponseDto)
                    .collect(Collectors.toList());

            feeRequests.add(DetailedFeeResponse.builder()
                    .id(fee.getId())
                    .eventName(fee.getEventName())
                    .description(fee.getDescription())
                    .paymentDeadline(fee.getPaymentDeadline())
                    .eventDateTime(fee.getEventDateTime())
                    .className(fee.getSchoolClass().getClassName())
                    .feeAmountPerStudent(fee.getFeeAmount())
                    .totalExpected(totalExpected)
                    .totalCollected(totalCollected)
                    .totalPending(totalPending)
                    .collectionPercent(collectionPercent)
                    .totalStudents(totalStudents)
                    .totalPaid(totalPaid)
                    .totalUnpaid(totalUnpaid)
                    .summary(summary)
                    .build());
        }

        float overallCollectionRate = grandTotalExpected > 0
                ? (float) ((grandTotalCollected / grandTotalExpected) * 100) : 0f;

        return TeacherTotalSummaryResponse.builder()
                .totalExpected(grandTotalExpected)
                .totalCollected(grandTotalCollected)
                .collectionRate(overallCollectionRate)
                .feeRequests(feeRequests)
                .build();
    }
}