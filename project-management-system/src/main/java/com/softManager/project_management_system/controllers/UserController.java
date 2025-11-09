package com.softManager.project_management_system.controllers;

import com.softManager.project_management_system.dto.UserDTO;
import com.softManager.project_management_system.model.Role;
import com.softManager.project_management_system.model.User;
import com.softManager.project_management_system.model.UserPrincipal;
import com.softManager.project_management_system.repository.UserRepository;
import com.softManager.project_management_system.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    //for admin
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build(); // Correct for empty responses
        }
        return ResponseEntity.ok(users); // Returns users with HTTP 200 status
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        String token = userService.verify(userDTO);
        if(token.equals("Failed"))
            throw new BadCredentialsException("Bad credentials");
        String username = userDTO.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));

        Set<Role> roles = user.getRoles();
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", username);
        response.put("roles",roles.stream()
                .map(Enum::name) // Convert Enum to String
                .collect(Collectors.joining(",")));
        return ResponseEntity.ok(response);
    }



    @DeleteMapping("/{username}/{password}")
    public ResponseEntity<?> deleteUser(@PathVariable String username, @PathVariable String password) {
        userService.deleteUser(username,password);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

