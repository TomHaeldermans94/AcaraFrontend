package be.acara.frontend.service;

import be.acara.frontend.controller.dto.LikeEventDto;
import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "userFeignClient", url = "${baseURL}")
public interface UserFeignClient {
    
    @PostMapping("/api/users/sign-up")
    ResponseEntity<Void> signUp(User user);
    
    @PostMapping("/login")
    ResponseEntity<Void> login(@RequestBody String payload);
    
    @GetMapping("/api/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);

    @PutMapping("/api/users/{id}")
    ResponseEntity<Void> editUser(@PathVariable("id") Long id, UserDto userDto);

    @GetMapping("/api/users/username/{username}")
    boolean checkUsername(@PathVariable("username") String username);

    @GetMapping("/api/users/{id}/likes")
    boolean checkIfUserLikesThisEvent(@PathVariable("id") Long id);

    @PostMapping("/api/users/{userId}/likes")
    void likeEvent(@PathVariable("userId") Long id, @RequestBody LikeEventDto likeEventDto);

    @DeleteMapping("/api/users/{userId}/likes/{eventId}")
    void dislikeEvent(@PathVariable("userId") Long userId, @PathVariable("eventId") Long eventId);
}
