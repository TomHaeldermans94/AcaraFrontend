package be.acara.frontend.service;

import be.acara.frontend.domain.User;
import be.acara.frontend.model.UserModel;

public interface UserService {
    void save(User user);
    
    User findByUsername(String username);
    
    User getUser(Long id);
    
    void editUser(UserModel user);

    void likeOrDislike(Long id);

    void like(Long id);

    void dislike(Long id);

}
