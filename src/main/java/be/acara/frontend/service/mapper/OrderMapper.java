package be.acara.frontend.service.mapper;

import be.acara.frontend.controller.dto.CreateOrderDto;
import be.acara.frontend.model.CreateOrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@SuppressWarnings("java:S1214") // remove the warning for the INSTANCE variable
public interface OrderMapper {
    
    @Mapping(target = "eventId", source = "createOrderModel.eventModel.id")
    CreateOrderDto createOrderModelToCreateOrderDto(CreateOrderModel createOrderModel);
}
