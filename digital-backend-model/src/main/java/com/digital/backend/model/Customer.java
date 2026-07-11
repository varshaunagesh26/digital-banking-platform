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

    @Schema(description = "customer id")
    private Long customerId;

    @Schema(description = "First Name", example = "John")
    private String firstName;

    @Schema(description = "Last Name", example = "Doe")
    private String lastName;

    @Schema(description = "Date of Birth", example = "1997-01-01" )
    private Date customerDOB;

    @Schema(description = "Phone number")
    private Long phone;

    @Schema(description = "Email")
    private String customerEmail;

    @Schema(description = "Address")
    private String customerAddress;

    @Schema(description = "Account numbers")
    private List<Long> accountNumbers;

}
