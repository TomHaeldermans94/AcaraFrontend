package be.acara.frontend.controller;

import be.acara.frontend.controller.dto.CategoriesList;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.nullable;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class EventControllerIntegrationTest {

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
        Mockito.when(eventFeignClient.getAllCategories()).thenReturn(createCategoriesList());

        this.mockMvc.perform(get(String.format("/events/detail/%d", id)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("event", createEvent()))
                .andExpect(model().attribute("eventImage", compareBase64Image()));
    }


    @Test
    void getAllEvents() throws Exception {

        Mockito.when(eventFeignClient.getEvents()).thenReturn(createEventList());
        Mockito.when(eventFeignClient.getAllCategories()).thenReturn(createCategoriesList());

        this.mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("events", createEventList().getEventList()));
    }

    @Test
    void addEvent() throws Exception {
        Mockito.doNothing().when(eventFeignClient).addEvent(createEvent());
        Mockito.when(eventFeignClient.getAllCategories()).thenReturn(createCategoriesList());
        Mockito.when(eventFeignClient.getEventById(nullable(Long.class))).thenReturn(createEvent());
        ResultMatcher view = MockMvcResultMatchers.view().name("addEvent");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", getImageBytes("image_event_1.jpg"));
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/events/new").file(mockMultipartFile))
                .andExpect(status().isOk())
                .andExpect(view)
                .andExpect(model().attributeHasFieldErrors("event", "name", "description", "location", "category", "price"));
    }

    @Test
    void editEvent() throws Exception {
        Mockito.doNothing().when(eventFeignClient).editEvent(createEvent().getId(), createEvent());
        Mockito.when(eventFeignClient.getAllCategories()).thenReturn(createCategoriesList());
        Mockito.when(eventFeignClient.getEventById(nullable(Long.class))).thenReturn(createEvent());
        ResultMatcher view = MockMvcResultMatchers.view().name("editEvent");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", getImageBytes("image_event_1.jpg"));
        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/events/1").file(mockMultipartFile))
                .andExpect(status().isOk())
                .andExpect(view)
                .andExpect(model().attributeHasFieldErrors("event", "name", "description", "location", "category", "price"));
    }

    @Test
    void deleteEvent() throws Exception {
        Long id = 1L;
        Mockito.doNothing().when(eventFeignClient).deleteEvent(createEvent().getId());
        Mockito.when(eventFeignClient.getAllCategories()).thenReturn(createCategoriesList());
        Mockito.when(eventFeignClient.getEvents()).thenReturn(createEventListAfterDelete());
        ResultMatcher view = MockMvcResultMatchers.view().name("redirect:/events");
        this.mockMvc.perform(get(String.format("/events/delete/%d", id)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view);
        this.mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("events", createEventListAfterDelete().getEventList()));
    }

    @Test
    void search_noParams() throws Exception {
        Mockito.when(eventFeignClient.search(Map.of("location", "genk"))).thenReturn(new EventList());
        Mockito.when(eventFeignClient.getAllCategories()).thenReturn(createCategoriesList());

        ResultMatcher view = MockMvcResultMatchers.view().name("searchForm");
        this.mockMvc.perform(get("/events/search"))
                .andExpect(status().isOk())
                .andExpect(view);
    }

    @Test
    void search_withParams() throws Exception {
        Mockito.when(eventFeignClient.search(Map.of("location", "genk"))).thenReturn(createEventList());
        Mockito.when(eventFeignClient.getAllCategories()).thenReturn(createCategoriesList());

        ResultMatcher view = MockMvcResultMatchers.view().name("eventList");
        this.mockMvc.perform(get("/events/search").param("location", "genk"))
                .andExpect(status().isOk())
                .andExpect(view)
                .andExpect(model().attribute("events", createEventList().getEventList()));
    }

    private CategoriesList createCategoriesList() throws Exception {
        List<String> categories = new ArrayList<>();
        categories.add("Music");
        categories.add("Theatre");
        return new CategoriesList(categories);
    }

    private Event createEvent() throws Exception {
        return Event.builder()
                .id(1L)
                .name("concert")
                .location("genk")
                .category("MUSIC")
                .eventDate(LocalDateTime.of(2020, 12, 20, 20, 30, 54))
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
                .eventDate(LocalDateTime.of(2020, 12, 20, 20, 30, 54))
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

    private EventList createEventListAfterDelete() throws Exception {
        List<Event> events = new ArrayList<>();
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
