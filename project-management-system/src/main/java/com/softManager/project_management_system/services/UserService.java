package com.softManager.project_management_system.services;

import com.softManager.project_management_system.dto.UserDTO;
import com.softManager.project_management_system.model.Role;
import com.softManager.project_management_system.model.User;
import com.softManager.project_management_system.model.UserPrincipal;
import com.softManager.project_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JWTService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encoder.encode(userDTO.getPassword()));
        User savedUser = userRepository.save(user);

        UserDTO savedDTO = new UserDTO();
        savedDTO.setUsername(savedUser.getUsername());
        savedDTO.setEmail(savedUser.getEmail());
        savedDTO.setPassword(savedUser.getPassword());
        return savedDTO;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setPassword(user.getPassword());
            return dto;
        }).collect(Collectors.toList());
    }


    public void deleteUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("The user with username "+username+" doesn't exist"));
    }

    public String verify(UserDTO userDTO) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(userDTO.getUsername(),userDTO.getPassword()));
        if(authentication.isAuthenticated())
            return  jwtService.generateToken(userDTO.getUsername());
        return "Failed";
    }

}

