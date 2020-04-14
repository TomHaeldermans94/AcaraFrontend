package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.controller.dto.EventDtoList;
import be.acara.frontend.model.EventModel;
import be.acara.frontend.security.TokenLogoutHandler;
import be.acara.frontend.service.EventService;
import be.acara.frontend.service.mapper.EventMapper;
import be.acara.frontend.util.WithMockAdmin;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static be.acara.frontend.util.EventUtil.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {
    @MockBean
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @MockBean
    private TokenLogoutHandler tokenLogoutHandler;
    @MockBean
    private EventService eventService;
    @MockBean
    private EventMapper mapper;
    
    @Autowired
    private MockMvc mockMvc;
    
    @AfterEach
    void tearDown() {
        reset(eventService);
    }
    
    @Test
    void displayEvent() throws Exception {
        Long id = 1L;
        EventDto eventDto = firstEventDto();
        when(eventService.getEvent(id)).thenReturn(eventDto);
        when(mapper.eventDtoToEventModel(firstEventDto())).thenReturn(firstEvent());
        
        
        mockMvc.perform(get("/events/detail/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("eventDetails"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attribute("event", mapper.eventDtoToEventModel(eventDto)));
    }
    
    @Test
    @WithMockAdmin
    void displayAddEventForm() throws Exception {
        mockMvc.perform(get("/events/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("addEvent"))
                .andExpect(model().attribute("event", new EventModel()));
    }
    
    @Test
    @WithMockAdmin
    void displayEditEventForm() throws Exception {
        Long id = 1L;
        when(eventService.getEvent(id)).thenReturn(firstEventDto());
        when(mapper.eventDtoToEventModel(firstEventDto())).thenReturn(firstEvent());
        
        mockMvc.perform(get("/events/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("editEvent"))
                .andExpect(model().attribute("event", firstEvent()));
    }
    
    @Test
    void findAllEvents() throws Exception {
        EventDtoList eventDtoList = createEventDtoList();
        when(eventService.findAllEvents(anyInt(), anyInt())).thenReturn(eventDtoList);
        
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("eventList"))
                .andExpect(model().attribute("events", eventDtoList));
    }
    
    @Test
    @WithMockAdmin
    void handleAddEventForm() throws Exception {
        MockMultipartFile image = new MockMultipartFile("eventImage", getImage1AsBytes());
        doNothing().when(eventService).addEvent(firstEventDto());
        
        mockMvc.perform(multipart("/events/new")
                .file(image)
                .flashAttr("event", firstEvent())
        )
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/events"))
                .andExpect(model().hasNoErrors());
    }
    
    @Test
    @WithMockAdmin
    void handleEditEventForm() throws Exception {
        Long id = 1L;
        when(eventService.getEvent(id)).thenReturn(firstEventDto());
        when(mapper.eventDtoToEventModel(firstEventDto())).thenReturn(firstEvent());
        MockMultipartFile image = new MockMultipartFile("eventImage", getImage1AsBytes());
        
        mockMvc.perform(multipart("/events/{id}", id)
                .file(image)
                .flashAttr("event", firstEvent()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/events"))
                .andExpect(model().hasNoErrors());
    }
    
    @Test
    @WithMockAdmin
    void handleEditEventForm_withUnchangedImage() throws Exception {
        Long id = 1L;
        EventModel event = firstEvent();
        MockMultipartFile image = new MockMultipartFile("eventImage", new byte[0]);
        
        when(eventService.getEvent(id)).thenReturn(firstEventDto());
        when(mapper.eventDtoToEventModel(firstEventDto())).thenReturn(firstEvent());
        
        mockMvc.perform(multipart("/events/{id}", id)
                .file(image)
                .flashAttr("event", event))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/events"))
                .andExpect(model().hasNoErrors());
    }
    
    @Test
    @WithMockAdmin
    void showNewModelAndViewInCaseOfErrorsInAddOrEditEvents_withNullImage_whenAddEvent() throws Exception {
        EventModel event = firstEvent();
        event.setName("");
        EventDto eventDto = firstEventDto();
        eventDto.setImage(null);
        MockMultipartFile image = new MockMultipartFile("eventImage", getImage1AsBytes());
        
        mockMvc.perform(multipart("/events/new")
                .file(image)
                .flashAttr("event", event))
                .andExpect(status().isOk())
                .andExpect(view().name("addEvent"))
                .andExpect(model().attributeHasFieldErrors("event", "name"));
    }
    
    @Test
    @WithMockAdmin
    void showNewModelAndViewInCaseOfErrorsInAddOrEditEvents_withNonNullImage_whenEditEvent() throws Exception {
        EventModel event = firstEvent();
        event.setName("");
        EventDto eventFromDb = firstEventDto();
        when(eventService.getEvent(anyLong())).thenReturn(eventFromDb);
        when(mapper.eventDtoToEventModel(eventFromDb)).thenReturn(firstEvent());
        
        MockMultipartFile image = new MockMultipartFile("eventImage", getImage1AsBytes());
        
        mockMvc.perform(multipart("/events/{id}", 1L)
                .file(image)
                .flashAttr("event", event)
                .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(view().name("editEvent"))
                .andExpect(model().attributeHasFieldErrors("event", "name"));
    }
    
    @Test
    @WithMockAdmin
    void deleteEvent() throws Exception {
        Long id = 1L;
        
        doNothing().when(eventService).delete(id);
        
        mockMvc.perform(get("/events/delete/{id}", id).with(csrf()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/events"));
        
        verify(eventService, times(1)).delete(id);
    }
    
    @Test
    @WithMockAdmin
    void search() throws Exception {
        when(eventService.search(anyMap())).thenReturn(createEventDtoList());
        
        mockMvc.perform(get("/events/search").queryParam("location", "genk"))
                .andExpect(status().isOk())
                .andExpect(view().name("eventList"))
                .andExpect(model().attribute("events", Matchers.equalTo(createEventDtoList())));
    }
    
    @Test
    void search_noParams() throws Exception {
        mockMvc.perform(get("/events/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("searchForm"))
                .andExpect(model().attributeDoesNotExist("events"));
    }
}
