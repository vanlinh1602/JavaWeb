package com.aptech.shop;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Streamable;

import com.aptech.common.entity.Customer;
import com.aptech.common.entity.Shop;
import com.aptech.paging.SearchRepository;

public interface ShopRepository extends JpaRepository<Shop, Integer> {
	
	public Shop findByIdAndCustomer(Integer id, Customer customer);

	@Query("SELECT s FROM Shop s WHERE s.customer.id = ?2 "
			+ "AND (s.name LIKE %?1%)")
	public Page<Shop> findAll(String keyword, Integer customerId, Pageable pageable);
	
	@Query("SELECT s FROM Shop s WHERE s.customer.id = ?1")
	public Page<Shop> findAll(Integer customerId, Pageable pageable);

	@Query("SELECT s FROM Shop s WHERE s.name = ?1")
	public Shop findByName(String name);

	public Long countById(Integer id);

	@Query("SELECT s FROM Shop s WHERE s.customer.id = ?1 AND s.enabled = true")
	public List<Shop> listShopByCustomer(Integer customerId );

}
