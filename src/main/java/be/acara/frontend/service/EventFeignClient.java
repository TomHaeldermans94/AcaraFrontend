package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CategoriesList;
import be.acara.frontend.model.Event;
import be.acara.frontend.model.EventList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "eventFeignClient", url = "${baseURL}/api/events")
public interface EventFeignClient {

    @GetMapping
    EventList getEvents();
    
    @GetMapping("/{id}")
    Event getEventById(@PathVariable("id") Long id);

    @GetMapping("/categories")
    CategoriesList getAllCategories();

    @PostMapping
    void addEvent(Event event);

    @PutMapping("/{id}")
    void editEvent(@PathVariable("id") Long id, Event event);
    
    @GetMapping("/search")
    EventList search(@RequestParam Map<String, String> searchParams);

    @DeleteMapping("/{id}")
    void deleteEvent(@PathVariable("id") Long id);
}
