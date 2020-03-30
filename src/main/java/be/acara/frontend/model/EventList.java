package be.acara.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EventList {
    private List<Event> eventList;
}
