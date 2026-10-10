package com.infy.dto;

import lombok.Data;

@Data
public class UserDTO {
	
	private Integer userId;
	private String name;
	private String email;
	private String password;
	private String addressLine1;
	private String addressLine2;
	private String area;
	private String city;
	private String state;
	private String pinCode;
	private String mobileNumber;
}
