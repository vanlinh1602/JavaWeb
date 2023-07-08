package com.aptech.menu;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.aptech.category.CategoryService;
import com.aptech.common.entity.Article;
import com.aptech.common.entity.Category;
import com.aptech.common.entity.section.Section;
import com.aptech.common.entity.section.SectionType;
import com.aptech.common.exception.MenuItemNotFoundException;
import com.aptech.section.SectionService;

@Controller
public class MenuController {

	@Autowired private MenuService service;
	@Autowired SectionService sectionService;
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/m/{alias}")
	public String viewMenu(@PathVariable(name = "alias") String alias, Model model) {
		try {
			Article article = service.getArticleBoundToMenu(alias);
			
			List<Section> listSections = sectionService.listEnabledSections();
			model.addAttribute("listSections", listSections);	
			
			if (hasAllCategoriesSection(listSections)) {
				List<Category> listCategories = categoryService.listNoChildrenCategories();
				model.addAttribute("listCategories", listCategories);
			}
			
			model.addAttribute("pageTitle", article.getTitle());
			model.addAttribute("article", article);
			
		} catch (MenuItemNotFoundException ex) {
			return "error/404";
		}
		
		return "article";
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
