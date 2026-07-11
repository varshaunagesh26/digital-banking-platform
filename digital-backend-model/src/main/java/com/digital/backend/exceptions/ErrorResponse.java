package com.digital.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class ErrorResponse {
    protected Instant timestamp;
    protected Integer status;
    protected String error;
    protected String message;
    protected String path;
    protected String stackTrace;
    protected String exception;

    public Long getTimeStampEpoch(){
        return timestamp.toEpochMilli();
    }
}
