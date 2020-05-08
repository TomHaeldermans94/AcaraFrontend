package be.acara.frontend.service;

import be.acara.frontend.controller.dto.LikeEventDto;
import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.domain.JwtToken;
import be.acara.frontend.domain.User;
import be.acara.frontend.exception.IdNotFoundException;
import be.acara.frontend.exception.SomethingWentWrongException;
import be.acara.frontend.exception.UserNotFoundException;
import be.acara.frontend.model.UserModel;
import be.acara.frontend.repository.RoleRepository;
import be.acara.frontend.repository.UserRepository;
import be.acara.frontend.service.mapper.UserMapper;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static be.acara.frontend.security.SecurityConstants.SECRET;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Service
@Primary
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFeignClient userFeignClient;
    private final UserMapper userMapper;
    private final JwtTokenService jwtTokenService;
    
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, @Lazy PasswordEncoder passwordEncoder, UserFeignClient userFeignClient, UserMapper userMapper, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userFeignClient = userFeignClient;
        this.userMapper = userMapper;
        this.jwtTokenService = jwtTokenService;
    }
    
    @Override
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        ResponseEntity<Void> signUpResponse = userFeignClient.signUp(user);
        if (!signUpResponse.getStatusCode().is2xxSuccessful()) {
            throw new SomethingWentWrongException();
        }
        user.setRoles(Set.of(roleRepository.findByName("ROLE_USER")));
        userRepository.save(user);
    }
    
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found", id)));
    }
    
    @Override
    public void editUser(UserModel user) {
        editBackEndUser(user);
        editFrontEndUser(user);
        jwtTokenService.remove(user.getUsername());
    }


    @Override
    public void likeEvent(Long id) {
        Long userId = getCurrentUser().getId();
        LikeEventDto likeEventDto = new LikeEventDto(id);
        userFeignClient.likeEvent(userId, likeEventDto);
    }

    @Override
    public void dislikeEvent(Long eventId) {
        Long userId = getCurrentUser().getId();
        userFeignClient.dislikeEvent(userId, eventId);
    }

    private void editBackEndUser(UserModel user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserDto userDto = userMapper.userModelToUserDto(user);
        ResponseEntity<Void> response = userFeignClient.editUser(user.getId(), userDto);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new SomethingWentWrongException();
        }
    }
    
    private void editFrontEndUser(UserModel user) {
        User userFromDb = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException(String.format("User with ID %d not found", user.getId())));
        if (!userFromDb.getId().equals(user.getId())) {
            throw new IdNotFoundException(String.format("Id of user to edit does not match given id. User id = %d, and given id = %d", userFromDb.getId(), user.getId()));
        }
        if (!userFromDb.getFirstName().equals(user.getFirstName())) {
            userFromDb.setFirstName(user.getFirstName());
        }
        if (!userFromDb.getLastName().equals(user.getLastName())) {
            userFromDb.setLastName(user.getLastName());
        }
        if (!userFromDb.getPassword().equals(user.getPassword())) {
            userFromDb.setPassword(user.getPassword());
        }
        userRepository.saveAndFlush(userFromDb);
    }

    @Override
    public User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(userName)) {
            return null;
        }
        return findByUsername(userName);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        ResponseEntity<Void> login = userFeignClient.login(String.format("{\"username\": \"%s\", \"password\": \"%s\"}", user.getUsername(), user.getPassword()));
        if (!login.getHeaders().containsKey("Authorization")) {
            return null;
        }
        String authHeader = login.getHeaders().get("Authorization").get(0);
        DecodedJWT token = JWT.require(HMAC512(SECRET.getBytes()))
                .build()
                .verify(authHeader.replace("Bearer ", ""));
        JwtToken jwtToken = JwtToken.builder()
                .token(token.getToken())
                .username(username)
                .expirationDate(token.getExpiresAt())
                .build();
        jwtTokenService.save(jwtToken);

        return user;
    }
}
