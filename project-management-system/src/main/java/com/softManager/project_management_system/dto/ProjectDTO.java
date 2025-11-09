package com.softManager.project_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProjectDTO {

    private Long id; // For updates, otherwise can be ignored in POST

    @NotNull(message = "Project manager is required")
    private Long managerId;

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    private String status;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Budget is required")
    private Double budget;
}



