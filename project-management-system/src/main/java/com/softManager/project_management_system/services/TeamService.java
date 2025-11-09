package com.softManager.project_management_system.services;

import com.softManager.project_management_system.dto.TeamDTO;
import com.softManager.project_management_system.dto.TeamMemberDTO;
import com.softManager.project_management_system.dto.TeamResponseDTO;
import com.softManager.project_management_system.model.*;
import com.softManager.project_management_system.repository.MemberRepository;
import com.softManager.project_management_system.repository.TaskRepository;
import com.softManager.project_management_system.repository.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProjectServices projectServices;

    @Autowired
    private MemberService memberService;



    private boolean managesAny(UserPrincipal userPrincipal){
        List<Project> managed =  projectServices.getAllProjectsSpecial().stream().
                filter(Project -> Project.getManager().getUser().getUsername().equals(userPrincipal.getUsername()))
                .toList();
        return !managed.isEmpty();
    }

    public TeamResponseDTO createTeam(TeamDTO teamDTO, UserPrincipal userPrincipal) throws AccessDeniedException {
        if( managesAny(userPrincipal) ){
            Team team = modelMapper.map(teamDTO, Team.class);
            team.setCreatedBy(memberService.getMemberByUsernameSpecial(userPrincipal.getUsername()));
            Team saved = teamRepository.save(team);
            return modelMapper.map(saved, TeamResponseDTO.class);
        }
        throw new AccessDeniedException("You need to be project manager");
    }

    public TeamResponseDTO getTeamById(Long id, UserPrincipal userPrincipal) throws AccessDeniedException {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));
        if(team.getCreatedBy().getUser().getUsername().equals(userPrincipal.getUsername()))
        return modelMapper.map(team, TeamResponseDTO.class);

        throw new AccessDeniedException("This team is not created by you");
    }

    public List<TeamResponseDTO> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(team -> modelMapper.map(team, TeamResponseDTO.class))
                .collect(Collectors.toList());
    }

    public TeamDTO updateTeam(Long id, TeamDTO teamDTO, UserPrincipal userPrincipal) throws AccessDeniedException {
        Team existing = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));

        if(existing.getCreatedBy().getUser().getUsername().equals(userPrincipal.getUsername())) {
            existing.setName(teamDTO.getName());
            Team updated = teamRepository.save(existing);
            return modelMapper.map(updated, TeamDTO.class);
        }
        throw new AccessDeniedException("This team is not created by you");
    }

    public void deleteTeam(Long id, UserPrincipal userPrincipal) throws AccessDeniedException {
        Team existing = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));

        if(existing.getCreatedBy().getUser().getUsername().equals(userPrincipal.getUsername())) {
            teamRepository.deleteById(id);
        }
        else {
            throw new AccessDeniedException("This team is not created by you");
        }
    }

    public void addMemberToTeam(Long teamId, Long memberId, UserPrincipal userPrincipal) throws AccessDeniedException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        if(team.getCreatedBy().getUser().getUsername().equals(userPrincipal.getUsername())) {
            member.setTeam(team);
            memberRepository.save(member);
        }
        else {
            throw new AccessDeniedException("This team is not created by you");
        }
    }

    public void removeMemberFromTeam(Long teamId, Long memberId, UserPrincipal userPrincipal) throws AccessDeniedException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        if(team.getCreatedBy().getUser().getUsername().equals(userPrincipal.getUsername())) {
            member.setTeam(null);
            team.getMembers().remove(member);
            memberRepository.save(member);
            teamRepository.save(team);
        }
        else {
            throw new AccessDeniedException("This team is not created by you");
        }
    }

    public List<TeamResponseDTO> getMyTeams(UserPrincipal userPrincipal) throws AccessDeniedException {
        if(managesAny(userPrincipal)){
            List<Team> myTeams = teamRepository.findAll()
                    .stream().
                    filter(team -> team.getCreatedBy().getUser().getUsername().equals(userPrincipal.getUsername()))
                    .toList();
            return myTeams != null ? myTeams.stream()
                    .map(team -> modelMapper.map(team, TeamResponseDTO.class))
                    .toList()
                    : Collections.emptyList();
        }
        throw new AccessDeniedException("You are not authorized");
    }

    public List<TeamMemberDTO> getTeamMembers(Long id, UserPrincipal userPrincipal) throws AccessDeniedException {
        TeamResponseDTO team = getTeamById(id , userPrincipal);
        return team.getMembers() != null ? team.getMembers() :  Collections.emptyList();
    }

    public TeamResponseDTO getTeamByTaskId(Long taskId, UserPrincipal userPrincipal) throws AccessDeniedException {
        Task task  = findTaskById(taskId);
        Project project = task.getProject();
        if ((isManaged(project , userPrincipal))){
            if(!(task.getTeam() == null)) {
                return modelMapper.map(task.getTeam(), TeamResponseDTO.class);
            }
            else{
                return null;
            }
        }
        throw new AccessDeniedException("You are not project manager");
    }

    public boolean isManaged(Project project , UserPrincipal userPrincipal){
        return userPrincipal.getUsername().equals(project.getManager().getUser().getUsername());
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

    }
}
