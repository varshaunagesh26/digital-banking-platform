package com.digital.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Customer {

    @Schema(name = "customer id")
    private Long customerId;

    @Schema(name = "First Name", example = "John")
    private String firstName;

    @Schema(name = "Last Name", example = "Doe")
    private String lastName;

    @Schema(name = "Date of Birth", example = "1997-01-01" )
    private Date customerDOB;

    @Schema(name = "Phone number")
    private Long phone;

    @Schema(name = "Email")
    private String customerEmail;

    @Schema(name = "Address")
    private String customerAddress;

    @Schema(name = "Account numbers")
    private List<Long> accountNumbers;

}
