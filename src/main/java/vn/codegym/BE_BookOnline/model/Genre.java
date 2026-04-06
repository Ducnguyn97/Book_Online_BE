package vn.codegym.BE_BookOnline.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "book_type")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_book")
    private Long id;

    @Column(name = "name_type_book", nullable = false, unique = true)
    private String nameTypeBook;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "typeBooks")
    private List<Book> books;
}