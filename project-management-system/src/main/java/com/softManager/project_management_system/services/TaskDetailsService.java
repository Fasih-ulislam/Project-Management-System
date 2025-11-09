package com.softManager.project_management_system.services;

import com.softManager.project_management_system.dto.TaskDetailsDTO;
import com.softManager.project_management_system.model.*;
import com.softManager.project_management_system.repository.ProjectRepository;
import com.softManager.project_management_system.repository.TaskDetailsRepository;
import com.softManager.project_management_system.repository.TaskRepository;
import com.softManager.project_management_system.repository.UserRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

@Transactional
@Service
public class TaskDetailsService {

    @Autowired
    private TaskDetailsRepository taskDetailsRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    private Member getMemberByUsernameSpecial(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Member member = user.getMember();
        if (member == null) {
            throw new RuntimeException("This user is not a member");
        }

        return member;
    }

    private Task findTaskByProjectAndTaskId(Long projectId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (task.getProject() == null || !task.getProject().getId().equals(projectId)) {
            throw new RuntimeException("Task does not belong to the specified project");
        }
        return task;
    }

    private boolean partOfTeam(Task task , UserPrincipal userPrincipal){
        if(task.getTeam() == null){
            return false;
        }
        List<String> usernames =  task.getTeam().getMembers().stream().map(member -> member.getUser().getUsername()).toList();
        return usernames.contains(userPrincipal.getUsername());
    }

    private boolean createdTeam(Task task , UserPrincipal userPrincipal){
        if(task.getTeam() == null){
            return false;
        }
        return task.getTeam().getCreatedBy().getUser().getUsername().equals(userPrincipal.getUsername());
    }

    public TaskDetails createTaskDetails(Long taskId,
                                  @Valid TaskDetailsDTO taskDetailsDTO,
                                  UserPrincipal userPrincipal) throws AccessDeniedException {
        Task task = findTaskById(taskId);
       if (partOfTeam(task, userPrincipal) || createdTeam(task , userPrincipal)){
           TaskDetails taskDetails = new TaskDetails();
           taskDetails.setPercentageCompleted(taskDetailsDTO.getPercentageCompleted());
           taskDetails.setHoursWorked(taskDetailsDTO.getHoursWorked());
           taskDetails.setStatus(taskDetailsDTO.getStatus());
           taskDetails.setComment(taskDetailsDTO.getComment());

           taskDetails.setMadeBy(getMemberByUsernameSpecial(userPrincipal.getUsername()));
           taskDetails.setTask(task);
           taskDetails.setTimestamp();
           taskDetailsRepository.save(taskDetails);
           return taskDetails;
       }
        throw new AccessDeniedException("You are not part of the assigned team");
    }

    public TaskDetails getTaskDetails(Long taskId,
                                      Long taskDetailsId,
                                      UserPrincipal userPrincipal) throws AccessDeniedException {
        Task task = findTaskById(taskId);
        if (partOfTeam(task, userPrincipal) || task.getTeam().getCreatedBy().getUser().getUsername().equals(userPrincipal.getUsername())){
            return taskDetailsRepository.findById(taskDetailsId)
                    .orElseThrow(() -> new NoSuchElementException("task details with the id not found"));
        }
        throw new AccessDeniedException("You are not part of the assigned team");
    }

    public void deleteTaskDetails(Long taskId,
                                  Long taskDetailsId,
                                  UserPrincipal userPrincipal) throws AccessDeniedException {
        TaskDetails taskDetails = getTaskDetails( taskId , taskDetailsId , userPrincipal);
        if(taskDetails.getMadeBy().getUser().getUsername().equals(userPrincipal.getUsername())) {
            taskDetailsRepository.delete(taskDetails);
            return;
        }
        throw new RuntimeException("Task details not created by you");

    }

    public TaskDetails updateTaskDetails(Long taskId,
                                         Long taskDetailsId,
                                         @Valid TaskDetailsDTO taskDetailsDTO,
                                         UserPrincipal userPrincipal) throws AccessDeniedException {
        TaskDetails taskDetails = getTaskDetails(taskId , taskDetailsId , userPrincipal);
        if(taskDetails.getMadeBy().getUser().getUsername().equals(userPrincipal.getUsername())){
            if(!taskDetailsDTO.getComment().isBlank())
                taskDetails.setComment(taskDetailsDTO.getComment());
            taskDetails.setHoursWorked(taskDetailsDTO.getHoursWorked());
            taskDetails.setPercentageCompleted(taskDetailsDTO.getPercentageCompleted());
            taskDetails.setTimestamp();
            taskDetailsRepository.save(taskDetails);
            return taskDetails;
        }
        throw new AccessDeniedException("This task detail was not made by you");
    }

    public boolean isManaged(Project project , UserPrincipal userPrincipal){
        return userPrincipal.getUsername().equals(project.getManager().getUser().getUsername());
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public List<TaskDetails> getTaskAllDetails(Long taskId, UserPrincipal userPrincipal) throws AccessDeniedException {
        Task task = findTaskById(taskId);
        Project project = task.getProject();
        if ((isManaged(project , userPrincipal)) || partOfTeam(task , userPrincipal)){
            return task.getTaskDetails();
        }
        throw new AccessDeniedException("You are not part of the assigned team");
    }
}
