package be.acara.frontend.service;

import be.acara.frontend.domain.JwtToken;
import be.acara.frontend.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {
    @Mock
    private TokenRepository tokenRepository;
    private JwtTokenService jwtTokenService;
    private JwtToken jwtToken;
    
    @BeforeEach
    void setUp() {
        this.jwtTokenService = new JwtTokenService(tokenRepository);
        jwtToken = JwtToken.builder()
                .id(1L)
                .token("test token")
                .username("username")
                .expirationDate(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .build();
    }
    
    @Test
    void save() {
        jwtToken.setId(null);
        jwtTokenService.save(jwtToken);
        verify(tokenRepository, times(1)).saveAndFlush(jwtToken);
    }
    
    @Test
    void saveWithExistingToken() {
        jwtToken.setId(null);
        when(tokenRepository.findJwtTokenByUsername(jwtToken.getUsername())).thenReturn(Optional.of(jwtToken));
        
        jwtTokenService.save(jwtToken);
        verify(tokenRepository, times(1)).saveAndFlush(jwtToken);
    }
    
    @Test
    void getToken() {
        when(tokenRepository.findJwtTokenByUsername(anyString())).thenReturn(Optional.of(jwtToken));
        
        JwtToken answer = jwtTokenService.getToken(jwtToken.getUsername());
        
        assertThat(answer).isEqualTo(jwtToken);
        verify(tokenRepository, times(1)).findJwtTokenByUsername(anyString());
    }
    
    @Test
    void remove() {
        when(tokenRepository.findJwtTokenByUsername(jwtToken.getUsername())).thenReturn(Optional.of(jwtToken));
        doNothing().when(tokenRepository).delete(jwtToken);
        
        jwtTokenService.remove(jwtToken.getUsername());
    
        verify(tokenRepository, times(1)).delete(jwtToken);
    }
}
