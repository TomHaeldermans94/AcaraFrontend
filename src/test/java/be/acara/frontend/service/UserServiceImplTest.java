package be.acara.frontend.service;

import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.domain.Role;
import be.acara.frontend.domain.User;
import be.acara.frontend.exception.IdNotFoundException;
import be.acara.frontend.exception.SomethingWentWrongException;
import be.acara.frontend.exception.UserNotFoundException;
import be.acara.frontend.model.UserModel;
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
        
        assertThrows(SomethingWentWrongException.class, () -> userService.save(user));
    
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
    
    @Test
    void editUser() {
        UserModel userModel = UserUtil.firstUser();
        UserDto userDto = UserUtil.firstUserDto();
        UserModel newUserModel = UserModel.builder()
                .id(userModel.getId())
                .firstName("newName")
                .lastName("newLastName")
                .emailConfirm("kek@kek.kek")
                .email("kek@kek.kek")
                .username("kek")
                .password("kek")
                .passwordConfirm("kek").build();
        
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.userModelToUserDto(newUserModel)).thenReturn(userDto);
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();
        when(userFeignClient.editUser(userDto.getId(), userDto)).thenReturn(responseEntity);
        when(userRepository.findById(userModel.getId())).thenReturn(Optional.of(user));
    
        userService.editUser(newUserModel);
        
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userMapper, times(1)).userModelToUserDto(newUserModel);
        verify(userFeignClient, times(1)).editUser(anyLong(), eq(userDto));
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).saveAndFlush(user);
    }
    
    @Test
    void editUser_mismatchingId() {
        UserDto userDto = UserUtil.firstUserDto();
        UserModel newUserModel = UserModel.builder()
                .id(44444L)
                .firstName("newName")
                .lastName("newLastName")
                .emailConfirm("kek@kek.kek")
                .email("kek@kek.kek")
                .username("kek")
                .password("kek")
                .passwordConfirm("kek").build();
    
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.userModelToUserDto(newUserModel)).thenReturn(userDto);
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();
        when(userFeignClient.editUser(newUserModel.getId(), userDto)).thenReturn(responseEntity);
        when(userRepository.findById(newUserModel.getId())).thenReturn(Optional.of(user));
    
        IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class, () -> userService.editUser(newUserModel));
        
        assertThat(idNotFoundException.getMessage()).isEqualTo(String.format("Id of user to edit does not match given id. User id = %d, and given id = %d", user.getId(), newUserModel.getId()));
    
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userMapper, times(1)).userModelToUserDto(newUserModel);
        verify(userFeignClient, times(1)).editUser(anyLong(), eq(userDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(0)).saveAndFlush(user);
    }
    
    @Test
    void editUser_backendError() {
        UserModel userModel = UserUtil.firstUser();
        UserDto userDto = UserUtil.firstUserDto();
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.userModelToUserDto(userModel)).thenReturn(userDto);
        ResponseEntity<Void> responseEntity = ResponseEntity.badRequest().build();
        when(userFeignClient.editUser(anyLong(), eq(userDto))).thenReturn(responseEntity);
    
        assertThrows(SomethingWentWrongException.class, () -> userService.editUser(userModel));
    
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userMapper, times(1)).userModelToUserDto(userModel);
        verify(userFeignClient, times(1)).editUser(anyLong(), eq(userDto));
        verify(userRepository, times(0)).findById(user.getId());
        verify(userRepository, times(0)).saveAndFlush(user);
    }
}
