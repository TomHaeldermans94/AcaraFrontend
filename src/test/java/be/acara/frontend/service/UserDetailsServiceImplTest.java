package be.acara.frontend.service;

import be.acara.frontend.domain.Role;
import be.acara.frontend.domain.User;
import be.acara.frontend.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserFeignClient userFeignClient;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private User user;
    @Mock
    private JWT jwt;
    @Mock
    private DecodedJWT decodedJWT;
    
    @Test
    void loadUserByUsername_notFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("admin"));
    }
    
    @Test
    void loadUserByUsername_doesNotContainAuthHeader() {
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();
        when(userFeignClient.login(anyString())).thenReturn(responseEntity);
    
        UserDetails answer = userDetailsService.loadUserByUsername("admin");
        
        assertThat(answer).isNull();
    }
    
    @Test
    void loadUserByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<Void> responseEntity = ResponseEntity.ok()
                .header("Authorization", "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.uOb7Wfaoew2r7cVrjuCLybLC4AzDGGgJ33MraoobnVeWrHJcuQeaAQCl7Yxligf5BpV68zLwpmVjFeM8MxJCyw")
                .build();
        when(userFeignClient.login(anyString())).thenReturn(responseEntity);
        when(user.getUsername()).thenReturn("username");
        when(user.getPassword()).thenReturn("password");
        Role role = new Role();
        role.setId(1L);
        role.setName("USER");
        when(user.getRoles()).thenReturn(Set.of(role));
        
    
        UserDetails answer = userDetailsService.loadUserByUsername("admin");
    
        
        assertThat(answer).isNotNull();
        assertThat(answer).isInstanceOf(org.springframework.security.core.userdetails.User.class);
        assertThat(answer.getUsername()).isEqualTo("username");
        assertThat(answer.getPassword()).isEqualTo("password");
        assertThat(answer.getAuthorities()).extracting("role").contains(role.getName());
    }
}
