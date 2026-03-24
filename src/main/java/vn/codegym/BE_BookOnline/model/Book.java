package vn.codegym.BE_BookOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import vn.codegym.BE_BookOnline.converter.StringListConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
@SQLDelete(sql = "UPDATE books SET is_deleted = true WHERE id_book = ?")
@SQLRestriction("is_deleted = false")
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_book")
    private Long id;

    @Column(name = "name_book", nullable = false)
    private String nameBook;

    @Column(name = "author_book", nullable = false)
    private String authorBook;//tac gia

    @Column(name =  "ISBN_book", nullable = false, unique = true)
    private String isbnBook;//ma sach quoc te

    @Column(name = "description_book")
    private String descriptionBook;

    @Column(name = "publisher_book", nullable = false)
    private String publisherBook;//nha xuat ban

    @Column(name = "price_book", nullable = false)
    private BigDecimal priceBook;//gia niem yet

    @Column(name = "discount_book", nullable = false)
    private Double discountBook;//giam gia

    @Column(name = "quantity_book", nullable = false)
    private Integer quantityBook;//so luong sach

    @Column(name = "average_rating", nullable = false)
    private Double averageRating;//diem danh gia trung binh

    @Column(name ="sold_quantity_book", nullable = false)
    private Integer soldQuantityBook;//so luong da ban

    @Column(name = "discount_percent_book", nullable = false)
    private Double discountPercentBook;//phan tram giam

    @Column(name = "date_created")
    @CreatedDate
    private LocalDateTime dateCreated;

    @LastModifiedBy
    private String lastModifiedBy;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false; // Mặc định là false (chưa xóa)

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(
            name = "book_type_book",
            joinColumns = @JoinColumn(name = "id_book"),
            inverseJoinColumns = @JoinColumn(name = "id_type_book"))
    private List<Genre> typeBooks;//loai sach

    @Column(name = "image_urls", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> imageUrls;//anh sach (JSON array of URLs)

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;//chi tiet gio hang

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviewBook;//danh gia sach

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;//chi tiet don hang

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Wishlist> wishlists;//danh sach yeu thich


}
