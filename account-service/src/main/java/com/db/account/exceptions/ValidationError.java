package com.db.account.exceptions;

import lombok.*;

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
