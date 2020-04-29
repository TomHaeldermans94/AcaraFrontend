package be.acara.frontend.service;

import be.acara.frontend.domain.User;
import be.acara.frontend.model.UserModel;
import org.springframework.security.core.Authentication;

public interface UserService {
    void save(User user);
    
    User findByUsername(String username);
    
    User getUser(Long id);
    
    void editUser(UserModel user);
    
    boolean hasUserId(Authentication authentication, Long userId);
}
