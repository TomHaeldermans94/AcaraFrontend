package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.CategoriesList;
import be.acara.frontend.model.Event;
import be.acara.frontend.model.EventList;
import be.acara.frontend.model.EventWithoutImage;
import be.acara.frontend.service.EventFeignClient;
import be.acara.frontend.service.mapper.EventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static be.acara.frontend.util.EventUtil.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    
    @Mock
    private EventFeignClient eventFeignClient;
    @Mock
    private EventMapper mapper;
    @InjectMocks
    private EventController eventController;
    
    private MockMvc mockMvc;
    private CategoriesList categoriesList;
    
    
    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
        categoriesList = new CategoriesList(List.of("MUSIC", "THEATRE", "UNKNOWN"));
        when(eventFeignClient.getAllCategories()).thenReturn(categoriesList);
    }
    
    @Test
    void displayEvent() throws Exception {
        Long id = 1L;
        Event event = firstEvent();
        when(eventFeignClient.getEventById(id)).thenReturn(event);
        
        
        mockMvc.perform(get("/events/detail/{id}",id))
                .andExpect(status().isOk())
                .andExpect(view().name("eventDetails"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attribute("event", event))
                .andExpect(model().attribute("categoryList", categoriesList.getCategories()));
    }
    
    @Test
    void displayAddEventForm() throws Exception {
        mockMvc.perform(get("/events/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("addEvent"))
                .andExpect(model().attribute("event", new Event()));
    }
    
    @Test
    void displayEditEventForm() throws Exception {
        Long id = 1L;
        when(eventFeignClient.getEventById(id)).thenReturn(firstEvent());
        
        mockMvc.perform(get("/events/{id}", id))
                .andExpect(status().isOk())
                .andExpect(view().name("editEvent"))
                .andExpect(model().attribute("event", firstEvent()));
    }
    
    @Test
    void findAllEvents() throws Exception {
        EventList eventList = createEventList();
        when(eventFeignClient.getEvents()).thenReturn(eventList);
        
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("eventList"))
                .andExpect(model().attribute("events", eventList.getEventList()));
    }
    
    @Test
    void handleAddEventForm() throws Exception {
        doNothing().when(eventFeignClient).addEvent(any());
        when(mapper.mapEventWithoutImageToEventWithMultipartImage(any(),any())).thenReturn(firstEvent());
        
        MockMultipartFile image = new MockMultipartFile("image", getImage1AsBytes());
        
        mockMvc.perform(multipart("/events/new")
                .file(image)
                .flashAttr("event", map(firstEvent()))
        )
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/events"))
                .andExpect(model().hasNoErrors());
    }
    
    @Test
    void handleEditEventForm() throws Exception{
        Long id = 1L;
        doNothing().when(eventFeignClient).editEvent(anyLong(),any());
        MockMultipartFile image = new MockMultipartFile("image", getImage1AsBytes());
        when(mapper.mapEventWithoutImageToEventWithMultipartImage(any(),any())).thenReturn(firstEvent());
        
        mockMvc.perform(multipart("/events/{id}", id)
                .file(image)
                .flashAttr("event", map(firstEvent())))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/events"))
                .andExpect(model().hasNoErrors());
    }
    
    @Test
    void handleEditEventForm_withUnchangedImage() throws Exception{
        Long id = 1L;
        EventWithoutImage eventWithoutImage = map(firstEvent());
        Event eventFromDb = firstEvent();
        MockMultipartFile image = new MockMultipartFile("image",new byte[0]);
        
        when(eventFeignClient.getEventById(id)).thenReturn(firstEvent());
        when(mapper.mapEventWithoutImageToEventWithUnchangedImage(eventWithoutImage, eventFromDb.getImage())).thenReturn(eventFromDb);
        
        mockMvc.perform(multipart("/events/{id}", id)
                .file(image)
                .flashAttr("event", eventWithoutImage))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/events"))
                .andExpect(model().hasNoErrors());
    }
    
    @Test
    void showNewModelAndViewInCaseOfErrorsInAddOrEditEvents_withNullImage_whenAddEvent() throws Exception {
        EventWithoutImage event = map(firstEvent());
        event.setName("");
        Event eventFromDb = firstEvent();
        eventFromDb.setImage(null);
        when(eventFeignClient.getEventById(anyLong())).thenReturn(eventFromDb);
    
        MockMultipartFile image = new MockMultipartFile("image", getImage1AsBytes());
    
        mockMvc.perform(multipart("/events/new")
                .file(image)
                .flashAttr("event", event)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("addEvent"))
                .andExpect(model().attributeHasFieldErrors("event","name"));
    }
    
    @Test
    void showNewModelAndViewInCaseOfErrorsInAddOrEditEvents_withNonNullImage_whenEditEvent() throws Exception {
        EventWithoutImage event = map(firstEvent());
        event.setName("");
        Event eventFromDb = firstEvent();
        when(eventFeignClient.getEventById(anyLong())).thenReturn(eventFromDb);
        
        MockMultipartFile image = new MockMultipartFile("image", getImage1AsBytes());
        
        mockMvc.perform(multipart("/events/{id}", 1L)
                .file(image)
                .flashAttr("event", event)
        )
                .andExpect(status().isOk())
                .andExpect(view().name("editEvent"))
                .andExpect(model().attributeHasFieldErrors("event","name"));
    }
    
    @Test
    void deleteEvent() throws Exception {
        Long id = 1L;
        
        doNothing().when(eventFeignClient).deleteEvent(id);
        
        mockMvc.perform(get("/events/delete/{id}", id))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/events"));
        
        verify(eventFeignClient, times(1)).deleteEvent(id);
    }
    
    @Test
    void search() throws Exception {
        when(eventFeignClient.search(anyMap())).thenReturn(createEventList());
        
        mockMvc.perform(get("/events/search").queryParam("location","genk"))
                .andExpect(status().isOk())
                .andExpect(view().name("eventList"))
                .andExpect(model().attribute("events", hasSize(greaterThan(0))));
    }
    
    @Test
    void search_noParams() throws Exception {
        mockMvc.perform(get("/events/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("searchForm"))
                .andExpect(model().attributeDoesNotExist("events"));
    }
}
