package com.aptech.contact;

import org.springframework.data.repository.CrudRepository;

import com.aptech.common.entity.Contact;

public interface ContactReponsitory extends CrudRepository<Contact, Integer> {
	
}
