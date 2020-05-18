package be.acara.frontend.service.mapper;

import be.acara.frontend.controller.dto.CreateOrderDto;
import be.acara.frontend.model.CreateOrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
@SuppressWarnings("java:S1214") // remove the warning for the INSTANCE variable
public interface OrderMapper {
    
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    
    @Mapping(target = "eventId", source = "createOrderModel.eventModel.id")
    CreateOrderDto createOrderModelToCreateOrderDto(CreateOrderModel createOrderModel);
}
