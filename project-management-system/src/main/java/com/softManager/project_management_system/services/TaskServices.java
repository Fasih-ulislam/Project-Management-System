package com.softManager.project_management_system.services;

import com.softManager.project_management_system.dto.TaskDTO;
import com.softManager.project_management_system.model.*;
import com.softManager.project_management_system.repository.ProjectRepository;
import com.softManager.project_management_system.repository.TaskRepository;
import com.softManager.project_management_system.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServices {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    @Autowired
    private TeamRepository teamRepository;

    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    private Task findTaskByProjectAndTaskId(Long projectId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getProject() == null || !task.getProject().getId().equals(projectId)) {
            throw new RuntimeException("Task does not belong to the specified project");
        }
        return task;
    }

    public boolean isManaged(Project project , UserPrincipal userPrincipal){
        return userPrincipal.getUsername().equals(project.getManager().getUser().getUsername());
    }

    public Task createTask(Long projectId, TaskDTO taskDTO, UserPrincipal userPrincipal) {
        Project project = findProjectById(projectId);

        if (!(isManaged(project , userPrincipal))){
            throw new RuntimeException("Project access denied");
        }
        Task task = Task.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .startDate(taskDTO.getStartDate())
                .endDate(taskDTO.getEndDate())
                .completed(false)
                .project(project)
                .build();

        return taskRepository.save(task);
    }

    public Task updateTask(Long projectId, Long taskId, TaskDTO taskDTO, UserPrincipal userPrincipal) {
        Task task = findTaskByProjectAndTaskId(projectId, taskId);

        Project project = findProjectById(projectId);

        if (!(isManaged(project , userPrincipal))){
            throw new RuntimeException("Project access denied");
        }

        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStartDate(taskDTO.getStartDate());
        task.setEndDate(taskDTO.getEndDate());

        return taskRepository.save(task);
    }

    public void deleteTask(Long projectId, Long taskId, UserPrincipal userPrincipal) {
        Task task = findTaskByProjectAndTaskId(projectId, taskId);
        Project project = findProjectById(projectId);

        if (!(isManaged(project , userPrincipal))){
            throw new RuntimeException("Project access denied");
        }

        taskRepository.delete(task);
    }

    public List<Task> getTasksByProjectId(Long projectId, UserPrincipal userPrincipal) throws AccessDeniedException {
        Project project = findProjectById(projectId);

        if (!isManaged(project, userPrincipal)) {
            throw new AccessDeniedException("Project access denied");
        }

        List<Task> tasks = project.getTasks();

        for (Task task : tasks) {
            boolean isCompleted = task.getTaskDetails() != null &&
                    task.getTaskDetails().stream()
                            .anyMatch(detail -> detail.getPercentageCompleted() != null && detail.getPercentageCompleted() == 100);
            task.setCompleted(isCompleted);
        }

        taskRepository.saveAll(tasks);

        return tasks;
    }


    public void assignTaskToTeam(Long taskId, Long teamId, UserPrincipal userPrincipal) throws AccessDeniedException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        Project project = task.getProject();
        if (!(isManaged(project , userPrincipal))){
            throw new AccessDeniedException("Project access denied");
        }
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        if(team.getCreatedBy().getUser().getUsername().equals(userPrincipal.getUsername())) {
            task.setTeam(team);
            taskRepository.save(task);
        }
        else {
            throw new AccessDeniedException("This team is not created by you");
        }

    }

    public List<Task> getAssignedTasks(UserPrincipal userPrincipal) {
        List<Task> assigned = taskRepository.findAll().stream()
                .filter(task -> task.getTeam() != null && task.getTeam().getMembers() != null)
                .filter(task -> task.getTeam().getMembers().stream()
                        .anyMatch(member -> member.getUser().getUsername().equals(userPrincipal.getUsername())))
                .toList();

        if (!assigned.isEmpty()) {
            for (Task task : assigned) {
                boolean isCompleted = task.getTaskDetails() != null &&
                        task.getTaskDetails().stream()
                                .anyMatch(detail -> detail.getPercentageCompleted() != null && detail.getPercentageCompleted() == 100);
                task.setCompleted(isCompleted);
            }
            taskRepository.saveAll(assigned);
            return assigned;
        }
        throw new RuntimeException("You are not assigned to any task");

    }

    public void removeTaskFromTeam(Long taskId, Long teamId, UserPrincipal userPrincipal) throws AccessDeniedException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        Project project = task.getProject();
        if (!(isManaged(project , userPrincipal))){
            throw new AccessDeniedException("Project access denied");
        }
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        if(team.getCreatedBy().getUser().getUsername().equals(userPrincipal.getUsername())) {
            task.setTeam(null);
            taskRepository.save(task);
        }
        else {
            throw new AccessDeniedException("This team is not created by you");
        }
    }
}
