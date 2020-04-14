package be.acara.frontend.service;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.domain.User;
import be.acara.frontend.exception.IdNotFoundException;
import be.acara.frontend.exception.UserNotFoundException;
import be.acara.frontend.model.UserModel;
import be.acara.frontend.repository.RoleRepository;
import be.acara.frontend.repository.UserRepository;
import be.acara.frontend.service.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFeignClient userFeignClient;
    private final UserMapper userMapper;
    
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserFeignClient userFeignClient, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userFeignClient = userFeignClient;
        this.userMapper = userMapper;
    }
    
    @Override
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        ResponseEntity<Void> signUpResponse = userFeignClient.signUp(user);
        if (!signUpResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Something went wrong!");
        }
        user.setRoles(Set.of(roleRepository.findByName("USER")));
        userRepository.save(user);
    }
    
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }
    
    @Override
    public void editUser(UserModel user){
        editBackEndUser(user);
        editFrontEndUser(user);
    }
    
    private void editBackEndUser(UserModel user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserDto userDto = userMapper.userModelToUserDto(user);
        userFeignClient.editUser(user.getId(), userDto);
    }
    
    private void editFrontEndUser(UserModel user){
        User userFromDb = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", user.getId())));
        if (!userFromDb.getId().equals(user.getId())) {
            throw new IdNotFoundException(String.format("Id of user to edit does not match given id. User id = %d, and given id = %d", userFromDb.getId(), user.getId())
            );
        }
        if(!userFromDb.getUsername().equals(user.getUsername())){
            userFromDb.setUsername(user.getUsername());
        }
        if(!userFromDb.getPassword().equals(user.getPassword())){
            userFromDb.setPassword(user.getPassword());
        }
        userRepository.saveAndFlush(userFromDb);
    }
    
    public boolean checkIfUserNameIsValid(UserModel user) {
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
