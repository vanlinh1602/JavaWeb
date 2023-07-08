package com.aptech.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.aptech.category.CategoryService;
import com.aptech.common.entity.Category;
import com.aptech.common.entity.Shop;
import com.aptech.common.entity.product.Product;
import com.aptech.common.entity.section.Section;
import com.aptech.common.entity.section.SectionType;
import com.aptech.section.SectionService;
import com.aptech.shop.ShopRepository;

@Controller
public class ProductsByShopController {
	
	@Autowired private ShopRepository shopRepository;
	@Autowired private ProductService productService;
	@Autowired private SectionService sectionService;
	@Autowired private CategoryService categoryService;
	
	@GetMapping("/test")
	public String listProductsByShop() {
		return "product/products_by_shop";
	}
	
//	@GetMapping("/shop/{shop_id}")
//	public String listProductsByShop(@PathVariable(name = "shop_id") Integer shopId, Model model) {
//		return listProductsByShopByPage(shopId, 1, model);
//	}
//	
//	@GetMapping("/shop/{shop_id}/page/{pageNum}")
//	public String listProductsByShopByPage(@PathVariable(name = "shop_id") Integer shopId,
//			@PathVariable(name = "pageNum") int pageNum,
//			Model model) {
//		
//		Optional<Shop> shopById = shopRepository.findById(shopId);
//		if (!shopById.isPresent()) {
//			return "error/404";
//		}
//		
//		Shop shop = shopById.get();
//		
//		Page<Product> pageProducts = productService.listByBrand(pageNum, shop.getId());
//		List<Product> listProducts = pageProducts.getContent();
//		
//		List<Section> listSections = sectionService.listEnabledSections();
//		model.addAttribute("listSections", listSections);	
//		
//		if (hasAllCategoriesSection(listSections)) {
//			List<Category> listCategories = categoryService.listNoChildrenCategories();
//			model.addAttribute("listCategories", listCategories);
//		}
//		
//		
//		model.addAttribute("sortField", "name");
//		model.addAttribute("sortDir", "asc");
//		model.addAttribute("totalPages", pageProducts.getTotalPages());
//		model.addAttribute("totalItems", pageProducts.getTotalElements());
//		model.addAttribute("currentPage", pageNum);	
//		
//		long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
//		model.addAttribute("startCount", startCount);
//		
//		long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
//		if (endCount > pageProducts.getTotalElements()) {
//			endCount = pageProducts.getTotalElements();
//		}
//		
//		model.addAttribute("endCount", endCount);
//		
//		model.addAttribute("shop", shop);
//		model.addAttribute("products", listProducts);
//		model.addAttribute("pageTitle", "Products by " + shop.getName());		
//		
//		return "product/products_by_shop";
//	}
//
//	private boolean hasAllCategoriesSection(List<Section> listSections) {
//		for (Section section : listSections) {
//			if (section.getType().equals(SectionType.ALL_CATEGORIES)) {
//				return true;
//			}
//		}
//		
//		return false;
//	}
}
