package com.softManager.project_management_system.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('USER')")
public class HealthCheck {
    @GetMapping("/health-check")
    public boolean healthCheck(){
        return true;
    }
}
