-- src/main/resources/db/migration/V2__create_orders_and_reviews.sql
CREATE TABLE orders (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        listing_id BIGINT NOT NULL,
                        buyer_id BIGINT NOT NULL,
                        amount DECIMAL(12,2) NOT NULL,
                        status VARCHAR(30) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                            ON UPDATE CURRENT_TIMESTAMP,
                        CONSTRAINT fk_order_listing FOREIGN KEY (listing_id) REFERENCES listings(id),
                        CONSTRAINT fk_order_buyer FOREIGN KEY (buyer_id) REFERENCES users(id),
                        CONSTRAINT chk_order_amount CHECK (amount >= 0),
                        INDEX idx_order_buyer (buyer_id),
                        INDEX idx_order_listing (listing_id)
);

CREATE TABLE reviews (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         order_id BIGINT NOT NULL UNIQUE,
                         reviewer_id BIGINT NOT NULL,
                         reviewee_id BIGINT NOT NULL,
                         rating INT NOT NULL,
                         comment VARCHAR(1000),
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP,
                         CONSTRAINT uk_review_order UNIQUE (order_id),
                         CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES orders(id),
                         CONSTRAINT fk_review_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id),
                         CONSTRAINT fk_review_reviewee FOREIGN KEY (reviewee_id) REFERENCES users(id),
                         CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5),
                         INDEX idx_review_reviewee (reviewee_id)
);