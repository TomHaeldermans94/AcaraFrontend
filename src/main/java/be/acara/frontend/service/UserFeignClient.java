package be.acara.frontend.service;

import be.acara.frontend.security.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "userFeignClient", url = "${baseURL}/api/users")
public interface UserFeignClient {

    @PostMapping("/sign-up")
    ResponseEntity<Void> signUp(User user);
    
    @PostMapping("/login")
    ResponseEntity<Void> login(@RequestBody String payload);
}
