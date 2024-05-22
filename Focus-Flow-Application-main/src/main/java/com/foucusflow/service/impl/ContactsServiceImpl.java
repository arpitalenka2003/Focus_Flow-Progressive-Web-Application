package com.foucusflow.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foucusflow.entity.Contacts;
import com.foucusflow.entity.User;
import com.foucusflow.repo.ContactsRepository;
import com.foucusflow.service.IContactsService;

@Service
public class ContactsServiceImpl implements IContactsService {

	@Autowired
	private ContactsRepository contactsRepo;

	@Override
	public List<Contacts> getAllContacts(long userId) {
		return contactsRepo.listOfContacts(userId);
	}

	@Override
	public List<Contacts> listOfBlockedContacts(long userId) {
		return contactsRepo.listOfBlockedContacts(userId);
	}

	@Override
	public Contacts checkBlockedOrNot(long userId, long conId) {
		return contactsRepo.checkBlockedOrNot(userId, conId);
	}

	@Override
	public Contacts saveMyContact(String saveAs, User friend, long userid){
        return contactsRepo.save(new Contacts(saveAs, friend, userid, false, true));
    }
	
	@Override
    public Contacts saveMyContact1(long id, String saveAs, User friend, long userid, boolean blocked){
        return contactsRepo.save(new Contacts(id,saveAs, friend, userid, blocked, true));
    }
}