package com.infy.api.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.infy.api.TransactionController;
import com.infy.dto.TransactionDTO;
import com.infy.dto.TransactionPageResponse;
import com.infy.repository.UserRepository;
import com.infy.service.TransactionService;
import com.infy.utility.JwtUtil;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private TransactionService transactionService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private JwtUtil jwtutil;


	@Test
	void getTransactionsSuccess() throws Exception {

		TransactionDTO dto = new TransactionDTO();
		dto.setTxnId(1);
		dto.setAmount(5000.0);
		dto.setType("credit");
		dto.setOrganizationName("amazon");
		dto.setDate(LocalDate.now());

		TransactionPageResponse response = new TransactionPageResponse();

		response.setTransactions(List.of(dto));
		response.setTotalRecords(1L);
		response.setPageNumber(0);
		response.setPageSize(10);

		Authentication auth = mock(Authentication.class);

		when(auth.getPrincipal()).thenReturn(1);

		SecurityContext securityContext = mock(SecurityContext.class);

		when(securityContext.getAuthentication()).thenReturn(auth);

		SecurityContextHolder.setContext(securityContext);

		when(transactionService.getTransactions(anyInt(), anyInt(), anyInt(), anyString(), any())).thenReturn(response);

		mockMvc.perform(get("/transaction/getTransaction").param("pageNumber", "0").param("pageSize", "10")
				.param("financialYear", "2023-2024").param("month", "01")).andExpect(status().isOk())
				.andExpect(jsonPath("$.transactions[0].organizationName").value("amazon"));

		verify(transactionService, Mockito.times(1)).getTransactions(anyInt(), anyInt(), anyInt(), anyString(), any());
	}


	@Test
	void downloadSuccess() throws Exception {
		
		Authentication auth = mock(Authentication.class);

		when(auth.getPrincipal()).thenReturn(1);

		SecurityContext securityContext = mock(SecurityContext.class);

		when(securityContext.getAuthentication()).thenReturn(auth);

		SecurityContextHolder.setContext(securityContext);
		
		TransactionDTO dto=new TransactionDTO();
		dto.setAmount(2500.0);
		
		when(transactionService.getTransactionOfUser(anyInt())).thenReturn(List.of(dto));
		
		mockMvc.perform(get("/transaction/download")).andExpect(status().isOk())
					.andExpect(jsonPath("$[0].amount").value(2500.0));
	}



}
