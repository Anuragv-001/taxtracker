package com.infy.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.infy.dto.LoginRequest;
import com.infy.dto.LoginResponse;
import com.infy.dto.UserDTO;
import com.infy.entity.User;
import com.infy.exception.TaxTrackerException;
import com.infy.repository.UserRepository;
import com.infy.security.LoginAttemptService;
import com.infy.utility.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service(value="userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	private final LoginAttemptService loginAttemptService;
	private final JwtUtil jwtUtil;
	private final MailSender mailSener;
	
	@Transactional
	public Integer registerUser(UserDTO userdto) throws TaxTrackerException{
		if(!userRepository.findByEmail(userdto.getEmail()).isEmpty()) {
			throw new TaxTrackerException("User Already Exist");
		}
		if(!userRepository.findByMobileNumber(userdto.getMobileNumber()).isEmpty()) {
			throw new TaxTrackerException("Mobile number already being used");
		}
		User res=modelMapper.map(userdto, User.class);
		res.setPassword(passwordEncoder.encode(userdto.getPassword()));
		User user=userRepository.save(res);
//		sendRegistrationMail(user.getEmail()); 
		return user.getUserId();
	}
	public LoginResponse login(LoginRequest req) throws TaxTrackerException {
		String key = req.getEmail();
		if (loginAttemptService.isBlocked(key)) {
			throw new TaxTrackerException("Too many failed attempts. Try again later.");
		}
		Optional<User> maybeUser = userRepository.findByEmail(req.getEmail());
		if (maybeUser.isEmpty()) {
			loginAttemptService.loginFailed(key);
			throw new TaxTrackerException("Invalid credentials.");
		}

		User user = maybeUser.get();

		if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
			loginAttemptService.loginFailed(key);
			throw new TaxTrackerException("Invalid credentials.");
		}

		// success
		loginAttemptService.loginSucceeded(key);

		String token = jwtUtil.generateToken(user);

		return LoginResponse.builder().token(token).userName(user.getName()).email(user.getEmail()).build();
	}
	
	public void sendRegistrationMail(String email) {
		
		SimpleMailMessage msg=new SimpleMailMessage();
		
		msg.setFrom("ephoriadepartmentday@gmail.com");
		msg.setTo(email);
		msg.setSubject("TaxTracker registration successful");
		msg.setText("Hello,\n\n welcom to TaxTracker Application.\n\n Regards");
		mailSener.send(msg);
	}
}
