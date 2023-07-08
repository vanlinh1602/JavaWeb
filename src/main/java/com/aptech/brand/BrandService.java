package com.aptech.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aptech.common.entity.Brand;
import com.aptech.common.exception.BrandNotFoundException;

@Service
public class BrandService {
	
	@Autowired
	private BrandRepository repo;

	public List<Brand> listAll() {
		return (List<Brand>) repo.findAll();
	}

	public Brand get(Integer id) throws BrandNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		}
	}

}
