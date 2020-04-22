package be.acara.frontend.service;

import be.acara.frontend.domain.Role;
import be.acara.frontend.domain.User;
import be.acara.frontend.exception.UserNotFoundException;
import be.acara.frontend.repository.RoleRepository;
import be.acara.frontend.repository.UserRepository;
import be.acara.frontend.service.mapper.UserMapper;
import be.acara.frontend.util.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserFeignClient userFeignClient;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private Role role;
    
    private User user;
    
    private static String ROLE_USER = "ROLE_USER";
    
    @BeforeEach
    void setUp() {
        user = UserUtil.firstUserDomain();
    }
    
    @Test
    void save() {
        user.setId(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        ResponseEntity<Void> responseVoid = ResponseEntity.ok().build();
        when(userFeignClient.signUp(user)).thenReturn(responseVoid);
        when(roleRepository.findByName(ROLE_USER)).thenReturn(role);
        when(userRepository.save(user)).thenReturn(user);
        
        userService.save(user);
        
        verify(passwordEncoder, times(1)).encode("password");
        verify(userFeignClient, times(1)).signUp(user);
        verify(roleRepository, times(1)).findByName(ROLE_USER);
        verify(userRepository, times(1)).save(user);
    }
    
    @Test
    void save_httpError() {
        user.setId(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        ResponseEntity<Void> responseVoid = ResponseEntity.badRequest().build();
        when(userFeignClient.signUp(user)).thenReturn(responseVoid);
        
        assertThrows(RuntimeException.class, () -> userService.save(user));
    
        verify(passwordEncoder, times(1)).encode("password");
        verify(userFeignClient, times(1)).signUp(user);
        verify(roleRepository, times(0)).findByName(ROLE_USER);
        verify(userRepository, times(0)).save(user);
    }
    
    @Test
    void findByUsername() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    
        User user = userService.findByUsername(this.user.getUsername());
        
        assertThat(user).isEqualTo(this.user);
    }
    
    @Test
    void getUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    
        User user = userService.getUser(this.user.getId());
        
        assertThat(user).isEqualTo(this.user);
    }
    
    @Test
    void getUser_withInvalidId() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
    
        UserNotFoundException answer = assertThrows(UserNotFoundException.class, () -> userService.getUser(this.user.getId()));
        
        assertThat(answer).hasMessage(String.format("User with id %d not found", this.user.getId()));
    
    }
}
