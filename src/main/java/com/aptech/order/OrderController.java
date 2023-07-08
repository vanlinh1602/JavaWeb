package com.aptech.order;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.aptech.ControllerHelper;
import com.aptech.common.entity.Customer;
import com.aptech.common.entity.order.Order;
import com.aptech.common.entity.order.OrderDetail;
import com.aptech.common.entity.product.Product;
import com.aptech.review.ReviewService;

@Controller
public class OrderController {
	@Autowired private OrderService orderService;
	@Autowired private ControllerHelper controllerHelper;
	@Autowired private ReviewService reviewService;
	
	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(Model model,
			@PathVariable(name = "id") Integer id, HttpServletRequest request) {
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		Order order = orderService.getOrder(id, customer);
		
		setProductReviewableStatus(customer, order);
		
		model.addAttribute("order", order);
		
		return "orders/order_details_modal";
	}	
	
	private void setProductReviewableStatus(Customer customer, Order order) {
		Iterator<OrderDetail> iterator = order.getOrderDetails().iterator();
		
		while(iterator.hasNext()) {
			OrderDetail orderDetail = iterator.next();
			Product product = orderDetail.getProduct();
			Integer productId = product.getId();
			
			boolean didCustomerReviewProduct = reviewService.didCustomerReviewProduct(customer, productId);
			product.setReviewedByCustomer(didCustomerReviewProduct);
			
			if (!didCustomerReviewProduct) {
				boolean canCustomerReviewProduct = reviewService.canCustomerReviewProduct(customer, productId);
				product.setCustomerCanReview(canCustomerReviewProduct);
			}
			
		}
	}
}
