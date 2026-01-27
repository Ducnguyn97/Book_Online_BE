package vn.codegym.BE_BookOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "reviews")
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_review")
    private Long idReview;
    @Column(name = "rating", nullable = false)
    private float rating;
    @Column(name = "comment", length = 2000)
    private String comment;
    @Column(name = "review_date", nullable = false)
    private String reviewDate;
    @Column(name = "image_review")
    private String imageReview;
    @Column(name = "admin_response", length = 2000)
    private String adminResponse;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(
            name = "id_book",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_book"))
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(
            name = "id_user",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_user"))
    private User user;

    @OneToOne( cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            optional = false)
    @JoinColumn(name = "id_order_detail",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_order_detail"))
    private OrderDetail orderDetail;//liên kết với OrderDetail
}
