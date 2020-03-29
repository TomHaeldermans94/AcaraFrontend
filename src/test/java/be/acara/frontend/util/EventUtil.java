package be.acara.frontend.util;

import be.acara.frontend.model.Event;
import be.acara.frontend.model.EventList;
import be.acara.frontend.model.EventWithoutImage;
import be.acara.frontend.service.mapper.EventMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EventUtil {
    
    public static Event firstEvent() {
        return Event.builder()
                .id(1L)
                .category("MUSIC")
                .description("event description")
                .eventDate(LocalDateTime.now().plusYears(1L).truncatedTo(ChronoUnit.MINUTES))
                .location("location")
                .name("event name")
                .price(BigDecimal.TEN)
                .image(getImage1AsBytes())
                .build();
    }
    
    public static Event secondEvent() {
        return Event.builder()
                .id(2L)
                .category("THEATRE")
                .description("another event description")
                .eventDate(LocalDateTime.now().plusMonths(6).truncatedTo(ChronoUnit.MINUTES))
                .location("home")
                .name("the name of this event")
                .price(BigDecimal.ONE)
                .image(getImage1AsBytes())
                .build();
    }
    
    public static Event thirdEvent() {
        return Event.builder()
                .id(3L)
                .category("THEATRE")
                .description("another event description")
                .eventDate(LocalDateTime.now().plusMonths(3).truncatedTo(ChronoUnit.MINUTES))
                .location("home")
                .name("the name of this event")
                .price(BigDecimal.ONE)
                .image(getImage1AsBytes())
                .build();
    }
    
    public static EventWithoutImage map(Event event) {
        return new EventMapper().map(event);
    }
    
    public static EventList createEventList() {
        return new EventList(List.of(
                firstEvent(),
                secondEvent(),
                thirdEvent()
        ));
    }
    
    public static byte[] getImage1AsBytes() {
        try {
            File file = new File("image_event_1.jpg");
            FileInputStream fis = new FileInputStream(file);
            return fis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
