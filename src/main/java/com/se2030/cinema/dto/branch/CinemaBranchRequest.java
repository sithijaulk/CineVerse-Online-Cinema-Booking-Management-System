package com.se2030.cinema.dto.branch;

import jakarta.validation.constraints.NotBlank;

public record CinemaBranchRequest(
        @NotBlank(message = "Branch name is required") String branchName,
        @NotBlank(message = "Location is required") String location,
        Boolean active) {
}