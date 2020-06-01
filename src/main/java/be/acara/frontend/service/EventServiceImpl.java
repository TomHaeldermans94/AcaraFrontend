package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CategoryDto;
import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.controller.dto.EventDtoList;
import be.acara.frontend.controller.dto.TicketDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EventServiceImpl implements EventService {
    private final EventFeignClient eventFeignClient;
    
    public EventServiceImpl(EventFeignClient eventFeignClient) {
        this.eventFeignClient = eventFeignClient;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EventDto getEvent(Long id) {
        return eventFeignClient.getEventById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        eventFeignClient.deleteEvent(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EventDtoList findAllEvents(Map<String, String> params, int page, int size, String sort) {
        return eventFeignClient.getEvents(params, page, size, sort);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<CategoryDto> getCategories() {
        return eventFeignClient.getAllCategories().getCategories();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addEvent(EventDto eventDto) {
        eventFeignClient.addEvent(eventDto);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void editEvent(Long id, EventDto eventDto) {
        eventFeignClient.editEvent(id, eventDto);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EventDtoList getEventsFromUser(Long id, int page, int size) {
        return eventFeignClient.getAllEventsFromSelectedUser(id, page, size);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EventDtoList getEventsThatUserLiked(Long id, int page, int size) {
        return eventFeignClient.getAllEventsThatUserLiked(id, page, size);
    }
}
