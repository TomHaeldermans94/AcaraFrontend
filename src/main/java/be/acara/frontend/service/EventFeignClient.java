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
    EventDtoList getEvents(@RequestParam Map<String, String> searchParams, @RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("sort") String sort);
    
    @GetMapping("/{id}")
    EventDto getEventById(@PathVariable("id") Long id);

    @GetMapping("/categories")
    CategoriesList getAllCategories();

    @PostMapping
    void addEvent(EventDto eventDto);

    @PutMapping("/{id}")
    void editEvent(@PathVariable("id") Long id, EventDto eventDto);

    @DeleteMapping("/{id}")
    void deleteEvent(@PathVariable("id") Long id);

    @GetMapping("/userevents/{id}")
    EventDtoList getAllEventsFromSelectedUser(@PathVariable("id") Long id, @RequestParam("page") int page, @RequestParam("size") int size);

    @GetMapping("/likedevents/{id}")
    EventDtoList getAllEventsThatUserLiked(@PathVariable("id") Long id, @RequestParam("page") int page, @RequestParam("size") int size);
}
