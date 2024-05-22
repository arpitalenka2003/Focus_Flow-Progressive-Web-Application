package com.foucusflow.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "contacts")
public class Contacts {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long user;

    private boolean isBlocked;
    
    private boolean isContact;

    private String saveName;

    @ManyToOne
    private User myContacts;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return myContacts;
	}

	public void setUser(User user) {
		this.myContacts = user;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public boolean isContact() {
		return isContact;
	}

	public void setContact(boolean isContact) {
		this.isContact = isContact;
	}

	public String getSaveName() {
		return saveName;
	}

	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}

	public User getMyContacts() {
		return myContacts;
	}

	public void setMyContacts(User myContacts) {
		this.myContacts = myContacts;
	}
	
	public Contacts() {
		super();
	}

	public Contacts(String saveName, User myContacts, long user, boolean isBlocked, boolean isContact) {
        this.saveName = saveName;
        this.myContacts = myContacts;
        this.user = user;
        this.isBlocked = isBlocked;
        this.isContact = isContact;
    }
	
	public Contacts(long fId, String saveName, User myContacts, long user, boolean isBlocked, boolean isContact) {
		super();
		this.id = fId;
		this.saveName = saveName;
		this.myContacts = myContacts;
		this.user = user;
		this.isBlocked = isBlocked;
		this.isContact = isContact;
	}

	@Override
	public String toString() {
		return "Contacts [id=" + id + ", user=" + user + ", isBlocked=" + isBlocked + ", isContact=" + isContact
				+ ", saveName=" + saveName + ", myContacts=" + myContacts + "]";
	}
}