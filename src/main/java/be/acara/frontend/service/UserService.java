package be.acara.frontend.service;

import be.acara.frontend.domain.User;
import be.acara.frontend.model.UserModel;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    /**
     * Saves the user
     *
     * @param user the user to save
     */
    void save(User user);
    
    /**
     * Returns the user by the provided username
     *
     * @param username the username of the user to return
     * @return the user that has the matching username
     */
    User findByUsername(String username);
    
    /**
     * Gets an user by id
     *
     * @param id the id of the user to get
     * @return the user matching the given id
     */
    User getUser(Long id);
    
    /**
     * Edits the given user
     *
     * @param user the usermodel that contains all the changes
     */
    void editUser(UserModel user);
    
    /**
     * This method will like the specified id
     *
     * @param id the id of the event to like
     */
    void likeEvent(Long id);
    
    /**
     * This method will dislike the specified id
     *
     * @param id the id of the event to dislike
     */
    void dislikeEvent(Long id);
    
    /**
     * Helper method to get the current user from the SecurityContext
     *
     * @return null if not logged in, the user if logged in
     */
    User getCurrentUser();
}
