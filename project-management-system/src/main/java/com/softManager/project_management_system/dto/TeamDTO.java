package com.softManager.project_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDTO {
    @NotBlank(message = "Give a name to the team")
    private String name;

}

