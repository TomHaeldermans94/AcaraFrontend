package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CreateOrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(value = "orderFeignClient", url = "${baseURL}")
public interface OrderFeignClient {

    @PostMapping("/api/orders/")
    ResponseEntity<Void> create(CreateOrderDto createOrderDto);

    @PutMapping("/api/orders/{id}")
    void edit(@PathVariable("id") Long id, CreateOrderDto createOrderDto);

    @DeleteMapping("/api/orders/{id}")
    void remove(@PathVariable("id") Long id);
}
