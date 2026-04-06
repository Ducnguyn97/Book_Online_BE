package vn.codegym.BE_BookOnline.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vn.codegym.BE_BookOnline.dto.request.GenreCreateRequest;
import vn.codegym.BE_BookOnline.dto.response.GenreResponse;
import vn.codegym.BE_BookOnline.model.Genre;
import vn.codegym.BE_BookOnline.repository.GenreRepository;
import vn.codegym.BE_BookOnline.service.GenreService;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<GenreResponse> findAllGenres() {
        List<Genre> genres = genreRepository.findAllGenres();

        return genres.stream()
                .map(genre -> GenreResponse.builder()
                        .id(genre.getId())
                        .name(genre.getNameTypeBook())
                        .build())
                .toList();
    }

    @Override
    public List<GenreResponse> findGenresWithBook(Long bookId) {
        List<Genre> genres = genreRepository.findGenresWithBook(bookId);
        return genres.stream()
                .map(genre -> GenreResponse.builder()
                        .id(genre.getId())
                        .name(genre.getNameTypeBook())
                        .build())
                .toList();
    }


    @Override
    public GenreResponse createGenre(String email, GenreCreateRequest request) {
        String name = request.getName().trim();
        if (genreRepository.existsByNameTypeBookIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Thể loại đã tồn tại: " + name);
        }

        Genre newGenre = Genre.builder()
                .nameTypeBook(name)
                .build();
        Genre saved = genreRepository.save(newGenre);
        log.info("Created genre: {} by {}", saved, email);
        return GenreResponse.builder()
                .id(saved.getId())
                .name(saved.getNameTypeBook())
                .build();
    }

    @Override
    public GenreResponse updateGenre(String email, Long genreId, GenreCreateRequest request) {
        Genre existingGenre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy loại sách với ID: " + genreId));

        String newName = request.getName().trim();
        // If the newName equals existing, nothing to change
        if (!existingGenre.getNameTypeBook().equalsIgnoreCase(newName)) {
            genreRepository.findByNameTypeBookIgnoreCase(newName)
                    .ifPresent(g -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Đã có thể loại khác sử dụng tên này: " + newName);
                    });
            existingGenre.setNameTypeBook(newName);
            genreRepository.save(existingGenre);
            log.info("Updated genre id {} name to '{}' by {}", genreId, newName, email);
        }

        return GenreResponse.builder()
                .id(existingGenre.getId())
                .name(existingGenre.getNameTypeBook())
                .build();
    }

    @Override
    public void deleteGenresByStaffId(String email, Long genreId) {
        Genre existingGenre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy loại sách với ID: " + genreId));
        existingGenre.setIsDeleted(true);
        genreRepository.save(existingGenre);
        log.info("Deleted genre id {} by {}", genreId, email);
    }
}
