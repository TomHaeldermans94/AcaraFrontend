package be.acara.frontend.security;

import be.acara.frontend.exception.CustomAccessDeniedHandler;
import be.acara.frontend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    
    private static final String ROLE_ADMIN = "ADMIN";
    
    private final TokenLogoutHandler tokenLogoutHandler;
    
    public WebSecurityConfig(UserService userService, TokenLogoutHandler tokenLogoutHandler) {
        this.userService = userService;
        this.tokenLogoutHandler = tokenLogoutHandler;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
    
    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }
    
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login").not().authenticated()
                .antMatchers("/users/registration").not().authenticated()
                .antMatchers("/users/detail/{userId}").authenticated()
                .antMatchers("/events/search/**").permitAll()
                .antMatchers("/events/delete/{\\d+}").hasRole(ROLE_ADMIN)
                .antMatchers("/events/new").hasRole(ROLE_ADMIN)
                .antMatchers("/orders/**").authenticated()
                .antMatchers("/events/{\\d+}").hasRole(ROLE_ADMIN)
                .antMatchers("/users/profile").authenticated()
                .antMatchers("/users/{\\d+}").authenticated()
                .and()
                .formLogin()
                .defaultSuccessUrl("/events")
                .loginProcessingUrl("/login")
                .and()
                .logout()
                .logoutSuccessUrl("/events")
                .logoutSuccessHandler(tokenLogoutHandler)
                .deleteCookies("JSESSIONID")
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());
    
        // H2 webconsole stuff, don't add stuff to this
        http.authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .and()
                .csrf().disable()
                .headers().frameOptions().disable();
    }
}
