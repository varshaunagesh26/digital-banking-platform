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

    @Schema(name = "Branch Code")
    private Long branchCode;

    @Schema(name = "Branch Name")
    private String branchName;

    @Schema(name = "Branch Address")
    private String branchAddress;

    @Schema(name = "Branch IFSC")
    private String branchIFSC;

    @Schema(name = "Account Number")
    private List<Long> accountNumbers;

}
