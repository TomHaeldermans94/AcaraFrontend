package be.acara.frontend.service.mapper;

import be.acara.frontend.domain.CartItem;
import be.acara.frontend.model.CartItemModel;
import be.acara.frontend.service.EventService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {EventService.class})
@SuppressWarnings("java:S1214") // remove the warning for the INSTANCE variable
public abstract class CartItemMapper {
    
    public static final  CartItemMapper INSTANCE = Mappers.getMapper(CartItemMapper.class);
    
    @Mapping(source = "cartItem.eventId", target = "eventDto")
    public abstract CartItemModel cartItemToCartItemModel(CartItem cartItem);
    @Mapping(source = "cartItemModel.eventDto.id", target = "eventId")
    public abstract CartItem cartItemModelToCartItem(CartItemModel cartItemModel);
}
