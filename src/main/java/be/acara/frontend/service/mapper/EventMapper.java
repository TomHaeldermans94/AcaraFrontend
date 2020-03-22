package be.acara.frontend.service.mapper;

import be.acara.frontend.model.Event;
import be.acara.frontend.model.EventWithoutImage;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class EventMapper {
    private Log logger = LogFactory.getLog(this.getClass());

    public Event map(EventWithoutImage eventWithoutImage) {
        return Event.builder()
                .category(eventWithoutImage.getCategory())
                .description(eventWithoutImage.getDescription())
                .eventDate(eventWithoutImage.getEventDate())
                .location(eventWithoutImage.getLocation())
                .name(eventWithoutImage.getName())
                .price(eventWithoutImage.getPrice())
                .id(eventWithoutImage.getId())
                .build();
    }

    public EventWithoutImage map(Event event) {
        return EventWithoutImage.builder()
                .category(event.getCategory())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .name(event.getName())
                .price(event.getPrice())
                .id(event.getId())
                .build();
    }

    public Event mapEventWithoutImageToEventWithMultipartImage(EventWithoutImage eventWithoutImage, MultipartFile image) {
        Event event = map(eventWithoutImage);
        if (image != null) {
            try {
                event.setImage(image.getBytes());
            } catch (IOException e) {
                logger.error("Something went wrong when setting the image to the event");
            }
        }
        return event;
    }

    public Event mapEventWithoutImageToEventWithUnchangedImage(EventWithoutImage eventWithoutImage, byte[] image) {
        Event event = map(eventWithoutImage);
        if (image != null) {
            event.setImage(image);
        }
        return event;
    }
}
