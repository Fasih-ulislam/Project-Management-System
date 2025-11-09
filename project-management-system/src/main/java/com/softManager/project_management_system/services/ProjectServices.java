package com.softManager.project_management_system.services;

import com.softManager.project_management_system.dto.ProjectDTO;
import com.softManager.project_management_system.model.Member;
import com.softManager.project_management_system.model.Project;
import com.softManager.project_management_system.model.UserPrincipal;
import com.softManager.project_management_system.repository.MemberRepository;
import com.softManager.project_management_system.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectServices {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    private void updateProjectStatusIfAllTasksCompleted(Project project) {
        boolean allTasksCompleted = project.getTasks() != null &&
                !project.getTasks().isEmpty() &&
                project.getTasks().stream().allMatch(task -> Boolean.TRUE.equals(task.isCompleted()));

        if (allTasksCompleted) {
            project.setStatus("Completed");
        } else {
            project.setStatus("In progress");
        }

        projectRepository.save(project);
    }


    public Project createProject(ProjectDTO dto) {
        Project project = modelMapper.map(dto, Project.class);

        Member manager = memberRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + dto.getManagerId()));

        project.setManager(manager);
        project.setStatus("In progress");
        return projectRepository.save(project);
    }

    public List<ProjectDTO> getAllProjects() {
        // Update status for each project
        List<Project> all = projectRepository.findAll();
        for (Project project : all) {
            updateProjectStatusIfAllTasksCompleted(project);
        }
        List<ProjectDTO> projects = all.stream()
                .map(project -> {
                    ProjectDTO dto = modelMapper.map(project, ProjectDTO.class);
                    if (project.getManager() != null) {
                        dto.setManagerId(project.getManager().getId());
                    }
                    return dto;
                })
                .toList();

        return projects; // Return the list
    }

    public List<Project> getAllProjectsSpecial() {
        // Update status for each project
        return projectRepository.findAll();
    }


    public Project getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(()-> new RuntimeException("Project not found"));
        updateProjectStatusIfAllTasksCompleted(project);
        return project;
    }

    public Project updateProject(Long id, ProjectDTO dto) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Manually update only the allowed fields
        existingProject.setName(dto.getName());
        existingProject.setDescription(dto.getDescription());
        existingProject.setStartDate(dto.getStartDate());
        existingProject.setEndDate(dto.getEndDate());
        existingProject.setBudget(dto.getBudget());

        Member manager = memberRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + dto.getManagerId()));
        existingProject.setManager(manager);

        return projectRepository.save(existingProject);
    }


    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public void deleteAllProjects() {
        projectRepository.deleteAll();
    }

    public List<ProjectDTO> getManagerProjects(UserPrincipal userPrincipal) {
        List<Project> all = projectRepository.findAll();

        // Filter projects where the manager's username matches
        List<Project> managed = all.stream()
                .filter(project -> project.getManager() != null &&
                        project.getManager().getUser().getUsername().equals(userPrincipal.getUsername()))
                .toList();

        if (managed.isEmpty()) {
            throw new RuntimeException("You are not managing any project.");
        }

        // Update project statuses
        for (Project project : managed) {
            updateProjectStatusIfAllTasksCompleted(project);
        }

        // Map to DTOs and set managerId
        return managed.stream()
                .map(project -> {
                    ProjectDTO dto = modelMapper.map(project, ProjectDTO.class);
                    dto.setManagerId(project.getManager().getId());
                    return dto;
                })
                .toList();
    }

}


