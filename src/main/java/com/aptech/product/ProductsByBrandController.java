package com.aptech.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.aptech.brand.BrandRepository;
import com.aptech.category.CategoryService;
import com.aptech.common.entity.Brand;
import com.aptech.common.entity.Category;
import com.aptech.common.entity.product.Product;
import com.aptech.common.entity.section.Section;
import com.aptech.common.entity.section.SectionType;
import com.aptech.section.SectionService;

@Controller
public class ProductsByBrandController {
	
	@Autowired private BrandRepository brandRepo;
	@Autowired private ProductService productService;
	@Autowired private SectionService sectionService;
	@Autowired private CategoryService categoryService;
	
	@GetMapping("/brand/{brand_id}")
	public String listProductsByBrand(@PathVariable(name = "brand_id") Integer brandId, Model model) {
		return listProductsByBrandByPage(brandId, 1, model);
	}
	
	@GetMapping("/brand/{brand_id}/page/{pageNum}")
	public String listProductsByBrandByPage(@PathVariable(name = "brand_id") Integer brandId,
			@PathVariable(name = "pageNum") int pageNum,
			Model model) {
		
		Optional<Brand> brandById = brandRepo.findById(brandId);
		if (!brandById.isPresent()) {
			return "error/404";
		}
		
		Brand brand = brandById.get();
		
		Page<Product> pageProducts = productService.listByBrand(pageNum, brand.getId());
		List<Product> listProducts = pageProducts.getContent();
		
		List<Section> listSections = sectionService.listEnabledSections();
		model.addAttribute("listSections", listSections);	
		
		if (hasAllCategoriesSection(listSections)) {
			List<Category> listCategories = categoryService.listNoChildrenCategories();
			model.addAttribute("listCategories", listCategories);
		}
		
		
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", "asc");
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("currentPage", pageNum);	
		
		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
		if (endCount > pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		model.addAttribute("brand", brand);
		model.addAttribute("products", listProducts);
		model.addAttribute("pageTitle", "Products by " + brand.getName());		
		
		return "product/products_by_brand";
	}

	private boolean hasAllCategoriesSection(List<Section> listSections) {
		for (Section section : listSections) {
			if (section.getType().equals(SectionType.ALL_CATEGORIES)) {
				return true;
			}
		}
		
		return false;
	}
}
