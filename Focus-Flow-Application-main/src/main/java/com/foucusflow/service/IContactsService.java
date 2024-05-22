package com.foucusflow.service;

import java.util.List;

import com.foucusflow.entity.Contacts;
import com.foucusflow.entity.User;

public interface IContactsService {

	List<Contacts> getAllContacts(long userId);

	List<Contacts> listOfBlockedContacts(long userId);

	Contacts checkBlockedOrNot(long userId, long conId);

	Contacts saveMyContact(String givenName, User contact, long id);

	Contacts saveMyContact1(long id, String saveAs, User friend, long userid, boolean blocked);
}