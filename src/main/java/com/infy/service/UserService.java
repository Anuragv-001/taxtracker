package com.infy.service;

import com.infy.dto.LoginRequest;
import com.infy.dto.LoginResponse;
import com.infy.dto.UserDTO;
import com.infy.exception.TaxTrackerException;


public interface UserService {
	public Integer registerUser(UserDTO userdto) throws TaxTrackerException;
	public LoginResponse login(LoginRequest request) throws TaxTrackerException;
	public void sendRegistrationMail(String email);
}
