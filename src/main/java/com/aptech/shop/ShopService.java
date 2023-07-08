package com.aptech.shop;


import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.aptech.common.entity.Customer;
import com.aptech.common.entity.Shop;
import com.aptech.common.exception.ShopNotFoundException;

@Service
public class ShopService {
	public static final int SHOPS_PER_PAGE = 5;
	
	@Autowired private ShopRepository shopRepository;


	
	public Page<Shop> listForCustomerByPage(Customer customer, int pageNum, String sortField, String sortDir,
			String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, SHOPS_PER_PAGE, sort);
		
		if (keyword != null) {
			return shopRepository.findAll(keyword, customer.getId(), pageable);
		}
		
		return shopRepository.findAll(customer.getId(), pageable);
	}

	public boolean isNameUnique(String name) {
		Shop shop = shopRepository.findByName(name);
		return shop == null;
	}

	public Shop createShop(Shop shop, Customer customer) {
		
		shop.setCreatedTime(new Date());
		shop.setEnabled(true);
		shop.setCustomer(customer);
		Shop updateShop = shopRepository.save(shop);
		return updateShop;
//	    shopRepository.save(shop);
	}

	public Shop get(Integer id) throws ShopNotFoundException {
		try {
			return shopRepository.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ShopNotFoundException("Could not find any shop with ID " + id);
		}
	}

	public Shop updateShop(Shop shopInForm, Customer customer) throws ShopNotFoundException {
		Shop shopInDB = shopRepository.findByIdAndCustomer(shopInForm.getId(), customer);
		
		if (shopInDB == null) {
			throw new ShopNotFoundException("Shop ID " + shopInForm.getId() + " not found");
		}
		shopInDB.setName(shopInForm.getName());
		shopInDB.setAlias(shopInForm.getAlias());
		shopInDB.setCreatedTime(new Date());
		shopInDB.setDeliveryAddress(shopInForm.getDeliveryAddress());
		shopInDB.setCustomer(customer);
		if ( shopInDB.getImage() != null) {
			shopInDB.setImage(shopInForm.getImage());
		}
		
		return shopRepository.save(shopInDB);

	}

	public void delete(Integer id) throws ShopNotFoundException {
		Long count = shopRepository.countById(id);
		if (count == null || count == 0) {
			throw new ShopNotFoundException("Could not find shop with ID " + id);
			
		}
		shopRepository.deleteById(id);
	}

	public List<Shop> listShopByCustomer(Customer customer) {
		return shopRepository.listShopByCustomer(customer.getId());
		
	}
}
