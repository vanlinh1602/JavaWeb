package com.aptech.customer;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.aptech.Utility;
import com.aptech.common.entity.Customer;
import com.aptech.common.exception.CustomerNotFoundException;
import com.aptech.setting.EmailSettingBag;
import com.aptech.setting.SettingService;

@Controller
public class ForgotPasswordController {
	@Autowired private CustomerService customerService;
	@Autowired private SettingService settingService;
	
	@GetMapping("/forgot_password")
	public String showRequestForm() {
		return "customer/recover-password";
	}
	
	@PostMapping("/forgot_password")
	public String processRequestForm(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		try {
			String token = customerService.updateResetPasswordToken(email);
			String link = Utility.getSiteURL(request) + "/reset_password?token=" + token;
			sendEmail(link, email);
			
			model.addAttribute("message", "We have sent a reset password link to your email."
					+ " Please check.");
		} catch (CustomerNotFoundException e) {
			model.addAttribute("error", e.getMessage());
		} catch (UnsupportedEncodingException | MessagingException e) {
			model.addAttribute("error", "Could not send email");
		}
		
		return "customer/recover-password";
	}
	
	private void sendEmail(String link, String email) 
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = email;
		String subject = "Here's the link to reset your password";
		
		String content = "<p>Hello,</p>"
				+ "<p>Did you forget your password?</p>"
				+ "<p>Alternately, you could use the url below to complete your action.</p>"
				+ "<p><a href=\"" + link + "\">Reset Password</a></p>"
				+ "<br>"
				+ "<p>If you don't want to change your password or didn't request this, "
				+ "please ignore and delete this message.</p>"
		        + "<p>Thank you,</p>"
		        + "<p>The Aptech Account Team.</p>";
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);		
		
		helper.setText(content, true);
		mailSender.send(message);
	}
	
	@GetMapping("/reset_password")
	public String showResetForm(@Param("token") String token, Model model) {
		Customer customer = customerService.getByResetPasswordToken(token);
		if (customer != null) {
			model.addAttribute("token", token);
		} else {
			model.addAttribute("pageTitle", "Invalid Token");
			model.addAttribute("message", "Invalid Token");
			return "message";
		}
		
		return "customer/reset_password_form";
	}
	
	@PostMapping("/reset_password")
	public String processResetForm(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		
		try {
			customerService.updatePassword(token, password);
			
			model.addAttribute("pageTitle", "Reset Your Password");
			model.addAttribute("title", "Reset Your Password");
			model.addAttribute("message", "You have successfully changed your password.");
			
		} catch (CustomerNotFoundException e) {
			model.addAttribute("pageTitle", "Invalid Token");
			model.addAttribute("message", e.getMessage());
		}	

		return "message";		
	}
}
