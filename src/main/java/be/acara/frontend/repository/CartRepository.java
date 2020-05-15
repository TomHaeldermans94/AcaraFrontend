package be.acara.frontend.repository;

import be.acara.frontend.domain.Cart;
import be.acara.frontend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, User> {
    Optional<Cart> findByUserUsername(String username);
}
