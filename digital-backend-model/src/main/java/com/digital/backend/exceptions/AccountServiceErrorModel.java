package com.digital.backend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountServiceErrorModel {

    private String fieldName;
    private Object rejectedMessage;
    private String messageError;
}
