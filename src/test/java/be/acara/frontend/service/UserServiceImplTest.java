package be.acara.frontend.service;

import be.acara.frontend.controller.dto.LikeEventDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static be.acara.frontend.util.UserUtil.firstUserDomain;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    @Mock
    private SecurityContext securityContext;
    
    private User user;
    
    private static String ROLE_USER = "ROLE_USER";
    
    @BeforeEach
    void setUp() {
        user = firstUserDomain();
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

    @Test
    void doesUserLikeThisEvent(){
        when(userFeignClient.checkIfUserLikesThisEvent(1L)).thenReturn(true);
        assertTrue(userFeignClient.checkIfUserLikesThisEvent(1L));
        verify(userFeignClient, times(1)).checkIfUserLikesThisEvent(1L);
    }

    @Test
    void loadUserByUsername_notFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("admin"));
    }

    @Test
    void loadUserByUsername_doesNotContainAuthHeader() {
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();
        when(userFeignClient.login(anyString())).thenReturn(responseEntity);

        UserDetails answer = userService.loadUserByUsername("admin");

        assertThat(answer).isNull();
    }

    @Test
    void loadUserByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<Void> responseEntity = ResponseEntity.ok()
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.uOb7Wfaoew2r7cVrjuCLybLC4AzDGGgJ33MraoobnVeWrHJcuQeaAQCl7Yxligf5BpV68zLwpmVjFeM8MxJCyw")
                .build();
        when(userFeignClient.login(anyString())).thenReturn(responseEntity);

        Role role = new Role();
        role.setId(1L);
        role.setName("USER");

        UserDetails answer = userService.loadUserByUsername("admin");


        assertThat(answer).isNotNull();
        assertThat(answer).isInstanceOf(User.class);
        assertThat(answer.getUsername()).isEqualTo("username");
        assertThat(answer.getPassword()).isEqualTo("password");
        assertThat(answer.getAuthorities()).isNotEmpty();
        assertThat(answer.getAuthorities()).extracting(GrantedAuthority::getAuthority).contains(role.getName());
    }
    
    @Test
    @WithMockUser
    void getCurrentUser() {
        setAuthenticationMocks(firstUserDomain());
        
        when(userRepository.findByUsername("user")).thenReturn(user);
    
        User answer = userService.getCurrentUser();
        
        assertThat(answer).isEqualTo(this.user);
        
        verify(userRepository, times(1)).findByUsername("user");
    }
    
    @Test
    @WithMockUser
    void getCurrentUser_anonymousUser() {
        setAuthenticationMocks(firstUserDomain(), "anonymousUser");
        
        User answer = userService.getCurrentUser();
        
        assertThat(answer).isNull();
    
        verify(userRepository, times(0)).findByUsername("anonymousUser");
    }
    
    @Test
    void likeEvent() {
        Long eventId = 1L;
        Long userId = 1L;
        
        setAuthenticationMocks(this.user);
        LikeEventDto likeEventDto = new LikeEventDto(eventId);
        
        doNothing().when(userFeignClient).likeEvent(userId, likeEventDto);
        
        userService.likeEvent(eventId);
    
        verify(userRepository, times(1)).findByUsername("user");
        verify(userFeignClient, times(1)).likeEvent(eventId, likeEventDto);
    }
    
    @Test
    void disLikeEvent() {
        Long eventId = 1L;
        setAuthenticationMocks(this.user);
        
        doNothing().when(userFeignClient).dislikeEvent(user.getId(), eventId);
        
        userService.dislikeEvent(eventId);
        
        verify(userRepository, times(1)).findByUsername("user");
        verify(userFeignClient, times(1)).dislikeEvent(user.getId(), eventId);
    }
    
    private void setAuthenticationMocks(User user) {
        setAuthenticationMocks(user, "user");
    }
    
    private void setAuthenticationMocks(User user, String name) {
        Authentication auth = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
        
        when(auth.getName()).thenReturn(name);
        
        if (!name.equals("anonymousUser")) {
            when(userService.findByUsername(auth.getName())).thenReturn(user);
        }
    }
}
