package vn.codegym.BE_BookOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "deliveries")
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_delivery")
    private Long idDelivery;
    @Column(name = "name_delivery", nullable = false, unique = true)
    private String nameDelivery;
    @Column(name = "fee_delivery", nullable = false)
    private Double feeDelivery;
    @Column(name = "description_delivery")
    private String descriptionDelivery;
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;
}
