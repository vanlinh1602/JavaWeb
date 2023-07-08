package com.aptech.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShopRestController {
	@Autowired
	private ShopService service;
	
	@PostMapping("/shops/check_name")
	public String checkDuplicateName(String name) {
		return service.isNameUnique(name) ? "OK" : "Duplicated";
	}
}
