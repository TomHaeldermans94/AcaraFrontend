package be.acara.frontend.controller;

import be.acara.frontend.model.Event;
import be.acara.frontend.model.EventList;
import be.acara.frontend.service.EventFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class EventControllerTestIT {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private EventFeignClient eventFeignClient;
    
    @BeforeEach
    void setUp() {
    }
    
    @Test
    void displayEvent() throws Exception {
        Long id = 1L;
        
        Mockito.when(eventFeignClient.getEventById(id)).thenReturn(createEvent());
    
        this.mockMvc.perform(get(String.format("/events/detail/%d", id)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("event", createEvent()))
                .andExpect(model().attribute("eventImage", compareBase64Image()));
    }

    @Test
    void getAllEvents() throws Exception {

        Mockito.when(eventFeignClient.getEvents()).thenReturn(createEventList());

        this.mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("events", createEventList().getEventList()));
    }

    @Test
    void addEvent() throws Exception {
        Mockito.doNothing().when(eventFeignClient).addEvent(createEvent());
        ResultMatcher view = MockMvcResultMatchers.view().name("addEventForm");
        this.mockMvc.perform(post("/events/new"))
                .andExpect(status().isOk())
                .andExpect(view)
                .andExpect(model().attributeHasFieldErrors("event", "name", "description", "location", "category", "price"));
    }

    private Event createEvent() throws Exception {
        return Event.builder()
                .id(1L)
                .name("concert")
                .location("genk")
                .category("MUSIC")
                .eventDate(LocalDateTime.of(2020,12,20,20,30,54))
                .description("description")
                .price(new BigDecimal("20.0"))
                .image(getImageBytes("image_event_1.jpg"))
                .build();
    }

    private Event createEvent2() throws Exception {
        return Event.builder()
                .id(2L)
                .name("concert")
                .location("genk")
                .category("MUSIC")
                .eventDate(LocalDateTime.of(2020,12,20,20,30,54))
                .description("description")
                .price(new BigDecimal("20.1"))
                .image(getImageBytes("image_event_1.jpg"))
                .build();
    }

    private EventList createEventList() throws Exception {
        List<Event> events = new ArrayList<>();
        events.add(createEvent());
        events.add(createEvent2());
        return new EventList(events);
    }
    
    private byte[] getImageBytes(String imageLocation) throws Exception {
        File file = new File(imageLocation);
        FileInputStream fis = new FileInputStream(file);
        return fis.readAllBytes();
    }
    
    private String compareBase64Image() throws Exception {
        return Base64.getEncoder().encodeToString(getImageBytes("image_event_1.jpg"));
    }
}
