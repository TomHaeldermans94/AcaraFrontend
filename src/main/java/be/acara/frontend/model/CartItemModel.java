package be.acara.frontend.model;

import be.acara.frontend.controller.dto.EventDto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemModel {
    private EventDto eventDto;
    private int amount;
    private BigDecimal itemTotal = BigDecimal.ZERO;
}
