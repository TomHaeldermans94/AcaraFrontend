package be.acara.frontend.service.mapper;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.model.EventModel;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public EventModel map(EventDto eventDto) {
        EventModel event = EventModel.builder()
                .category(eventDto.getCategory())
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .id(eventDto.getId())
                .location(eventDto.getLocation())
                .image(new byte[0])
                .name(eventDto.getName())
                .price(eventDto.getPrice())
                .build();
        if (eventDto.getImage() != null) {
            event.setImage(eventDto.getImage());
        }
        return event;
    }

    public EventDto map(EventModel event) {
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
