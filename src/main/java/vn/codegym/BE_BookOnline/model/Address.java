package vn.codegym.BE_BookOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_address")
    private Long idAddress;

    @Column(name = "contact_name", nullable = false)
    private String contactName;

    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "province_id")
    private Integer provinceId; // ID Tỉnh/Thành từ GHN

    @Column(name = "district_id")
    private Integer districtId; // ID Quận/Huyện từ GHN

    @Column(name = "ward_code")// Mã Phường/Xã từ GHN
    private String wardCode;

    @Column(name = "building")
    private String building;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "id_user", nullable = false, foreignKey = @ForeignKey(name = "fk_address_user"))
    private User user;
   // không nên tạo quan hệ với Order vì một địa chỉ có thể được sử dụng cho nhiều đơn hàng khác nhau
}
