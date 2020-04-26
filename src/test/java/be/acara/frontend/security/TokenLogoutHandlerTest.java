package be.acara.frontend.security;

import be.acara.frontend.service.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenLogoutHandlerTest {
    @Mock
    private Authentication authentication;
    @Mock
    private JwtTokenService jwtTokenService;
    private MockHttpServletRequest req;
    private MockHttpServletResponse res;
    @InjectMocks
    private TokenLogoutHandler tokenLogoutHandler;
    
    @BeforeEach
    void setUp() {
        req = new MockHttpServletRequest();
        res = new MockHttpServletResponse();
    }
    
    @Test
    void onLogoutSuccess_withoutAuthentication() throws IOException, ServletException {
        tokenLogoutHandler.onLogoutSuccess(req, res, null);
        assertThat(res.getRedirectedUrl()).isEqualTo("/events");
    }
    
    @Test
    void onLogoutSuccess() throws IOException, ServletException {
        doNothing().when(jwtTokenService).remove(any());
        tokenLogoutHandler.onLogoutSuccess(req, res, authentication);
        assertThat(res.getRedirectedUrl()).isEqualTo("/events");
        verify(jwtTokenService, times(1)).remove(any());
    }
}
