package lk.ac.kln.unimart.review.service;

import lk.ac.kln.unimart.common.exception.ConflictException;
import lk.ac.kln.unimart.common.exception.ForbiddenException;
import lk.ac.kln.unimart.common.exception.ResourceNotFoundException;
import lk.ac.kln.unimart.listing.entity.Listing;
import lk.ac.kln.unimart.listing.repository.ListingRepository;
import lk.ac.kln.unimart.order.entity.Order;
import lk.ac.kln.unimart.order.entity.OrderStatus;
import lk.ac.kln.unimart.order.repository.OrderRepository;
import lk.ac.kln.unimart.review.dto.ReviewCreateRequest;
import lk.ac.kln.unimart.review.dto.ReviewResponse;
import lk.ac.kln.unimart.review.dto.ReviewUpdateRequest;
import lk.ac.kln.unimart.review.entity.Review;
import lk.ac.kln.unimart.review.mapper.ReviewMapper;
import lk.ac.kln.unimart.review.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ReviewService {

    private final ReviewRepository reviews;
    private final OrderRepository orders;
    private final ListingRepository listings;
    private final ReviewMapper mapper;

    public ReviewService(ReviewRepository reviews, OrderRepository orders, ListingRepository listings, ReviewMapper mapper) {
        this.reviews = reviews;
        this.orders = orders;
        this.listings = listings;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByListing(Long listingId, int page, int size) {
        Listing listing = listings.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reviews.findByReviewee_Id(listing.getSeller().getId(), pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsBySeller(Long sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reviews.findByReviewee_Id(sellerId, pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getAllReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reviews.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public ReviewResponse create(ReviewCreateRequest request, String email) {
        Order order = orders.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new ConflictException("Only completed orders can be reviewed");
        }
        if (!order.getBuyer().getUniversityEmail().equalsIgnoreCase(email)) {
            throw new ForbiddenException("Only the buyer can review this order");
        }
        if (reviews.existsByOrderId(order.getId())) {
            throw new ConflictException("This order already has a review");
        }

        Review review = new Review();
        review.setOrder(order);
        review.setReviewer(order.getBuyer());
        review.setReviewee(order.getListing().getSeller());
        review.setRating(request.rating());
        review.setComment(normalize(request.comment()));
        review.setCreatedAt(Instant.now());
        review.setUpdatedAt(Instant.now());

        return mapper.toResponse(reviews.save(review));
    }

    @Transactional
    public ReviewResponse update(Long id, ReviewUpdateRequest request, String email) {
        Review review = reviews.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getReviewer().getUniversityEmail().equalsIgnoreCase(email)) {
            throw new ForbiddenException("Only the review author can update this review");
        }

        review.setRating(request.rating());
        review.setComment(normalize(request.comment()));
        review.setUpdatedAt(Instant.now());

        return mapper.toResponse(review);
    }

    @Transactional
    public void delete(Long id, String email) {
        Review review = reviews.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getReviewer().getUniversityEmail().equalsIgnoreCase(email)) {
            throw new ForbiddenException("Only the review author can delete this review");
        }

        reviews.delete(review);
    }

    private String normalize(String comment) {
        return comment == null ? null : comment.trim();
    }
}