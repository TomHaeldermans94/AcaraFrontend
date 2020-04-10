package be.acara.frontend.service.mapper;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public Event map(EventDto eventDto) {
        Event event = Event.builder()
                .category(eventDto.getCategory())
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .id(eventDto.getId())
                .location(eventDto.getLocation())
                .name(eventDto.getName())
                .price(eventDto.getPrice())
                .build();
        if (eventDto.getImage() != null) {
            event.setImage(eventDto.getImage());
        }
        return event;
    }

    public EventDto map(Event event) {
        EventDto eventDto = EventDto.builder()
                .category(event.getCategory())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .location(event.getLocation())
                .name(event.getName())
                .price(event.getPrice())
                .build();
        if (event.getImage() != null) {
            eventDto.setImage(event.getImage());
        }
        return eventDto;
    }
}
