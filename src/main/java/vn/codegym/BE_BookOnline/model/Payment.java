package vn.codegym.BE_BookOnline.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_payment")
    private Long idPayment;//ma hinh thuc thanh toan
    @Column(name = "name_payment", nullable = false, unique = true)
    private String namePayment;//ten hinh thuc thanh toan
    @Column(name = "description_payment")
    private String descriptionPayment;//mo ta
    @Column(name = "fee_payment", nullable = false)
    private double feePayment;//
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;//ds don hang su dung hinh thuc thanh toan nay
}
