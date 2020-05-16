package be.acara.frontend.service;

import be.acara.frontend.controller.dto.CategoriesList;
import be.acara.frontend.controller.dto.CategoryDto;
import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.controller.dto.EventDtoList;
import be.acara.frontend.util.EventUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static be.acara.frontend.util.EventUtil.createEventDtoList;
import static be.acara.frontend.util.EventUtil.firstEventDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    
    @Mock
    private EventFeignClient eventFeignClient;
    private EventService eventService;
    
    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(eventFeignClient);
    }
    
    @Test
    void getEvent() {
        Long id = 1L;
        EventDto eventDto = firstEventDto();
        when(eventFeignClient.getEventById(id)).thenReturn(eventDto);
    
        EventDto answer = eventService.getEvent(id);
    
        assertThat(answer).isEqualTo(eventDto);
        
        verifyOnce().getEventById(id);
    }
    
    @Test
    void delete() {
        Long id = 1L;
        doNothing().when(eventFeignClient).deleteEvent(id);
        
        eventService.delete(id);
        
        verifyOnce().deleteEvent(id);
    }
    
    @Test
    void findAllEvents() {
        EventDtoList eventDtoList = createEventDtoList();
        int page = eventDtoList.getNumber();
        int size = eventDtoList.getSize();
        String sort = "";
        Map<String, String> emptyParams = Collections.emptyMap();
        
        when(eventFeignClient.getEvents(emptyParams, page, size, sort)).thenReturn(eventDtoList);
    
        EventDtoList answer = eventService.findAllEvents(emptyParams, page, size, sort);
        
        assertThat(answer).isEqualTo(eventDtoList);
        verifyOnce().getEvents(emptyParams, page, size, sort);
    }
    
    @Test
    void getCategories() {
        CategoriesList categoriesList = new CategoriesList(List.of(new CategoryDto("MUSIC", "Music")));
        
        when(eventFeignClient.getAllCategories()).thenReturn(categoriesList);
        
        List<CategoryDto> answer = eventService.getCategories();
        
        assertThat(answer).isEqualTo(categoriesList.getCategories());
        verifyOnce().getAllCategories();
    }
    
    @Test
    void addEvent() {
        EventDto eventDto = firstEventDto();
        doNothing().when(eventFeignClient).addEvent(eventDto);
        
        eventService.addEvent(eventDto);
        
        verifyOnce().addEvent(eventDto);
    }
    
    @Test
    void editEvent() {
        Long id = 1L;
        EventDto eventDto = firstEventDto();
        doNothing().when(eventFeignClient).editEvent(id, eventDto);
    
        eventService.editEvent(id, eventDto);
    
        verifyOnce().editEvent(id,eventDto);
    }
    
    @Test
    void getEventsFromUser() {
        Long id = 1L;
        EventDtoList eventDtoList = createEventDtoList();
        when(eventFeignClient.getAllEventsFromSelectedUser(id, eventDtoList.getNumber(), eventDtoList.getSize())).thenReturn(eventDtoList);
    
        EventDtoList answer = eventService.getEventsFromUser(id, eventDtoList.getNumber(), eventDtoList.getSize());
        
        assertThat(answer).isEqualTo(eventDtoList);
        verifyOnce().getAllEventsFromSelectedUser(id, eventDtoList.getNumber(), eventDtoList.getSize());
        
    }
    
    @Test
    void getEventsThatUserLiked() {
        Long id = 1L;
        int page = 0;
        int size = 0;
        
        when(eventFeignClient.getAllEventsThatUserLiked(id,page,size)).thenReturn(EventUtil.createEventDtoList());
    
        EventDtoList answer = eventService.getEventsThatUserLiked(id, page, size);
        
        assertThat(answer).isEqualTo(EventUtil.createEventDtoList());
        
        verifyOnce().getAllEventsThatUserLiked(id, page, size);
    }
    
    private EventFeignClient verifyOnce() {
        return verify(eventFeignClient, times(1));
    }
}
