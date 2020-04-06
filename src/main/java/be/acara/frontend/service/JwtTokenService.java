package be.acara.frontend.service;

import be.acara.frontend.domain.JwtToken;
import be.acara.frontend.repository.TokenRepository;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    private final TokenRepository tokenRepository;
    
    public JwtTokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }
    
    public void save(JwtToken token) {
        JwtToken existingToken = getToken(token.getUsername());
        if (existingToken != null) {
            token.setId(existingToken.getId());
        }
        this.tokenRepository.saveAndFlush(token);
    }
    
    public JwtToken getToken(String username) {
        return this.tokenRepository.findJwtTokenByUsername(username).orElse(null);
    }
    
    public void remove(String name) {
        tokenRepository.findJwtTokenByUsername(name).ifPresent(tokenRepository::delete);
    }
}
