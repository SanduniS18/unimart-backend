package lk.ac.kln.unimart.listing.service;

import lk.ac.kln.unimart.auth.entity.User;
import lk.ac.kln.unimart.auth.repository.UserRepository;
import lk.ac.kln.unimart.category.entity.Category;
import lk.ac.kln.unimart.category.repository.CategoryRepository;
import lk.ac.kln.unimart.common.exception.ConflictException;
import lk.ac.kln.unimart.common.exception.ForbiddenException;
import lk.ac.kln.unimart.common.exception.ResourceNotFoundException;
import lk.ac.kln.unimart.listing.dto.ListingRequest;
import lk.ac.kln.unimart.listing.dto.ListingResponse;
import lk.ac.kln.unimart.listing.entity.Listing;
import lk.ac.kln.unimart.listing.entity.ListingStatus;
import lk.ac.kln.unimart.listing.mapper.ListingMapper;
import lk.ac.kln.unimart.listing.repository.ListingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ListingService {

    private final ListingRepository listings;
    private final CategoryRepository categories;
    private final UserRepository users;
    private final ListingMapper mapper;

    public ListingService(ListingRepository listings, CategoryRepository categories,
                          UserRepository users, ListingMapper mapper) {
        this.listings = listings;
        this.categories = categories;
        this.users = users;
        this.mapper = mapper;
    }

    @Transactional
    public ListingResponse create(ListingRequest request, String email) {
        User seller = users.findByUniversityEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Category category = categories.findByIdAndActiveTrue(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Listing listing = new Listing();
        listing.setSeller(seller);
        listing.setCategory(category);
        listing.setTitle(request.title().trim());
        listing.setDescription(request.description().trim());
        listing.setPrice(request.price());
        listing.setStatus(ListingStatus.AVAILABLE);
        listing.setCreatedAt(Instant.now());
        listing.setUpdatedAt(Instant.now());

        return mapper.toResponse(listings.save(listing));
    }

    @Transactional(readOnly = true)
    public ListingResponse get(Long id) {
        Listing listing = listings.findByIdAndStatusNot(id, ListingStatus.ARCHIVED)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
        return mapper.toResponse(listing);
    }

    @Transactional
    public ListingResponse update(Long id, ListingRequest request, String email) {
        Listing listing = requireOwnedListing(id, email);
        Category category = categories.findByIdAndActiveTrue(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        listing.setTitle(request.title().trim());
        listing.setDescription(request.description().trim());
        listing.setPrice(request.price());
        listing.setCategory(category);
        listing.setUpdatedAt(Instant.now());

        return mapper.toResponse(listing);
    }

    @Transactional
    public void archive(Long id, String email) {
        Listing listing = requireOwnedListing(id, email);
        if (listing.getStatus() == ListingStatus.SOLD) {
            throw new ConflictException("Sold listings cannot be deleted");
        }
        listing.setStatus(ListingStatus.ARCHIVED);
    }

    private Listing requireOwnedListing(Long id, String email) {
        Listing listing = listings.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
        if (!listing.getSeller().getUniversityEmail().equalsIgnoreCase(email)) {
            throw new ForbiddenException("You do not own this listing");
        }
        return listing;
    }
}