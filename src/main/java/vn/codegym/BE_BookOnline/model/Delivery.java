package vn.codegym.BE_BookOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private String nameDelivery;;
    @Column(name = "ghtk_service_name")
    private String ghtkServiceName;            // Tên dịch vụ GHTK để gọi API (VD: "road", "fly")
    @Column(name = "description_delivery")
    private String descriptionDelivery;
    @Column(name = "active", nullable = false)
    private boolean active = true;
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;
}
