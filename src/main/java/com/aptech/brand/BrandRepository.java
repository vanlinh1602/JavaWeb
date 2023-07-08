package com.aptech.brand;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.aptech.common.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
	
	@Query("SELECT NEW Brand(b.id, b.name) FROM Brand b ORDER BY b.name ASC")
	public List<Brand> findAll();

}
