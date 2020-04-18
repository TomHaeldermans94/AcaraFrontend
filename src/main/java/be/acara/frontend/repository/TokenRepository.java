package be.acara.frontend.repository;

import be.acara.frontend.domain.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<JwtToken, Long> {
    Optional<JwtToken> findJwtTokenByUsername(String username);
}
