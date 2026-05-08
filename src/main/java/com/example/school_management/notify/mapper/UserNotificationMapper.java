package com.example.school_management.notify.mapper;

import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.StudentAdmissionStatus;
import com.example.school_management.common.enums.notify.NotificationType;
import com.example.school_management.notify.dto.*;
import com.example.school_management.notify.entity.Notification;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.specialEventFees.entity.SpecialEventFee;
import com.example.school_management.specialEventFees.entity.SpecialEventFeePayment;
import com.example.school_management.specialEventFees.repository.SpecialEventFeePaymentRepository;
import com.example.school_management.specialEventFees.repository.SpecialEventFeeRepository;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserNotificationMapper {

    private final StudentRepository studentRepository;
    private final SpecialEventFeeRepository specialEventFeeRepository;
    private final SpecialEventFeePaymentRepository specialEventFeePaymentRepository;

//    public GeneralParentNotificationResponseDto toParentResponseDto(UserNotification un) {
//        GeneralParentNotificationResponseDto dto = new GeneralParentNotificationResponseDto();
//        dto.setId(un.getId());
//        dto.setTitle(un.getNotification().getTitle());
//        dto.setShortInfo(un.getNotification().getShortInfo());
//        dto.setNotificationType(un.getNotification().getNotificationType());
//        dto.setEventDateTime(un.getNotification().getEventDateTime());
//        dto.setIsRead(un.getIsRead());
//        dto.setReadAt(un.getReadAt());
//        return dto;
//    }

    public ParentNotificationResponseDto toParentResponseDto(UserNotification un) {
        Notification n = un.getNotification();

        if (n.getNotificationType() == NotificationType.COMPLAINT) {
            return ComplaintNotificationResponse.builder()
                    .id(un.getId())
                    .title(n.getTitle())
                    .shortInfo(n.getShortInfo())
                    .description(n.getDescription())
                    .notificationType(n.getNotificationType())
                    .eventDateTime(n.getEventDateTime())
                    .isRead(un.getIsRead())
                    .readAt(un.getReadAt())
                    .build();
        }
        else if (n.getNotificationType().equals(NotificationType.STUDENT_APPROVAL)) {

            Parent parent = un.getParent();
            Student finStud = studentRepository.findFirstByParentAndAdmissionStatus(
                    parent, StudentAdmissionStatus.PENDING_PARENT_APPROVAL);

            ApproveStudentResponseDto responseDto = ApproveStudentResponseDto.builder()
                    .id(un.getId())
                    .title(n.getTitle())
                    .shortInfo(n.getShortInfo())
                    .description(n.getDescription())
                    .notificationType(n.getNotificationType())
                    .isRead(un.getIsRead())
                    .readAt(un.getReadAt())
                    .build();

            if (finStud != null) {
                responseDto.setApproveLink(responseDto.getApproveLink() + finStud.getRegId());
                responseDto.setRejectLink(responseDto.getRejectLink() + finStud.getRegId());
                responseDto.setExpiresAt(finStud.getApprovalExpiresAt());
            } else {
                responseDto.setApproveLink(null);
                responseDto.setRejectLink(null);
                responseDto.setExpiresAt(null);
            }

            return responseDto;
        }

        else if (n.getNotificationType() == NotificationType.SPECIAL_EVENT) {

            Parent parent = un.getParent();

            SpecialEventFee specialEvent = specialEventFeeRepository
                    .findByNotification(n)
                    .orElse(null);

            SpecialEventFeePayment payment = null;
            if (specialEvent != null && parent != null) {
                payment = specialEventFeePaymentRepository
                        .findBySpecialEventFeeAndParent(specialEvent, parent)
                        .orElse(null);
            }

            return SpecialEventNotificationResponseDto.builder()
                    .id(un.getId())
                    .title(n.getTitle())
                    .shortInfo(n.getShortInfo())
                    .description(n.getDescription())
                    .notificationType(n.getNotificationType())
                    .eventDateTime(n.getEventDateTime())
                    .feeAmount(specialEvent != null ? specialEvent.getFeeAmount() : null)
                    .feeType(specialEvent != null ? specialEvent.getFeeType() : null)
                    .feePaid(payment != null ? payment.getFeePaid() : false)
                    .feePaidAt(payment != null ? payment.getFeePaidAt() : null)
                    .paymentDeadline(specialEvent != null ? specialEvent.getPaymentDeadline() : null)
                    .isRead(un.getIsRead())
                    .readAt(un.getReadAt())
                    .build();
        }

        return GeneralParentNotificationResponseDto.builder()
                .id(un.getId())
                .title(n.getTitle())
                //.description(n.getDescription())
                .shortInfo(n.getShortInfo())
                .notificationType(n.getNotificationType())
                .eventDateTime(n.getEventDateTime())
                .urls(n.getUrls())
                .isRead(un.getIsRead())
                .readAt(un.getReadAt())
                .build();
    }

    public TeacherNotificationResponseDto toTeacherResponseDto(UserNotification un) {
        TeacherNotificationResponseDto dto = new TeacherNotificationResponseDto();
        dto.setId(un.getId());
        dto.setTitle(un.getNotification().getTitle());
        dto.setDescription(un.getNotification().getDescription());
        dto.setShortInfo(un.getNotification().getShortInfo());
        dto.setNotificationType(un.getNotification().getNotificationType());
        dto.setEventDateTime(un.getNotification().getEventDateTime());
        dto.setUrls(un.getNotification().getUrls());
        dto.setIsRead(un.getIsRead());
        dto.setReadAt(un.getReadAt());
        return dto;
    }
}
