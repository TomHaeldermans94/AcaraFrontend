package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CreateOrderDto;
import be.acara.frontend.controller.dto.CreateOrderList;
import be.acara.frontend.controller.dto.TicketDto;
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
    
    @PostMapping("/api/orders/batch")
    void create(CreateOrderList createOrderList);

    @GetMapping("/api/orders/ticket/{eventId}")
    TicketDto getEventTicket(@PathVariable("eventId") Long eventId);
}
