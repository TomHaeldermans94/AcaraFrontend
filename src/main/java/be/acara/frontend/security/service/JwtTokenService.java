package be.acara.frontend.security.service;

import be.acara.frontend.security.domain.JwtToken;
import be.acara.frontend.security.repository.TokenRepository;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    private final TokenRepository tokenRepository;
    
    public JwtTokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }
    
    public void save(JwtToken token) {
        this.tokenRepository.saveAndFlush(token);
    }
    
    public JwtToken getToken(String username) {
        return this.tokenRepository.findJwtTokenByUsername(username).orElse(null);
    }
    
    public void remove(String name) {
        tokenRepository.findJwtTokenByUsername(name).ifPresent(tokenRepository::delete);
    }
}
