package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CreateOrderDto;
import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.controller.dto.UserDto;
import be.acara.frontend.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "orderFeignClient", url = "${baseURL}")
public interface OrderFeignClient {

    @PostMapping("/api/orders/")
    ResponseEntity<Void> create(CreateOrderDto createOrderDto);

    @PutMapping("/api/orders/{id}")
    void edit(@PathVariable("id") Long id, CreateOrderDto createOrderDto);

    @DeleteMapping("/api/orders/{id}")
    void remove(@PathVariable("id") Long id);
}
