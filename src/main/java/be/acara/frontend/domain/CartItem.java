package be.acara.frontend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CartItem {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Cart cart;
    private Long eventId;
    private int amount;
    private BigDecimal itemTotal = BigDecimal.ZERO;
}
