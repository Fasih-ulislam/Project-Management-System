package com.softManager.project_management_system.controllers;

import com.softManager.project_management_system.dto.MemberDTO;
import com.softManager.project_management_system.dto.TeamMemberDTO;
import com.softManager.project_management_system.model.UserPrincipal;
import com.softManager.project_management_system.services.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PreAuthorize("hasAnyRole('USER','ADMIN','MEMBER')")
    @PostMapping
    public MemberDTO createMember(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                  @Valid @RequestBody MemberDTO memberDTO) {
        return memberService.createMember(userPrincipal.getUsername(), memberDTO);
    }


    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    @GetMapping
    public MemberDTO getMember(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return memberService.getMemberByUsername(userPrincipal.getUsername());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    @PutMapping
    public MemberDTO updateMember(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                  @Valid @RequestBody MemberDTO memberDTO) {
        return memberService.updateMember(userPrincipal.getUsername(), memberDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    @DeleteMapping
    public void deleteMember(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        memberService.deleteMember(userPrincipal.getUsername());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    @GetMapping("/all")
    public List<TeamMemberDTO> getAllMembers(@AuthenticationPrincipal UserPrincipal userprincipal) {
        return memberService.getAllMembers(userprincipal);
    }
}
