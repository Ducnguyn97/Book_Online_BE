package vn.codegym.BE_BookOnline.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_image")
    private Long idImage;//manh anh
    @Column(name = "name_image")
    private String nameImage;
    @Column(name = "is_thumbnail")
    private Boolean isThumbnail;
    @Column(name = "url_image")
    private String urlImage;
    @Column(name = "data_image", columnDefinition = "TEXT")
    @Lob
    private String dataImage;//du lieu anh
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "id_book", nullable = false, foreignKey = @ForeignKey(name = "fk_image_book"))
    private Book book;
}
