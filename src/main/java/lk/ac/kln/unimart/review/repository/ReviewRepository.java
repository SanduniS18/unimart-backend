package lk.ac.kln.unimart.review.repository;

import lk.ac.kln.unimart.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByOrderId(Long orderId);

    @EntityGraph(attributePaths = {"order", "reviewer", "reviewee"})
    Page<Review> findByReviewee_Id(Long revieweeId, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"order", "reviewer", "reviewee"})
    Page<Review> findAll(Pageable pageable);
}