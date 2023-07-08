package com.aptech.shipping;

import org.springframework.data.repository.CrudRepository;

import com.aptech.common.entity.Country;
import com.aptech.common.entity.ShippingRate;

public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {
	
	public ShippingRate findByCountryAndState(Country country, String state);
}
