package com.aptech.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aptech.ControllerHelper;
import com.aptech.Utility;
import com.aptech.category.CategoryService;
import com.aptech.common.entity.Category;
import com.aptech.common.entity.Country;
import com.aptech.common.entity.Customer;
import com.aptech.common.entity.order.Order;
import com.aptech.common.entity.section.Section;
import com.aptech.common.entity.section.SectionType;
import com.aptech.order.OrderService;
import com.aptech.review.ReviewService;
import com.aptech.section.SectionService;
import com.aptech.security.CustomerUserDetails;
import com.aptech.security.oauth.CustomerOAuth2User;
import com.aptech.setting.EmailSettingBag;
import com.aptech.setting.SettingService;

@Controller
public class CustomerController {
	@Autowired private CustomerService customerService;
	@Autowired private SettingService settingService;
	@Autowired private CategoryService categoryService;
	@Autowired private SectionService sectionService;
	
	@Autowired private OrderService orderService;
	@Autowired private ControllerHelper controllerHelper;
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		List<Country> listCountries = customerService.listAllCountries();
		
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Customer Registration");
		model.addAttribute("customer", new Customer());
		
		return "register/register_form";
	}
	
	@PostMapping("/create_customer")
	public String createCustomer(Customer customer, Model model,
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		customerService.registerCustomer(customer);
		sendVerificationEmail(request, customer);
		
		model.addAttribute("pageTitle", "Registration Succeeded!");
		
		return "/register/register_success";
	}

	private void sendVerificationEmail(HttpServletRequest request, Customer customer) 
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = customer.getEmail();
		String subject = emailSettings.getCustomerVerifySubject();
		String content = emailSettings.getCustomerVerifyContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		content = content.replace("[[name]]", customer.getFullName());
		
		String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
		
		content = content.replace("[[URL]]", verifyURL);
		
		helper.setText(content, true);
		
		mailSender.send(message);
		
		System.out.println("to Address: " + toAddress);
		System.out.println("Verify URL: " + verifyURL);
	}
	
	@GetMapping("/verify")
	public String verifyAccount(@Param("code") String code, Model model) {
		boolean verified = customerService.verify(code);
		
		return "register/" + (verified ? "verify_success" : "verify_fail");
	}
	
	@GetMapping("/dashboards")
	public String listFirstPage(Model model, HttpServletRequest request) {
		return viewDashboard(model, request, 1, "orderTime", "desc", null);
	}
	
	
	
	@GetMapping("/dashboards/page/{pageNum}")
	public String viewDashboard(Model model, HttpServletRequest request, @PathVariable(name = "pageNum") int pageNum,
			String sortField, String sortDir, String keyword) {
		
		List<Section> listSections = sectionService.listEnabledSections();
		model.addAttribute("listSections", listSections);
		
		if (hasAllCategoriesSection(listSections)) {
			List<Category> listCategories = categoryService.listNoChildrenCategories();
			model.addAttribute("listCategories", listCategories);
		}
		
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		Page<Order> page = orderService.listForCustomerByPage(customer, pageNum, sortField, sortDir, keyword);
		List<Order> listOrders = page.getContent();
		List<Country> listCountries = customerService.listAllCountries();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("moduleURL", "/orders");
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("customer", customer);
		model.addAttribute("listCountries", listCountries);
		
		long startCount = (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		return "customer/dashboard";
	}

	
	private boolean hasAllCategoriesSection(List<Section> listSections) {
		for (Section section : listSections) {
			if (section.getType().equals(SectionType.ALL_CATEGORIES)) {
				return true;
			}
		}
		
		return false;
	}
	
	@PostMapping("/update_account_details")
	public String updateAccountDetails(Model model, Customer customer, RedirectAttributes ra,
			HttpServletRequest request) {
		customerService.update(customer);
		ra.addFlashAttribute("message", "Your account details have been updated.");
		
		updateNameForAuthenticatedCustomer(customer, request);
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/dashboards";
		
		if ("address_book".equals(redirectOption)) {
			redirectURL = "redirect:/address_book";
		} else if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		} else if ("checkout".equals(redirectOption)) {
			redirectURL = "redirect:/address_book?redirect=checkout";
		}
		
		return redirectURL;
	}

	private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		
		if (principal instanceof UsernamePasswordAuthenticationToken 
				|| principal instanceof RememberMeAuthenticationToken) {
			CustomerUserDetails userDetails = getCustomerUserDetailsObject(principal);
			Customer authenticatedCustomer = userDetails.getCustomer();
			authenticatedCustomer.setFirstName(customer.getFirstName());
			authenticatedCustomer.setLastName(customer.getLastName());
			
		} else if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
			String fullName = customer.getFirstName() + " " + customer.getLastName();
			oauth2User.setFullName(fullName);
		}		
	}
	
	private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
		CustomerUserDetails userDetails = null;
		if (principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		} else if (principal instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		}
		
		return userDetails;
	}
}

