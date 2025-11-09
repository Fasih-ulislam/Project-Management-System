package com.softManager.project_management_system.controllers;

import com.softManager.project_management_system.dto.TeamDTO;
import com.softManager.project_management_system.dto.TeamMemberDTO;
import com.softManager.project_management_system.dto.TeamResponseDTO;
import com.softManager.project_management_system.model.UserPrincipal;
import com.softManager.project_management_system.services.TaskServices;
import com.softManager.project_management_system.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/teams")
@PreAuthorize("hasRole('MEMBER')")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TaskServices taskService;

    @PostMapping
    public TeamResponseDTO createTeam(@RequestBody TeamDTO teamDTO ,
                              @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        return teamService.createTeam(teamDTO , userPrincipal);
    }

    @GetMapping("/{id}")
    public TeamResponseDTO getTeam(@PathVariable Long id,
                                   @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        return teamService.getTeamById(id , userPrincipal);
    }

    @GetMapping("/my-Teams")
    public List<TeamResponseDTO> getTeam(@AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        return teamService.getMyTeams(userPrincipal);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<TeamResponseDTO> getAllTeams() {
        return teamService.getAllTeams();
    }

    @PutMapping("/{id}")
    public TeamDTO updateTeam(@PathVariable Long id,
                              @RequestBody TeamDTO teamDTO,
                              @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        return teamService.updateTeam(id, teamDTO , userPrincipal);
    }

    @DeleteMapping("/{id}")
    public void deleteTeam(@PathVariable Long id,
                           @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        teamService.deleteTeam(id , userPrincipal);
    }

    @GetMapping("/{teamId}/members/{memberId}")
    public void addMemberToTeam(@PathVariable Long teamId,
                                @PathVariable Long memberId ,
                                @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        teamService.addMemberToTeam(teamId, memberId ,userPrincipal);
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    public void removeMemberFromTeam(@PathVariable Long teamId,
                                @PathVariable Long memberId ,
                                @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        teamService.removeMemberFromTeam(teamId, memberId ,userPrincipal);
    }

    @GetMapping("/tasks/{taskId}/assign-to-team/{teamId}")
    public void assignTaskToTeam(@PathVariable Long taskId,
                                 @PathVariable Long teamId,
                                 @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        taskService.assignTaskToTeam(taskId, teamId , userPrincipal);
    }

    @DeleteMapping("/tasks/{taskId}/remove-team/{teamId}")
    public void removeTaskFromTeam(@PathVariable Long taskId,
                                 @PathVariable Long teamId,
                                 @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
        taskService.removeTaskFromTeam(taskId, teamId , userPrincipal);
    }

   @GetMapping("/{teamId}/members")
   public List<TeamMemberDTO> getTeamMembers(@PathVariable Long teamId,
                                        @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
       return teamService.getTeamMembers(teamId, userPrincipal);
   }

   @GetMapping("/{taskId}/team")
   public TeamResponseDTO getTeamOfTask(@PathVariable Long taskId,
                                  @AuthenticationPrincipal UserPrincipal userPrincipal) throws AccessDeniedException {
       return teamService.getTeamByTaskId(taskId , userPrincipal);
   }


}



