package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CategoriesList;
import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.controller.dto.EventDtoList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(value = "eventFeignClient", url = "${baseURL}/api/events")
public interface EventFeignClient {

    @GetMapping
    EventDtoList getEvents();
    
    @GetMapping("/{id}")
    EventDto getEventById(@PathVariable("id") Long id);

    @GetMapping("/categories")
    CategoriesList getAllCategories();

    @PostMapping
    void addEvent(EventDto eventDto);

    @PutMapping("/{id}")
    void editEvent(@PathVariable("id") Long id, EventDto eventDto);
    
    @GetMapping("/search")
    EventDtoList search(@RequestParam Map<String, String> searchParams);

    @DeleteMapping("/{id}")
    void deleteEvent(@PathVariable("id") Long id);
}
