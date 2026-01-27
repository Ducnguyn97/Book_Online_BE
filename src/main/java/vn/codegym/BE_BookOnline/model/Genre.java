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
@Table(name = "book_type")
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_book")
    private Long id;
    @Column(name = "name_type_book", nullable = false, unique = true)
    private String nameTypeBook;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "typeBooks")
    private List<Book> books;
}
