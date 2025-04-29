package org.meteoinit.skywatch.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.meteoinit.skywatch.config.JwtCore;
import org.meteoinit.skywatch.config.UserDetailsImpl;
import org.meteoinit.skywatch.dto.SignRequest;
import org.meteoinit.skywatch.model.Role;
import org.meteoinit.skywatch.model.User;
import org.meteoinit.skywatch.repository.RoleRepository;
import org.meteoinit.skywatch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class SecurityController {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setJwtCore(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }

    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody SignRequest signRequest) {
        if (userRepository.existsUserByUsername(signRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different name");
        }
        User user = new User();
        user.setUsername(signRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signRequest.getPassword()));

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found in database"));
        user.setRole(userRole);
        user.setEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok("Success, user created");
    }

    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody SignRequest signRequest) {

        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signRequest.getUsername(), signRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/guest")
    public ResponseEntity<?> guestAccess() {
        String plainPassword = UUID.randomUUID().toString();

        User guest = new User();
        guest.setUsername("guest_" + UUID.randomUUID());
        guest.setPassword(passwordEncoder.encode(plainPassword));
        guest.setEnabled(true);

        Role guestRole = roleRepository.findByRoleName("GUEST")
                .orElseThrow(() -> new RuntimeException("Guest role not found"));
        guest.setRole(guestRole);

        userRepository.save(guest);

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(guest.getUsername(), plainPassword)
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }

    @GetMapping("/role")
    public ResponseEntity<String> getUserRole(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String role = userDetails.getRole();
        return ResponseEntity.ok(role);
    }

    @GetMapping("/username")
    public ResponseEntity<String> getUsername(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        System.out.println(username);
        return ResponseEntity.ok(username);
    }

    @PostMapping("/logout-guest")
    public ResponseEntity<?> logoutGuest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String token = headerAuth.substring(7);
        String username = jwtCore.getNameFromJwt(token);

        User user = userRepository.findUserByUsername(username)
                .orElse(null);

        if (user != null && user.getRole().getRoleName().equals("GUEST")) {
            userRepository.delete(user);
        }
        //userRepository.deleteUserByUsername(username);

        return ResponseEntity.ok("Guest deleted");
    }
}
