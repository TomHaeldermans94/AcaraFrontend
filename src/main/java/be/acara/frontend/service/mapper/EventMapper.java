package be.acara.frontend.service.mapper;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.controller.dto.EventDtoList;
import be.acara.frontend.model.EventModel;
import be.acara.frontend.model.EventModelList;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@SuppressWarnings("java:S1214") // remove the warning for the INSTANCE variable
public interface EventMapper {
    
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);
    
    EventDto eventModelToEventDto(EventModel eventModel);
    EventModel eventDtoToEventModel(EventDto eventDto);
    default EventModelList eventDtoListToEventModelList(EventDtoList eventDtoList) {
        List<EventModel> content = eventDtoList.getContent().stream().map(this::eventDtoToEventModel).collect(Collectors.toList());
        return new EventModelList(content, eventDtoList.getPageable(), eventDtoList.getTotalElements());
    }
}
