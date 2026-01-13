package vn.codegym.BE_BookOnline.service;

import org.springframework.data.domain.Page;
import vn.codegym.BE_BookOnline.dto.request.ReviewRequest;
import vn.codegym.BE_BookOnline.dto.response.RatingStaticticsResponse;
import vn.codegym.BE_BookOnline.dto.response.ReviewResponse;

public interface ReviewService {
    // ===== WRITE REVIEW =====
    ReviewResponse writeReview(ReviewRequest request, String email);

    ReviewResponse editReview(Long reviewId, ReviewRequest request, String email);

    void deleteReview(Long reviewId, String email);

    Page<ReviewResponse> getReviewsByBookId(Long bookId,String sortBy, int page, int size);

    Page<ReviewResponse> getReviewsByUserEmail(String email, String sortBy, int page, int size);

    RatingStaticticsResponse getRatingStatisticsByBookId(Long bookId);

    void updateBookRatingStatistics(Long bookId);

    ReviewResponse rejectReview(Long adminId, Long reviewId, String reason);

    Page<ReviewResponse> getPendingReviews(int page, int size);

    void reportReview(Long userId, Long reviewId, String reason);

    Page<ReviewResponse> getReviewsWithImages(Long bookId, int page, int size);



}
