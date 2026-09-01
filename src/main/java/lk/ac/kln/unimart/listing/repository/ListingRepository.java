package lk.ac.kln.unimart.listing.repository;

import lk.ac.kln.unimart.listing.entity.Listing;
import lk.ac.kln.unimart.listing.entity.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface ListingRepository extends JpaRepository<Listing, Long>,
        JpaSpecificationExecutor<Listing> {

    @Override
    @EntityGraph(attributePaths = {"seller", "category"})
    Page<Listing> findAll(@Nullable Specification<Listing> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"seller", "category"})
    Optional<Listing> findByIdAndStatusNot(Long id, ListingStatus status);

    @Override
    @EntityGraph(attributePaths = {"seller", "category"})
    Optional<Listing> findById(Long id);
}