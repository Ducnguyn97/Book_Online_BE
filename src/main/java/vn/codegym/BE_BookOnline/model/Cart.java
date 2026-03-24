package vn.codegym.BE_BookOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "carts")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cart")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false, unique = true)
    private User user;
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;
    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    //Business methods
    public void clearCart() {
        this.cartItems.clear();
        this.lastUpdated = LocalDateTime.now();
    }
}
