package com.foucusflow.dto;

import java.io.Serializable;
import java.util.Base64;

import com.foucusflow.entity.User;

public class UserDto implements Serializable {

	private static final long serialVersionUID = 3546501080513125627L;

	private long id;

	private String fullName;

	private String username;

	private String dummyName;

	private String email;

	private String phoneNo;

	private String gender;

	private String profileImg;

	private String branch;

	private String semester;

	public UserDto(User user) {
		super();
		this.id = user.getId();
		this.fullName = user.getFullName();
		this.username = user.getUsername();
		this.dummyName = user.getDummyName();
		this.email = user.getEmail();
		this.phoneNo = user.getPhoneNo();
		this.gender = user.getGender();
		if (user.getProfileImg() != null) {
			this.profileImg = Base64.getEncoder().encodeToString(user.getProfileImg());
		}
		this.branch = user.getBranch();
		this.semester = user.getSemester();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getProfileImg() {
		return profileImg;
	}

	public void setProfileImg(String profileImg) {
		this.profileImg = profileImg;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getSemester() {
		return semester;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	@Override
	public String toString() {
		return "UserDto [id=" + id + ", fullName=" + fullName + ", username=" + username + ", dummyName=" + dummyName
				+ ", email=" + email + ", phoneNo=" + phoneNo + ", gender=" + gender + ", profileImg=" + profileImg
				+ ", branch=" + branch + ", semester=" + semester + "]";
	}
}