package com.aptech.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aptech.common.entity.Shop;
import com.aptech.common.entity.product.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

	@Query("SELECT p FROM Product p WHERE p.enabled = true "
			+ "AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%)"
			+ " ORDER BY p.name ASC")
	public Page<Product> listByCategory(Integer categoryId, String categoryIDMatch, Pageable pageable);
	
	public Product findByAlias(String alias);
	
	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1% " 			
			+ "OR p.shop.name LIKE %?1% ")
	public Page<Product> search(String keyword, Pageable pageable);
	
	@Query("Update Product p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1), 0),"
			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.product.id =?1) "
			+ "WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer productId);
	
	@Query("SELECT p FROM Product p WHERE p.enabled=true AND p.brand.id=?1")
	public Page<Product> listByBrand(Integer brandId, Pageable pageable);	
	
	@Query(nativeQuery = true,
            value = "SELECT * FROM emultivendor.products WHERE enabled= true ORDER BY id desc LIMIT 9")
	public List<Product> findAll();
	
	@Query(nativeQuery = true,
            value = "SELECT * FROM emultivendor.products WHERE enabled= true AND discount_percent >= 50 ORDER BY id desc LIMIT 5")
	public List<Product> findProductBestSeller();
	
	@Query(nativeQuery = true,
            value = "SELECT * FROM emultivendor.products WHERE enabled= true AND discount_percent = 25 ORDER BY id desc LIMIT 5")
	public List<Product> findFlashDeal();
	
	public Product findByName(String name);
	
	@Query("UPDATE Product p SET p.enabled = ?2 WHERE p.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	public Long countById(Integer id);
	
	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1% " 
			+ "OR p.shortDescription LIKE %?1% "
			+ "OR p.fullDescription LIKE %?1% "
			+ "OR p.brand.name LIKE %?1% "
			+ "OR p.shop.name LIKE %?1% "
			+ "OR p.category.name LIKE %?1%")
	public Page<Product> findAll(String keyword, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.category.id = ?1 "
			+ "OR p.category.allParentIDs LIKE %?2%")	
	public Page<Product> findAllInCategory(Integer categoryId, String categoryIdMatch, 
			Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE (p.category.id = ?1 "
			+ "OR p.category.allParentIDs LIKE %?2%) AND "
			+ "(p.name LIKE %?3% " 
			+ "OR p.shortDescription LIKE %?3% "
			+ "OR p.fullDescription LIKE %?3% "
			+ "OR p.brand.name LIKE %?3% "
			+ "OR p.shop.name LIKE %?3% "
			+ "OR p.category.name LIKE %?3%)")			
	public Page<Product> searchInCategory(Integer categoryId, String categoryIdMatch, 
			String keyword, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
	public Page<Product> searchProductsByName(String keyword, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.customer.id = ?2 "
			+ "AND (p.name LIKE %?1%)")
	public Page<Product> findAll(String keyword, Integer customerId, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.customer.id = ?1")
	public Page<Product> findAll(Integer customerId, Pageable pageable);
}
