package be.acara.frontend.service;

import be.acara.frontend.domain.User;
import be.acara.frontend.model.UserModel;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public interface UserService extends UserDetailsService {
    void save(User user);
    
    User findByUsername(String username);
    
    User getUser(Long id);
    
    void editUser(UserModel user);
    
    @Transactional(readOnly = true)
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
