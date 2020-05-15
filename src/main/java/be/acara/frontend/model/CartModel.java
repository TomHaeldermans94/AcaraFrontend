package be.acara.frontend.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class CartModel {
    private Set<CartItemModel> items;
    private BigDecimal total = BigDecimal.ZERO;
}
