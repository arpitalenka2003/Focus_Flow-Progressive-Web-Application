package com.foucusflow.dto;

import org.springframework.web.multipart.MultipartFile;

public class UserEditDto {
	
	private String id;

	private String fullName;

	private String username;

	private String dummyName;

	private String email;

	private String phoneNo;
	
	private MultipartFile profileImg;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDummyName() {
		return dummyName;
	}

	public void setDummyName(String dummyName) {
		this.dummyName = dummyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public MultipartFile getProfileImg() {
		return profileImg;
	}

	public void setProfileImg(MultipartFile profileImg) {
		this.profileImg = profileImg;
	}

	@Override
	public String toString() {
		return "UserEditDto [id=" + id + ", fullName=" + fullName + ", username=" + username + ", dummyName="
				+ dummyName + ", email=" + email + ", phoneNo=" + phoneNo + ", profileImg=" + profileImg + "]";
	}
}