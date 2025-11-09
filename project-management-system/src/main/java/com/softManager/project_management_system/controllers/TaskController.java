package com.softManager.project_management_system.controllers;

import com.softManager.project_management_system.dto.TaskDTO;
import com.softManager.project_management_system.model.Task;
import com.softManager.project_management_system.model.UserPrincipal;
import com.softManager.project_management_system.services.TaskServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Transactional
@PreAuthorize("hasRole('MEMBER')")
public class TaskController {

    private final TaskServices taskService;
    private final ModelMapper modelMapper;

    // Create Task
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Task> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskDTO taskDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Task createdTask = taskService.createTask(projectId, taskDTO , userPrincipal);
        return ResponseEntity.ok(createdTask);
    }

    // Update Task
    @PutMapping("/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskDTO taskDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Task updatedTask = taskService.updateTask(projectId, taskId, taskDTO , userPrincipal);
        return ResponseEntity.ok(updatedTask);
    }

    // Delete Task
    @DeleteMapping("/projects/{projectId}/tasks/{taskId}")
    public ResponseEntity<String> deleteTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        taskService.deleteTask(projectId, taskId , userPrincipal);
        return ResponseEntity.ok("Task deleted successfully!");
    }

    // Get all Tasks for a Project
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable Long projectId ,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        List<Task> tasks = taskService.getTasksByProjectId(projectId , userPrincipal);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/assigned-tasks")
    public ResponseEntity<List<Task>> getAssignedTasks(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Task> tasks = taskService.getAssignedTasks(userPrincipal);
        return ResponseEntity.ok(tasks);
    }
}
