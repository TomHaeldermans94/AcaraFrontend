package be.acara.frontend.service.mapper;

import be.acara.frontend.domain.Cart;
import be.acara.frontend.model.CartModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
@SuppressWarnings("java:S1214") // remove the warning for the INSTANCE variable
public interface CartMapper {
    
    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);
    
    CartModel cartToCartModel(Cart cart);
    Cart cartModelToCart(CartModel cartModel);
}
