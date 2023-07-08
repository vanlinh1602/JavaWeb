package com.aptech.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.aptech.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

	@Query("SELECT c FROM Category c WHERE c.enabled = true ORDER BY c.name ASC")
	public List<Category> findAllEnabled();
	
	@Query("SELECT c FROM Category c WHERE c.enabled = true AND c.alias = ?1")
	public Category findByAliasEnabled(String alias);

	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> findRootCategories(Sort sort);

	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public Page<Category> findRootCategories(Pageable pageable);

	
}

