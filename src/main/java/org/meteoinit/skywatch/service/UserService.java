package org.meteoinit.skywatch.service;

import lombok.RequiredArgsConstructor;
import org.meteoinit.skywatch.config.UserDetailsImpl;
import org.meteoinit.skywatch.dto.UserDto;
import org.meteoinit.skywatch.model.User;
import org.meteoinit.skywatch.repository.RoleRepository;
import org.meteoinit.skywatch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", username)
        ));

        return UserDetailsImpl.build(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean deleteUser(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public void updateUser(Long id, UserDto updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id " + id));
        user.setEnabled(updatedUser.isEnabled());
        user.setRole(roleRepository.findByRoleName(updatedUser.getRole()).orElseThrow());
        userRepository.save(user);
    }
}
