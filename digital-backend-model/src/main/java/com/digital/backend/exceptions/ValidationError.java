package com.digital.backend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ValidationError implements Serializable {

    private String field;
    private Serializable invalidValue;
    private String message;
}
