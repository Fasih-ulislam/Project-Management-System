package com.softManager.project_management_system.controllers;

import com.softManager.project_management_system.dto.ExpenseDTO;
import com.softManager.project_management_system.model.Expense;
import com.softManager.project_management_system.model.UserPrincipal;
import com.softManager.project_management_system.services.ExpenseServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tasks/{taskId}/expenses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MEMBER')")
public class ExpenseController {


    private final ExpenseServices expenseService;

    @PostMapping
    public Expense createExpense(@PathVariable Long projectId,
                                 @PathVariable Long taskId,
                                 @Valid @RequestBody ExpenseDTO expenseDTO ,
                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return expenseService.createExpense(projectId, taskId, expenseDTO,userPrincipal);
    }

    @PutMapping("/{expenseId}")
    public Expense updateExpense(@PathVariable Long projectId,
                                 @PathVariable Long taskId,
                                 @PathVariable Long expenseId,
                                 @Valid @RequestBody ExpenseDTO expenseDTO,
                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return expenseService.updateExpense(projectId, taskId, expenseId, expenseDTO ,userPrincipal);
    }

    @DeleteMapping("/{expenseId}")
    public void deleteExpense(@PathVariable Long projectId,
                              @PathVariable Long taskId,
                              @PathVariable Long expenseId,
                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        expenseService.deleteExpense(projectId, taskId, expenseId , userPrincipal);
    }

    @GetMapping
    public List<Expense> getExpensesByTaskId(@PathVariable Long projectId,
                                             @PathVariable Long taskId,
                                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return expenseService.getExpensesByTaskId(projectId, taskId , userPrincipal);
    }
}
