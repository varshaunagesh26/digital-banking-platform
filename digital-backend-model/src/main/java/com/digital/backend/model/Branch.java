package com.digital.backend.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Branch {

    @Schema(description = "Branch Code")
    private Long branchCode;

    @Schema(description = "Branch Name")
    private String branchName;

    @Schema(description = "Branch Address")
    private String branchAddress;

    @Schema(description = "Branch IFSC")
    private String branchIFSC;

    @Schema(description = "Account Number")
    private List<Long> accountNumbers;

}
