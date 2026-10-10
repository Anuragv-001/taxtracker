package com.infy.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.infy.dto.Form90CDTO;
import com.infy.dto.Form90CTransactionDTO;
import com.infy.entity.Form90C;
import com.infy.entity.Form90CTransaction;
import com.infy.entity.User;
import com.infy.exception.TaxTrackerException;
import com.infy.repository.Form90CRepository;
import com.infy.repository.Form90CTransactionRepository;
import com.infy.repository.UserRepository;
import com.infy.service.Form90CServiceImpl;

@ExtendWith(MockitoExtension.class)
class Form90CServiceImplTest {

	@InjectMocks
	private Form90CServiceImpl service;
	
	@Mock
	private Form90CTransactionRepository formTxnRepo;
	
	@Mock
	private Form90CRepository formRepo;
	
	@Mock
	private UserRepository userRepo;
	
	@Spy
	private final ModelMapper modelMapper = new ModelMapper();
	
	private Form90CDTO formDto;
	
	@BeforeEach
	void setUp() {
		formDto = new Form90CDTO();
		formDto.setName("Anurag");
		formDto.setMobileNumber("7310522500");
		formDto.setFinancialYear("2024");
		
		formDto.setTransactionHistory(new ArrayList<>());
	}
	
	private void mockSecurity(Integer userId) {
		Authentication auth = mock(Authentication.class);
		SecurityContext context = mock(SecurityContext.class);
		
		when(auth.getPrincipal()).thenReturn(userId);
		when(context.getAuthentication()).thenReturn(auth);
		SecurityContextHolder.setContext(context);
	}
	
	
	@Test
	void saveFormSuccess()  throws TaxTrackerException{
		
		mockSecurity(1);
		
		User user = new User();
		user.setUserId(1);
		user.setMobileNumber("7310522500");
		
		Form90C saved = new Form90C();
		saved.setFormId(101);
		
		when(userRepo.findById(1)).thenReturn(Optional.of(user));
		
		when(formRepo.save(any(Form90C.class))).thenReturn(saved);
		
		Integer result = service.saveForm(formDto);
		
		
		assertNotNull(result);
		assertEquals(101, result);
		
		verify(formRepo, Mockito.times(1)).save(any());
		
	}
	
	@Test
	void saveFormUserFoundThrowsException() {
		mockSecurity(1);
		
		when(userRepo.findById(1)).thenReturn(Optional.empty());
		
		assertThrows(TaxTrackerException.class, () -> service.saveForm(formDto));
	}
	
	@Test
	void saveFormUpdateExistingFormSuccess() throws TaxTrackerException {
		mockSecurity(1);
		
		User user = new User();
		user.setUserId(1);
		user.setMobileNumber("7310522500");
		
		Form90C existing = new Form90C();
		existing.setFormId(55);
		
		when(userRepo.findById(1)).thenReturn(Optional.of(user));
		
		when(formRepo.findByUserUserId(1)).thenReturn(existing);
		
		when(formRepo.save(any(Form90C.class))).thenReturn(existing);
		
		Form90CDTO form = new Form90CDTO();
		form.setMobileNumber("7310522500");
		form.setTransactionHistory(new ArrayList<>());
		
		Integer result = service.saveForm(form);
		
		assertEquals(55, result);
	}
	
	@Test
	void saveFormWithTransactionsSuccess() throws TaxTrackerException {
		mockSecurity(1);
		
		User user  = new User();
		user.setUserId(1);
		user.setMobileNumber("7310522500");
		
		when(userRepo.findById(1)).thenReturn(Optional.of(user));
		
		Form90C saved = new Form90C();
		saved.setFormId(99);
		
		when(formRepo.save(any(Form90C.class))).thenReturn(saved);
		
		Form90CTransactionDTO txn = new Form90CTransactionDTO();
		txn.setTxnDate(LocalDate.now());
		
		Form90CDTO dto = new Form90CDTO();
		dto.setMobileNumber("7310522500");
		dto.setTransactionHistory(List.of(txn));
		
		Integer res = service.saveForm(dto);
		
		assertEquals(99, res);
	}
	
	
	@Test
	void getFormByEmailSuccess() throws TaxTrackerException {
		String email = "test@gmail.com";
		
		User user = new User();
		user.setUserId(1);
		
		Form90C form = new Form90C();
		form.setFormId(100);
		
		Form90CTransaction txn = new Form90CTransaction();
		form.setTransactions(List.of(txn));
		
		Form90CDTO dto = new Form90CDTO();
		Form90CTransactionDTO txnDto = new Form90CTransactionDTO();
		
		when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
		
		when(formRepo.findByUserUserId(1)).thenReturn(form);
		
		doReturn(dto).when(modelMapper).map(any(Form90C.class), eq(Form90CDTO.class));
		
		doReturn(txnDto).when(modelMapper).map(any(Form90CTransaction.class), eq(Form90CTransactionDTO.class));
		
		Form90CDTO res = service.getFormByEmail(email);
		assertNotNull(res);
		assertEquals(1, res.getTransactionHistory().size());
	}
	
	@Test
	void getFormByEmailUserNotFoundThrowsException() {
		String email = "test@gmail.com";
		when(userRepo.findByEmail(email)).thenReturn(Optional.empty());
		assertThrows(TaxTrackerException.class, () -> service.getFormByEmail(email));
	}
	
	@Test
	void getFormByEmailFormNotFoundThrowsException() {
		String email = "test@gmail.com";
		User user =new User();
		user.setUserId(1);
		when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
		when(formRepo.findByUserUserId(1)).thenReturn(null);

		assertThrows(TaxTrackerException.class, () -> service.getFormByEmail(email));
	}
	
}
