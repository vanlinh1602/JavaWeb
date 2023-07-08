package com.aptech.question;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aptech.ControllerHelper;
import com.aptech.category.CategoryService;
import com.aptech.common.entity.Category;
import com.aptech.common.entity.Customer;
import com.aptech.common.entity.Question;
import com.aptech.common.entity.product.Product;
import com.aptech.common.entity.section.Section;
import com.aptech.common.entity.section.SectionType;
import com.aptech.common.exception.ProductNotFoundException;
import com.aptech.product.ProductService;
import com.aptech.question.vote.QuestionVoteService;
import com.aptech.section.SectionService;

@Controller
public class QuestionController {

	@Autowired private ControllerHelper controllerHelper;
	
	@Autowired private QuestionService questionService;
	
	@Autowired private ProductService productService;
	
	@Autowired private QuestionVoteService voteService;	
	@Autowired private CategoryService categoryService;
	@Autowired private SectionService sectionService;
	
	@GetMapping("/ask_question/{productAlias}")
	public String askQuestion(@PathVariable(name = "productAlias") String productAlias) {
		return "redirect:/p/" + productAlias + "#qa";
	}
	
	@GetMapping("/questions/{productAlias}") 
	public String listQuestionsOfProduct(@PathVariable(name = "productAlias") String productAlias,
			Model model, HttpServletRequest request) throws ProductNotFoundException {
		return listQuestionsOfProductByPage(model, request, productAlias, 1, "votes", "desc");
	}
	
	@GetMapping("/questions/{productAlias}/page/{pageNum}") 
	public String listQuestionsOfProductByPage(
				Model model, HttpServletRequest request,
				@PathVariable(name = "productAlias") String productAlias,
				@PathVariable(name = "pageNum") int pageNum,
				String sortField, String sortDir) throws ProductNotFoundException {
		Page<Question> page = questionService.listQuestionsOfProduct(productAlias, pageNum, sortField, sortDir);
		List<Question> listQuestions = page.getContent();
		Product product = productService.getProduct(productAlias);
		
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		if (customer != null) {
			voteService.markQuestionsVotedForProductByCustomer(listQuestions, product.getId(), customer.getId());
		}		
		
		List<Section> listSections = sectionService.listEnabledSections();
		model.addAttribute("listSections", listSections);
		
		if (hasAllCategoriesSection(listSections)) {
			List<Category> listCategories = categoryService.listNoChildrenCategories();
			model.addAttribute("listCategories", listCategories);
		}
				
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("listQuestions", listQuestions);
		model.addAttribute("product", product);

		long startCount = (pageNum - 1) * QuestionService.QUESTIONS_PER_PAGE_FOR_PUBLIC_LISTING + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + QuestionService.QUESTIONS_PER_PAGE_FOR_PUBLIC_LISTING - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Page " + pageNum + " | Questions for product: " + product.getName());
		} else {
			model.addAttribute("pageTitle", "Questions for product: " + product.getName());
		}		
		
		return "product/product_questions";
	}	

	@GetMapping("/customer/questions") 
	public String listQuestionsByCustomer(Model model, HttpServletRequest request) {
		return listQuestionsByCustomerByPage(model, request, 1, null, "askTime", "desc");
	}
	
	@GetMapping("/customer/questions/page/{pageNum}") 
	public String listQuestionsByCustomerByPage(
				Model model, HttpServletRequest request,
				@PathVariable(name = "pageNum") int pageNum,
				String keyword, String sortField, String sortDir) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		Page<Question> page = questionService.listQuestionsByCustomer(customer, keyword, pageNum, sortField, sortDir);		
		List<Question> listQuestions = page.getContent();
		
		List<Section> listSections = sectionService.listEnabledSections();
		model.addAttribute("listSections", listSections);
		
		if (hasAllCategoriesSection(listSections)) {
			List<Category> listCategories = categoryService.listNoChildrenCategories();
			model.addAttribute("listCategories", listCategories);
		}
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("moduleURL", "/customer/questions");
		
		model.addAttribute("listQuestions", listQuestions);

		long startCount = (pageNum - 1) * QuestionService.QUESTIONS_PER_PAGE_FOR_PUBLIC_LISTING + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + QuestionService.QUESTIONS_PER_PAGE_FOR_PUBLIC_LISTING - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		return "question/customer_questions";
	}
	
	private boolean hasAllCategoriesSection(List<Section> listSections) {
		for (Section section : listSections) {
			if (section.getType().equals(SectionType.ALL_CATEGORIES)) {
				return true;
			}
		}
		
		return false;
	}

	@GetMapping("/customer/questions/detail/{id}")
	public String viewQuestion(@PathVariable("id") Integer id, Model model, RedirectAttributes ra, 
			HttpServletRequest request) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		Question question = questionService.getByCustomerAndId(customer, id);
		
		if (question != null) {	
			model.addAttribute("question", question);
			return "question/question_detail_modal";
		} else {
			ra.addFlashAttribute("message", "Could not find any question with ID " + id);
			return "redirect:/customer/questions";			
		}
	}	
}
