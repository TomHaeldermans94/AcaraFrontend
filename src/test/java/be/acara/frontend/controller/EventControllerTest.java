package be.acara.frontend.controller;

import be.acara.frontend.model.Event;
import be.acara.frontend.service.EventFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class EventControllerTest {
    
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
    
        this.mockMvc.perform(get(String.format("/event/%d", id)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("event", createEvent()))
                .andExpect(model().attribute("eventImage", compareBase64Image()));
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
    
    private byte[] getImageBytes(String imageLocation) throws Exception {
        File file = new File(imageLocation);
        FileInputStream fis = new FileInputStream(file);
        return fis.readAllBytes();
    }
    
    private String compareBase64Image() throws Exception {
        return Base64.getEncoder().encodeToString(getImageBytes("image_event_1.jpg"));
    }
}
