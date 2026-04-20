package com.example.school_management.notify.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GeneralParentNotificationResponseDto.class, name = "GENERAL"),
        @JsonSubTypes.Type(value = ComplaintNotificationResponse.class, name = "COMPLAINT")
})
public interface ParentNotificationResponseDto {
}
