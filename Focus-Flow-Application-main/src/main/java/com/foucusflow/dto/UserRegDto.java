package com.foucusflow.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserRegDto {

	@NotBlank(message = "First name can't be empty")
	@Size(min = 3, max = 12, message = "First name must be between 3 - 12 characters")
	private String firstName;
	
	@NotBlank(message = "Last name can't be empty")
	@Size(min = 3, max = 12, message = "Last name must be between 3 - 12 characters")
	private String lastName;
	
	private String givenName;
	
	@NotBlank(message = "User name can't be empty")
	@Size(min = 3, max = 20, message = "User name must be between 5 - 12 characters")
	private String userName;
	
	@Pattern(regexp="\\d{10}", message="Phone number must be 10 digits")
	private String phoneNo;
	
	@Email(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")
	private String emailId;
	
	@NotBlank(message = "Password can't be empty")
	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", 
    message = "Password must contain at least 8 characters, including at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace")
	private String password;
	
	@NotBlank(message = "Confirm Password can't be empty")
	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", 
    message = "Password must contain at least 8 characters, including at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace")
	private String confirmPwd;
	
	@NotBlank(message = "Please select gender")
	private String gender;
	
	@NotBlank(message = "Please select a branch")
	private String branch;
	
	private String semester;
	
	@NotBlank(message = "Please select a role")
	private String role;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getGivenName() {
		return givenName;
	}
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPwd() {
		return confirmPwd;
	}
	public void setConfirmPwd(String confirmPwd) {
		this.confirmPwd = confirmPwd;
	}
	
	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
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
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@Override
	public String toString() {
		return "UserDto [firstName=" + firstName + ", lastName=" + lastName + ", givenName=" + givenName + ", userName="
				+ userName + ", phoneNo=" + phoneNo + ", emailId=" + emailId + ", password=" + password
				+ ", confirmPwd=" + confirmPwd + ", gender=" + gender + ", branch=" + branch + ", semester=" + semester
				+ ", role=" + role + "]";
	}
	
	
	
}
