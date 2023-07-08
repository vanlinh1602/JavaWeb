package com.aptech.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.aptech.common.entity.Review;
import com.aptech.common.entity.product.Product;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	
	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1")
	public Page<Review> findByCustomer(Integer customerId, Pageable pageable);
	
	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND ("
			+ "r.headline LIKE %?2% OR r.comment LIKE %?2% OR "
			+ "r.product.name LIKE %?2%)")
	public Page<Review> findByCustomer(Integer customerId, String keyword, Pageable pageable);
	
	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND r.id = ?2")
	public Review findByCustomerAndId(Integer customerId, Integer reviewId);
	
	public Page<Review> findByProduct(Product product, Pageable pageable);
	
	@Query("SELECT COUNT(r.id) FROM Review r WHERE r.customer.id = ?1 AND "
			+ "r.product.id = ?2")
	public Long countByCustomerAndProduct(Integer customerId, Integer productId);
	
	@Query("UPDATE Review r SET r.votes = COALESCE((SELECT SUM(v.votes) FROM ReviewVote v"
			+ " WHERE v.review.id=?1), 0) WHERE r.id = ?1")
	@Modifying
	public void updateVoteCount(Integer reviewId);
	
	@Query("SELECT r.votes FROM Review r WHERE r.id = ?1")
	public Integer getVoteCount(Integer reviewId);

	@Query("SELECT COUNT (r) FROM Review r WHERE r.rating = 5 and r.product.id =?1")
	public int count5Star(Integer productId);
	
	@Query("SELECT COUNT (r) FROM Review r WHERE r.rating = 4 and r.product.id =?1")
	public int count4Star(Integer productId);
	
	@Query("SELECT COUNT (r) FROM Review r WHERE r.rating = 3 and r.product.id =?1")
	public int count3Star(Integer productId);
	
	@Query("SELECT COUNT (r) FROM Review r WHERE r.rating = 2 and r.product.id =?1")
	public int count2Star(Integer productId);
	
	@Query("SELECT COUNT (r) FROM Review r WHERE r.rating = 1 and r.product.id =?1")
	public int count1Star(Integer productId);

}

