package com.softManager.project_management_system.controllers;

import com.softManager.project_management_system.dto.TaskDTO;
import com.softManager.project_management_system.dto.TaskDetailsDTO;
import com.softManager.project_management_system.model.Task;
import com.softManager.project_management_system.model.TaskDetails;
import com.softManager.project_management_system.model.UserPrincipal;
import com.softManager.project_management_system.services.TaskDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RequestMapping()
@RequiredArgsConstructor
@Transactional
@PreAuthorize("hasRole('MEMBER')")
@RestController
public class TaskDetailsController {

    @Autowired
    private TaskDetailsService taskDetailsService;

    @PostMapping("tasks/{taskId}/taskDetails")
    public ResponseEntity<TaskDetails> createTaskDetails(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskDetailsDTO taskDetailsDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
       TaskDetails taskDetails =  taskDetailsService.createTaskDetails( taskId , taskDetailsDTO , userPrincipal);
            return ResponseEntity.ok(taskDetails);
    }

    @GetMapping("tasks/{taskId}/taskDetails/{taskDetailsId}")
    public ResponseEntity<TaskDetails> getTaskDetails(
            @PathVariable Long taskId,
            @PathVariable Long taskDetailsId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        TaskDetails taskDetails =  taskDetailsService.getTaskDetails( taskId , taskDetailsId , userPrincipal);
        return ResponseEntity.ok(taskDetails);
    }

    @DeleteMapping("tasks/{taskId}/taskDetails/{taskDetailsId}")
    public ResponseEntity<?> deleteTaskDetails(
            @PathVariable Long taskId,
            @PathVariable Long taskDetailsId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        taskDetailsService.deleteTaskDetails(taskId , taskDetailsId , userPrincipal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("tasks/{taskId}/taskDetails/{taskDetailsId}")
    public ResponseEntity<TaskDetails> updateTaskDetails(
            @PathVariable Long taskId,
            @PathVariable Long taskDetailsId,
            @Valid @RequestBody TaskDetailsDTO taskDetailsDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        TaskDetails taskDetails = taskDetailsService.updateTaskDetails( taskId , taskDetailsId ,taskDetailsDTO , userPrincipal);
        return new ResponseEntity<>(taskDetails, HttpStatus.OK);
    }

    @GetMapping("tasks/{taskId}/taskDetails")
    public ResponseEntity<List<TaskDetails>> getTaskDetailsOfProject(
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        List<TaskDetails> taskDetails =  taskDetailsService.getTaskAllDetails( taskId , userPrincipal);
        return ResponseEntity.ok(taskDetails);
    }




}
