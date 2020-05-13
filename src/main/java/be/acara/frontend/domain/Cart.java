package be.acara.frontend.domain;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    private User user;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Set<CartItem> items = new HashSet<>();
    
    private BigDecimal total = BigDecimal.ZERO;
    
    public Cart(User user) {
        this(null, user, new HashSet<>(), BigDecimal.ZERO);
    }
}
