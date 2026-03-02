package com.db.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    @NotNull
    @Schema(name = "customer id")
    private Long customerId;

    @NotNull
    @Schema(name = "First Name", example = "John")
    private String firstName;

    @NotNull
    @Schema(name = "Last Name", example = "Doe")
    private String lastName;

    @NotNull
    @Schema(name = "Date of Birth", example = "1997-01-01" )
    private Date customerDOB;

    @NotNull
    @Schema(name = "Phone number")
    private Long phone;

    @NotNull
    @Schema(name = "Email")
    private String customerEmail;

    @NotNull
    @Schema(name = "Address")
    private String customerAddress;

    @NotNull
    @Schema(name = "Account numbers")
    private List<Long> accountNumbers;

}