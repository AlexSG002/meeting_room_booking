package com.alejandro.meetingbooking.dto.error;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ApiErrorResponse {
    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;
}
