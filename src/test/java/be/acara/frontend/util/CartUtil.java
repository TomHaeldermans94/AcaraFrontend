package be.acara.frontend.util;

import be.acara.frontend.domain.Cart;
import be.acara.frontend.model.CartModel;

import java.math.BigDecimal;
import java.util.HashSet;

public class CartUtil {
    
    public static Cart cart() {
        return Cart.builder()
                .id(1L)
                .items(new HashSet<>())
                .total(BigDecimal.ONE)
                .user(UserUtil.firstUserDomain())
                .build();
    }
    
    public static CartModel cartModel() {
        CartModel cartModel = new CartModel();
        cartModel.setItems(new HashSet<>());
        cartModel.setTotal(BigDecimal.ONE);
        return cartModel;
    }
}
