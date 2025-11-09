package com.softManager.project_management_system.services;

import com.softManager.project_management_system.dto.MemberDTO;
import com.softManager.project_management_system.dto.TeamMemberDTO;
import com.softManager.project_management_system.model.*;
import com.softManager.project_management_system.repository.MemberRepository;
import com.softManager.project_management_system.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ProjectServices projectServices;

    public MemberDTO createMember(String username, MemberDTO memberDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found with username: " + username));

        if (user.getMember() != null) {
            throw new DataIntegrityViolationException("User is already a member");
        }

        Member member = modelMapper.map(memberDTO, Member.class);
        member.setUser(user);
        Member savedMember = memberRepository.save(member);

        user.setMember(savedMember);
        user.getRoles().add(Role.MEMBER);

        userRepository.save(user);

        return modelMapper.map(savedMember, MemberDTO.class);
    }

    public MemberDTO getMemberByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Member member = user.getMember();
        if (member == null) {
            throw new RuntimeException("This user is not a member");
        }

        return modelMapper.map(member, MemberDTO.class);
    }

    public Member getMemberByUsernameSpecial(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Member member = user.getMember();
        if (member == null) {
            throw new RuntimeException("This user is not a member");
        }

        return member;
    }

    private boolean managesAny(UserPrincipal userPrincipal){
        List<Project> managed =  projectServices.getAllProjectsSpecial().stream().
                filter(Project -> Project.getManager().getUser().getUsername().equals(userPrincipal.getUsername()))
                .toList();
        return !managed.isEmpty();
    }

    public List<TeamMemberDTO> getAllMembers(UserPrincipal userprincipal) {
        if(userprincipal.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ADMIN"))
         || managesAny(userprincipal)) {
            return memberRepository.findAll()
                    .stream()
                    .map(member -> modelMapper.map(member, TeamMemberDTO.class))
                    .collect(Collectors.toList());
        }
        throw new AccessDeniedException("You are not admin or project manager");
    }

    public void deleteMember(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Member member = user.getMember();
        if (member == null) {
            throw new RuntimeException("This user is not a member");
        }

        user.setMember(null);
        userRepository.save(user);
        memberRepository.delete(member);
    }

    public MemberDTO updateMember(String username, MemberDTO memberDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        Member member = user.getMember();
        if (member == null) {
            throw new RuntimeException("This user is not a member");
        }

        member.setSalary(memberDTO.getSalary());
        member.setPosition(memberDTO.getPosition());
        member.setName(memberDTO.getName());

        Member updatedMember = memberRepository.save(member);
        return modelMapper.map(updatedMember, MemberDTO.class);
    }
}



