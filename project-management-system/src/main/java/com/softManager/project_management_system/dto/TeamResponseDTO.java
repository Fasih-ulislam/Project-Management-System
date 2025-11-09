package com.softManager.project_management_system.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeamResponseDTO {
    private Long id;               // For identifying the team
    private String name;           // The name of the team
    private List<TeamMemberDTO> members;  // List of MemberDTOs associated with this team
}

