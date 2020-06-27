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

    /**
     * Persists the token the user receives upon logging in on the backend
     * @param token the newly received JWT token of the user
     */
    public void save(JwtToken token) {
        JwtToken existingToken = getToken(token.getUsername());
        if (existingToken != null) {
            token.setId(existingToken.getId());
        }
        this.tokenRepository.saveAndFlush(token);
    }

    /**
     * Gets the token matching the given username
     * @param username the username bound to the token
     * @return a JwtToken object containing the user, token, etc.
     */
    public JwtToken getToken(String username) {
        return this.tokenRepository.findJwtTokenByUsername(username).orElse(null);
    }

    /**
     * Removes a token from the repository
     * @param name the username bound to the token to identify the token to remove
     */
    public void remove(String name) {
        tokenRepository.findJwtTokenByUsername(name).ifPresent(tokenRepository::delete);
    }
}
