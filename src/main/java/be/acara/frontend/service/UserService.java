package be.acara.frontend.service;

import be.acara.frontend.domain.User;

public interface UserService {
    void save(User user);
    
    User findByUsername(String username);
}
