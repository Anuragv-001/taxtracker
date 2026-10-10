package com.infy.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.infy.dto.SubmissionRequestDTO;
import com.infy.entity.Form90C;
import com.infy.entity.Form90CTransaction;
import com.infy.entity.User;
import com.infy.exception.TaxTrackerException;
import com.infy.repository.Form90CRepository;
import com.infy.repository.TransactionRepository;
import com.infy.repository.UserRepository;
import com.infy.service.SubmissionServiceImpl;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceImplTest {
	
	@InjectMocks
	private SubmissionServiceImpl submissionServiceImpl;
	
	@Mock
	private Form90CRepository form90CRepository;
	
	@Mock
	private TransactionRepository transactionRepository;
	
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private Authentication authentication;
	
	@Mock
	private SecurityContext securityContext;
	
	

	@Test
	void submitFormSuccess() throws TaxTrackerException {
		SubmissionRequestDTO request = new SubmissionRequestDTO();
		request.setFormId(1);
		
		Form90C form = new Form90C();
		form.setFormId(1);
		form.setFinancialYear("2024-25");
		form.setStatus("DRAFT");
		
		
		Form90CTransaction formTxn = new Form90CTransaction();
		formTxn.setOrganizationName("ABC Corp");
		formTxn.setAmount(10000.0);
		formTxn.setTaxAmount(1000.0);
		formTxn.setType("TDS");
		formTxn.setTxnDate(LocalDate.now());
		form.setTransactions(List.of(formTxn));
		
		when(form90CRepository.findById(1)).thenReturn(Optional.of(form));
		
		when(securityContext.getAuthentication()).thenReturn(authentication);
		
		when(authentication.getPrincipal()).thenReturn(10);
		
		SecurityContextHolder.setContext(securityContext);
		
		User user  = new User();
		user.setUserId(10);
		
		when(userRepository.findById(10)).thenReturn(Optional.of(user));
		
		submissionServiceImpl.submitForm(request);
		
		verify(transactionRepository).saveAll(Mockito.anyList());
		verify(form90CRepository).save(form);
		assertEquals("SUBMITTED", form.getStatus());
	}
	
	
	@Test
	void submitFormFormNotFound() {
		SubmissionRequestDTO request = new SubmissionRequestDTO();
		request.setFormId(1);
		
		when(form90CRepository.findById(1)).thenReturn(Optional.empty());
		
		TaxTrackerException exception = assertThrows(TaxTrackerException.class, () -> submissionServiceImpl.submitForm(request));
		
		assertEquals("Form Not Found", exception.getMessage());
		
		verify(form90CRepository).findById(1);
		verify(transactionRepository, Mockito.never()).saveAll(Mockito.any());
		verify(form90CRepository, Mockito.never()).save(Mockito.any());
		
	}
	

	@Test
	void submitFormSecurityContextMissing() {
		SubmissionRequestDTO request = new SubmissionRequestDTO();
		request.setFormId(1);
		
		Form90C form = new Form90C();
		form.setFormId(1);
		
		when(form90CRepository.findById(1)).thenReturn(Optional.of(form));
		
		SecurityContextHolder.clearContext();
		
		assertThrows(NullPointerException.class, () -> submissionServiceImpl.submitForm(request));
	}
	
	@Test
	void submitFormUserNotFoundShouldThrowException() {

	    Integer formId = 1;
	    Integer userId = 100;

	    SubmissionRequestDTO dto = new SubmissionRequestDTO();
	    dto.setFormId(formId);

	    Form90C form = new Form90C();
	    form.setTransactions(new ArrayList<>());

	    when(form90CRepository.findById(formId))
	            .thenReturn(Optional.of(form));

	    when(userRepository.findById(userId))
	            .thenReturn(Optional.empty());

	    Authentication auth = Mockito.mock(Authentication.class);
	    SecurityContext context = Mockito.mock(SecurityContext.class);

	    when(auth.getPrincipal()).thenReturn(userId);
	    when(context.getAuthentication()).thenReturn(auth);
	    SecurityContextHolder.setContext(context);

	    // ---- expect exception ----
	    TaxTrackerException ex = assertThrows(
	            TaxTrackerException.class,
	            () -> submissionServiceImpl.submitForm(dto)
	    );

	    assertEquals("User Not Found", ex.getMessage());

	    verify(userRepository, Mockito.never()).save(Mockito.any());
	}
}
