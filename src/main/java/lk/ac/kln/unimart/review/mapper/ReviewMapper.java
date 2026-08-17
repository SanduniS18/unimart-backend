package lk.ac.kln.unimart.review.mapper;

import lk.ac.kln.unimart.review.dto.ReviewResponse;
import lk.ac.kln.unimart.review.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getOrder().getId(),
                review.getReviewer().getId(),
                review.getReviewer().getFullName(), // adjust getter name to match your User entity
                review.getReviewee().getId(),
                review.getReviewee().getFullName(), // adjust getter name to match your User entity
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}