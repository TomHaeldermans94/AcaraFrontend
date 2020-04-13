package be.acara.frontend.service;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.domain.User;
import be.acara.frontend.repository.RoleRepository;
import be.acara.frontend.repository.UserRepository;
import be.acara.frontend.service.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFeignClient userFeignClient;
    private final UserMapper userMapper;
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserFeignClient userFeignClient, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userFeignClient = userFeignClient;
        this.userMapper = userMapper;
    }
    
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        ResponseEntity<Void> signUpResponse = userFeignClient.signUp(user);
        if (!signUpResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Something went wrong!");
        }
        user.setRoles(Set.of(roleRepository.findByName("USER")));
        userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void editUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserDto userDto = userMapper.map(user);
        userFeignClient.editUser(user.getId(), userDto);
    }

    public boolean checkIfUserNameIsValid(User user) {
        boolean isValidUsername = true;
        //check if the username was edited
        UserDto userDtoFromDb = userFeignClient.getUserById(user.getId());
        if(!userDtoFromDb.getUsername().equals(user.getUsername())) {
            //check if the username is already in the database
            isValidUsername = !userFeignClient.checkUsername(user.getUsername());
        }
        user.setUniqueUsername(isValidUsername);
        return isValidUsername;
    }
}
