package be.acara.frontend.service;


import be.acara.frontend.domain.JwtToken;
import be.acara.frontend.domain.User;
import be.acara.frontend.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static be.acara.frontend.security.SecurityConstants.SECRET;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private JwtTokenService tokenService;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
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
        tokenService.save(jwtToken);
    
        return user;
    }
}
