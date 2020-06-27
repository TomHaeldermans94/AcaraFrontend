package be.acara.frontend.model;

import be.acara.frontend.controller.dto.EventDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
public class EventModelList extends PageImpl<EventModel> {
    
    private List<EventDto> popularEvents;
    
    private List<EventDto> nextAttendingEvents;
    
    
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public EventModelList(@JsonProperty("content") List<EventModel> content,
                          @JsonProperty("number") int page,
                          @JsonProperty("size") int size,
                          @JsonProperty("totalElements") long total,
                          @JsonProperty("pageable") JsonNode pageable,
                          @JsonProperty("last") boolean last,
                          @JsonProperty("totalPages") int totalPages,
                          @JsonProperty("sort") JsonNode sort,
                          @JsonProperty("first") boolean first,
                          @JsonProperty("numberOfElements") int numberOfElements,
                          @JsonProperty("empty") boolean empty) {
        super(content, PageRequest.of(page, size), total);
    }
    
    public EventModelList(List<EventModel> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
    
    public EventModelList(List<EventModel> content) {
        super(content);
    }
    
    public List<EventDto> getPopularEvents() {
        return popularEvents;
    }
    
    public List<EventDto> getNextAttendingEvents() {
        return nextAttendingEvents;
    }
    
    public void setPopularEvents(List<EventDto> popularEvents) {
        this.popularEvents = popularEvents;
    }
    
    public void setNextAttendingEvents(List<EventDto> nextAttendingEvents) {
        this.nextAttendingEvents = nextAttendingEvents;
    }


}
