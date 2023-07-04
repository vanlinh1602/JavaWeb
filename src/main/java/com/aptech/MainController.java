package com.aptech;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.aptech.category.CategoryService;
import com.aptech.common.entity.Category;
import com.aptech.common.entity.product.Product;
import com.aptech.common.entity.section.Section;
import com.aptech.common.entity.section.SectionType;
import com.aptech.product.ProductRepository;
import com.aptech.section.SectionService;

@Controller
public class MainController {

	@Autowired private CategoryService categoryService;
	@Autowired private SectionService sectionService;
	@Autowired private ProductRepository productRepository;
	
	@GetMapping("")
	public String viewHomePage(Model model) {
		List<Section> listSections = sectionService.listEnabledSections();
		
		List<Product> listProducts = (List<Product>) productRepository.findAll();
		List<Product> listBestSeller = (List<Product>) productRepository.findProductBestSeller();
		List<Product> listFlashDeal = (List<Product>) productRepository.findFlashDeal();
		
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("listBestSeller", listBestSeller);
		model.addAttribute("listSections", listSections);
		model.addAttribute("listFlashDeal", listFlashDeal);
		
		if (hasAllCategoriesSection(listSections)) {
			List<Category> listCategories = categoryService.listNoChildrenCategories();
			model.addAttribute("listCategories", listCategories);
		}
		
		return "index";
	}

	private boolean hasAllCategoriesSection(List<Section> listSections) {
		for (Section section : listSections) {
			if (section.getType().equals(SectionType.ALL_CATEGORIES)) {
				return true;
			}
		}
		
		return false;
	}
	
	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		
		return "redirect:/";
	}	
}