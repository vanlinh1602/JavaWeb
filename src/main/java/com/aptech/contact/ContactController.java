package com.aptech.contact;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aptech.category.CategoryService;
import com.aptech.common.entity.Category;
import com.aptech.common.entity.Contact;
import com.aptech.common.entity.section.Section;
import com.aptech.common.entity.section.SectionType;
import com.aptech.section.SectionService;


@Controller
public class ContactController {
	@Autowired ContactService contactService;
	@Autowired SectionService sectionService;
	@Autowired CategoryService categoryService;
	
	@GetMapping("/contacts")
	public String contactUs(Model model) {
		
		List<Section> listSections = sectionService.listEnabledSections();
		model.addAttribute("listSections", listSections);	
		
		if (hasAllCategoriesSection(listSections)) {
			List<Category> listCategories = categoryService.listNoChildrenCategories();
			model.addAttribute("listCategories", listCategories);
		}
		
		model.addAttribute("contact", new Contact());	
		return "contact";
	}
	
	private boolean hasAllCategoriesSection(List<Section> listSections) {
		for (Section section : listSections) {
			if (section.getType().equals(SectionType.ALL_CATEGORIES)) {
				return true;
			}
		}
		
		return false;
	}
	
	@PostMapping("/sendMessage")
	public String sendMessage(Contact contact, Model model, RedirectAttributes re) {
		
		contactService.sendMessage(contact);
		model.addAttribute("pageTitle", "Send Message Succeeded!");
		re.addFlashAttribute("message","You have send successfully !");
		return "redirect:/contacts";
	}
	
	
	
}
