package com.bus.reservation.controller;

import com.bus.reservation.repository.RoleRepository;
import com.bus.reservation.repository.UserRepository;
import com.bus.reservation.dto.LoginDto;
import com.bus.reservation.dto.SignupDto;
import com.bus.reservation.entities.Role;
import com.bus.reservation.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?>authenticateUser(@RequestBody LoginDto loginDto){
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(),
                loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return new ResponseEntity<>("User signed in sucessfully", HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?>registerUser(@RequestBody SignupDto signupDto){
        if (userRepository.existsByUsername(signupDto.getUsername())){
            return new ResponseEntity<>("Username already taken", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(signupDto.getEmail())){
            return new ResponseEntity<>("Email already taken", HttpStatus.BAD_REQUEST);
        }
        User user=new User();
        user.setName(signupDto.getName());
        user.setUsername(signupDto.getUsername());
        user.setEmail(signupDto.getEmail());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        Role roles = roleRepository.findByName("ROLE_ADMIN").get();
        user.setRoles(Collections.singleton(roles));
        userRepository.save(user);

        return new ResponseEntity<>("User registered sucessfully", HttpStatus.OK);
    }
}
