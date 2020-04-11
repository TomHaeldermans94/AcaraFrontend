package be.acara.frontend.service;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.controller.dto.EventDtoList;

import java.util.List;
import java.util.Map;

public interface EventService {
    EventDto getEvent(Long id);
    
    void delete(Long id);
    
    EventDtoList findAllEvents(int page, int size);
    
    List<String> getCategories();
    
    void addEvent(EventDto eventDto);
    
    void editEvent(Long id, EventDto eventDto);
    
    EventDtoList search(Map<String, String> params);
}
