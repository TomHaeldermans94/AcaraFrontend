package be.acara.frontend.model;

import be.acara.frontend.controller.dto.EventDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class EventModelList extends PageImpl<EventModel> {

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
}