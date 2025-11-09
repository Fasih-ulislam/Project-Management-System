package com.softManager.project_management_system.services;

import com.softManager.project_management_system.dto.ExpenseDTO;
import com.softManager.project_management_system.model.Expense;
import com.softManager.project_management_system.model.Project;
import com.softManager.project_management_system.model.Task;
import com.softManager.project_management_system.model.UserPrincipal;
import com.softManager.project_management_system.repository.ExpenseRepository;
import com.softManager.project_management_system.repository.ProjectRepository;
import com.softManager.project_management_system.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseServices {

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public boolean isManaged(Project project , UserPrincipal userPrincipal){
        return userPrincipal.getUsername().equals(project.getManager().getUser().getUsername());
    }

    private Task findTaskByProjectAndTaskId(Long projectId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getProject() == null || !task.getProject().getId().equals(projectId)) {
            throw new RuntimeException("Task does not belong to the specified project");
        }
        return task;
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Expense createExpense(Long projectId, Long taskId, ExpenseDTO expenseDTO, UserPrincipal userPrincipal) {
        Task task = findTaskByProjectAndTaskId(projectId, taskId);

        Project project = findProjectById(projectId);

        if (!(isManaged(project , userPrincipal))){
            throw new RuntimeException("Project access denied");
        }

        Expense expense = Expense.builder()
                .description(expenseDTO.getDescription())
                .amount(expenseDTO.getAmount())
                .task(task)
                .build();

        return expenseRepository.save(expense);
    }

    public Expense updateExpense(Long projectId, Long taskId, Long expenseId, ExpenseDTO expenseDTO, UserPrincipal userPrincipal) {
        Task task = findTaskByProjectAndTaskId(projectId, taskId);

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getTask().getId().equals(task.getId())) {
            throw new RuntimeException("Expense does not belong to the specified task");
        }

        Project project = findProjectById(projectId);

        if (!(isManaged(project , userPrincipal))){
            throw new RuntimeException("Project access denied");
        }

        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());

        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long projectId, Long taskId, Long expenseId, UserPrincipal userPrincipal) {
        Task task = findTaskByProjectAndTaskId(projectId, taskId);

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getTask().getId().equals(task.getId())) {
            throw new RuntimeException("Expense does not belong to the specified task");
        }

        Project project = findProjectById(projectId);

        if (!(isManaged(project , userPrincipal))){
            throw new RuntimeException("Project access denied");
        }

        expenseRepository.delete(expense);
    }

    public List<Expense> getExpensesByTaskId(Long projectId, Long taskId, UserPrincipal userPrincipal) {
        Task task = findTaskByProjectAndTaskId(projectId, taskId);

        Project project = findProjectById(projectId);

        if (!(isManaged(project , userPrincipal))){
            throw new RuntimeException("Project access denied");
        }

        return task.getExpenses();
    }
}
