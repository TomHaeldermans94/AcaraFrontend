package be.acara.frontend.service.mapper;

import be.acara.frontend.controller.dto.EventDto;
import be.acara.frontend.model.EventModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
@SuppressWarnings("java:S1214") // remove the warning for the INSTANCE variable
public interface EventMapper {
    
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);
    
    EventDto eventModelToEventDto(EventModel eventModel);
    EventModel eventDtoToEventModel(EventDto eventDto);
}
