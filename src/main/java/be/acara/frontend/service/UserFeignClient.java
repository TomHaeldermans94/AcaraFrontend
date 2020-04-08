package be.acara.frontend.service;

import be.acara.frontend.controller.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "userFeignClient", url = "${baseURL}/api/users")
public interface UserFeignClient {

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
}
