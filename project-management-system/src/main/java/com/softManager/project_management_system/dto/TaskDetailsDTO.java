package com.softManager.project_management_system.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TaskDetailsDTO {
    @NotBlank(message = "you have to provide the status")
    private String status;
    @Min(0) @Max(100)
    private int percentageCompleted;
    @NotNull(message = "You need to provide the hours worked")
    private double hoursWorked;
    private String comment;
}

