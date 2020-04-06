package be.acara.frontend.service;

import be.acara.frontend.domain.User;
import be.acara.frontend.repository.RoleRepository;
import be.acara.frontend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFeignClient userFeignClient;
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserFeignClient userFeignClient) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userFeignClient = userFeignClient;
    }
    
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        ResponseEntity<Void> signUpResponse = userFeignClient.signUp(user);
        if (!signUpResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Something went wrong!");
        }
        userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
