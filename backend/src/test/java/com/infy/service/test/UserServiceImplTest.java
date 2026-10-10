package com.infy.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.infy.dto.LoginRequest;
import com.infy.dto.LoginResponse;
import com.infy.dto.UserDTO;
import com.infy.entity.User;
import com.infy.exception.TaxTrackerException;
import com.infy.repository.UserRepository;
import com.infy.security.LoginAttemptService;
import com.infy.service.UserServiceImpl;
import com.infy.utility.JwtUtil;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
	
	@InjectMocks
	private UserServiceImpl userService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private ModelMapper modelMapper;
	
	@Mock
	private LoginAttemptService loginAttemptService;
	
	@Mock
	private JwtUtil jwtUtil;
	
	@Mock
	private JavaMailSender mailSender;
		
	@Test
	void registerUserSuccess() throws TaxTrackerException {
		UserDTO userDTO = new UserDTO();
		userDTO.setEmail("test@gmail.com");
		userDTO.setPassword("password");
		
		User user = new User();
		user.setUserId(1);
		
		when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
		when(modelMapper.map(userDTO, User.class)).thenReturn(user);
		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
		when(userRepository.save(user)).thenReturn(user);
		
		Integer userId= userService.registerUser(userDTO);
		assertEquals(1, userId);
		verify(userRepository).save(user);
	}

	@Test 
	void registerUserUserAlreadyExists() {
		UserDTO userDTO =  new UserDTO();
		userDTO.setEmail("test@gmail.com");
		userDTO.setPassword("password");

		User existingUser = new User();
		existingUser.setUserId(1);
		
		when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(existingUser));
		
		TaxTrackerException exception = assertThrows(TaxTrackerException.class, () -> userService.registerUser(userDTO)); 
		
		assertEquals("User Already Exist", exception.getMessage());
		verify(userRepository, Mockito.never()).save(Mockito.any());
	
	}
	
	
	@Test
	void loginSuccess() throws TaxTrackerException {
		LoginRequest request = new LoginRequest();
		request.setEmail("test@gmail.com");
		request.setPassword("password");
		
		User user = new User();
		user.setEmail("test@gmail.com");
		user.setPassword("encodedPassword"); 

		
		when(loginAttemptService.isBlocked("test@gmail.com")).thenReturn(false);
		when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
		when(jwtUtil.generateToken(user)).thenReturn("jwt-token");
	
		LoginResponse response = userService.login(request);
		
		assertNotNull(response);
		assertEquals("jwt-token", response.getToken());
		assertNotNull(response.getToken());
		
		verify(loginAttemptService).loginSucceeded("test@gmail.com");
	}
	
	
	@Test
	void loginInvalidPassword() { 
		LoginRequest request = new LoginRequest();
		request.setEmail("test@gmail.com");
		request.setPassword("wrongPassword");
		
		User user = new User();
		user.setEmail("test@gmail.com");
		user.setPassword("encodedPassword");
		
		Mockito.when(loginAttemptService.isBlocked("test@gmail.com")).thenReturn(false);
		Mockito.when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
		Mockito.when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);
		
		TaxTrackerException exception = assertThrows(TaxTrackerException.class, () -> userService.login(request));
		
		assertEquals("Invalid credentials.", exception.getMessage());
		
		verify(loginAttemptService).loginFailed("test@gmail.com");
		verify(jwtUtil, Mockito.never()).generateToken(Mockito.any());
	}
	
	@Test
	void loginUserNotFound() {
		LoginRequest request = new LoginRequest();
		request.setEmail("test@gmail.com");
		request.setPassword("password");
		
		when(loginAttemptService.isBlocked("test@gmail.com")).thenReturn(false);
		when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());
		
		TaxTrackerException exception = assertThrows(TaxTrackerException.class, () -> userService.login(request));
		
		assertEquals("Invalid credentials.", exception.getMessage());
		verify(loginAttemptService).loginFailed("test@gmail.com");
		verify(jwtUtil, Mockito.never()).generateToken(Mockito.any());
	}
	
	@Test
	void loginAccountBlocked() {
		LoginRequest request = new LoginRequest();
		request.setEmail("test@gmail.com");
		request.setPassword("password");
		
		when(loginAttemptService.isBlocked("test@gmail.com")).thenReturn(true);
		
		TaxTrackerException exception = assertThrows(TaxTrackerException.class, () -> userService.login(request));
		
		assertEquals("Too many failed attempts. Try again later.", exception.getMessage());
		
		verify(loginAttemptService).isBlocked("test@gmail.com");
		verifyNoInteractions(userRepository);
		verify(jwtUtil, Mockito.never()).generateToken(Mockito.any());
	}
	
	@Test
	void sendRegistrationMailSuccess() {
		String mail = "test@gmail.com";
		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		
		userService.sendRegistrationMail(mail);
		
		verify(mailSender).send(captor.capture());
		
		SimpleMailMessage sentMsg = captor.getValue();
		
		assertEquals("ephoriadepartmentday@gmail.com", sentMsg.getFrom());
		assertEquals(mail, sentMsg.getTo()[0]);
		assertEquals("TaxTracker registration successful", sentMsg.getSubject());
	}
	
	@Test
	void registerUserMobileAlreadyUsedShouldThrowException() {

	    UserDTO dto = new UserDTO();
	    dto.setEmail("new@gmail.com");
	    dto.setMobileNumber("9999999999");

	    when(userRepository.findByEmail(dto.getEmail()))
	            .thenReturn(Optional.empty());

	    when(userRepository.findByMobileNumber(dto.getMobileNumber()))
	            .thenReturn(Optional.of(new User()));

	    TaxTrackerException ex = assertThrows(
	            TaxTrackerException.class,
	            () -> userService.registerUser(dto)
	    );

	    assertEquals("Mobile number already being used", ex.getMessage());
	}
	
}
