package be.acara.frontend.util;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.controller.dto.EventDtoList;
import be.acara.frontend.model.EventModel;
import be.acara.frontend.model.EventModelList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

public class EventUtil {

    public static EventDto firstEventDto() {
        return EventDto.builder()
                .id(1L)
                .category("MUSIC")
                .description("event description")
                .eventDate(LocalDateTime.now().plusYears(1L).truncatedTo(ChronoUnit.MINUTES))
                .location("location")
                .name("Event name")
                .price(BigDecimal.TEN)
                .image(getImage1AsBytes())
                .attendees(Set.of(UserUtil.secondUserDto(), UserUtil.firstUserDto(), UserUtil.thirdUserDto()))
                .relatedEvents(createEventDtoListWithoutFirstEventDto().getContent())
                .build();
    }

    public static EventModel firstEvent() {
        return EventModel.builder()
                .id(1L)
                .category("MUSIC")
                .description("event description")
                .eventDate(LocalDateTime.now().plusYears(1L).truncatedTo(ChronoUnit.MINUTES))
                .location("location")
                .name("Event name")
                .price(BigDecimal.TEN)
                .image(getImage1AsBytes())
                .build();
    }

    public static EventDto secondEventDto() {
        return EventDto.builder()
                .id(2L)
                .category("THEATRE")
                .description("another event description")
                .eventDate(LocalDateTime.now().plusMonths(6).truncatedTo(ChronoUnit.MINUTES))
                .location("home")
                .name("The name of this event")
                .price(BigDecimal.ONE)
                .image(getImage1AsBytes())
                .attendees(Set.of(UserUtil.secondUserDto()))
                .build();
    }

    public static EventModel secondEvent() {
        return EventModel.builder()
                .id(2L)
                .category("THEATRE")
                .description("another event description")
                .eventDate(LocalDateTime.now().plusMonths(6).truncatedTo(ChronoUnit.MINUTES))
                .location("home")
                .name("The name of this event")
                .price(BigDecimal.ONE)
                .image(getImage1AsBytes())
                .build();
    }

    public static EventDto thirdEventDto() {
        return EventDto.builder()
                .id(3L)
                .category("THEATRE")
                .description("another event description")
                .eventDate(LocalDateTime.now().plusMonths(3).truncatedTo(ChronoUnit.MINUTES))
                .location("home")
                .name("The name of this event")
                .price(BigDecimal.ONE)
                .image(getImage1AsBytes())
                .attendees(Set.of(UserUtil.secondUserDto(), UserUtil.firstUserDto()))
                .build();
    }

    public static EventModel thirdEvent() {
        return EventModel.builder()
                .id(3L)
                .category("THEATRE")
                .description("another event description")
                .eventDate(LocalDateTime.now().plusMonths(3).truncatedTo(ChronoUnit.MINUTES))
                .location("home")
                .name("The name of this event")
                .price(BigDecimal.ONE)
                .image(getImage1AsBytes())
                .build();
    }

    public static List<EventModel> createListOfEventsOfSize3() {
        return List.of(
                firstEvent(),
                secondEvent(),
                thirdEvent()
        );
    }

    public static EventDtoList createEventDtoList() {
        return new EventDtoList(List.of(
                firstEventDto(),
                secondEventDto(),
                thirdEventDto()
        ));
    }

    public static EventDtoList createEventDtoListWithoutFirstEventDto() {
        return new EventDtoList(List.of(
                secondEventDto(),
                thirdEventDto()
        ));
    }

    public static EventModelList createEventModelList() {
        return new EventModelList(List.of(
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
