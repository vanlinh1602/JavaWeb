package com.aptech.contact;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aptech.common.entity.Contact;

@Service
@Transactional
public class ContactService {
	@Autowired private ContactReponsitory contactReponsitory;
	
	public void sendMessage(Contact contact) {
		contactReponsitory.save(contact);
	}
}
