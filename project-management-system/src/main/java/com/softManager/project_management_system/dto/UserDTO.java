package com.softManager.project_management_system.dto;

import com.softManager.project_management_system.constraints.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    @NotBlank(message = "username is required")
    @UniqueUsername(message = "username already exists")
    private String username;
    @Email(message = "email format required")
    private String email;
    @NotBlank(message = "password is required")
    private String password;
}

