package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CategoryDto;
import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.controller.dto.EventDtoList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EventServiceImpl implements EventService {
    private final EventFeignClient eventFeignClient;
    
    public EventServiceImpl(EventFeignClient eventFeignClient) {
        this.eventFeignClient = eventFeignClient;
    }
    
    @Override
    public EventDto getEvent(Long id) {
        return eventFeignClient.getEventById(id);
    }
    
    @Override
    public void delete(Long id) {
        eventFeignClient.deleteEvent(id);
    }
    
    @Override
    public EventDtoList findAllEvents(int page, int size) {
        return eventFeignClient.getEvents(page, size);
    }
    
    @Override
    public List<CategoryDto> getCategories() {
        return eventFeignClient.getAllCategories().getCategories();
    }
    
    @Override
    public void addEvent(EventDto eventDto) {
        eventFeignClient.addEvent(eventDto);
    }
    
    @Override
    public void editEvent(Long id, EventDto eventDto) {
        eventFeignClient.editEvent(id, eventDto);
    }
    
    @Override
    public EventDtoList search(Map<String, String> params) {
        params.entrySet().removeIf(e -> e.getValue().isEmpty()); //remove empty values from the set to avoid errors when parsing dates or bigDecimals
        return eventFeignClient.search(params);
    }
}
