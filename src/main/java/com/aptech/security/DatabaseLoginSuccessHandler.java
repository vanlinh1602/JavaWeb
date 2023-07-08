package com.aptech.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.aptech.common.entity.AuthenticationType;
import com.aptech.common.entity.Customer;
import com.aptech.customer.CustomerService;

@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	
	@Lazy
	@Autowired private CustomerService customerService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
		Customer customer = userDetails.getCustomer();
		
		customerService.updateAuthenticationType(customer, AuthenticationType.DATABASE);
		
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	
}
