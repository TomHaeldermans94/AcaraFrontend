package be.acara.frontend.controller;

import be.acara.frontend.security.MethodSecurityConfigurer;
import be.acara.frontend.security.TokenLogoutHandler;
import be.acara.frontend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FaviconController.class)
@Import(MethodSecurityConfigurer.class)
class FaviconControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @MockBean
    private TokenLogoutHandler tokenLogoutHandler;
    
    @Test
    void returnNoFavicon() throws Exception {
        mockMvc.perform(get("/favicon.ico"))
                .andExpect(status().isOk());
    }
}
