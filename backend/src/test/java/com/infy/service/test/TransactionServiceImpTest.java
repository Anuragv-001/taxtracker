package com.infy.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.infy.dto.TransactionDTO;
import com.infy.dto.TransactionPageResponse;
import com.infy.entity.Transaction;
import com.infy.exception.TaxTrackerException;
import com.infy.repository.TransactionRepository;
import com.infy.service.TransactionServiceImpl;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImpTest {
	
	@Mock
	private TransactionRepository transactionRepo;
	
	@Mock
	private ModelMapper modelMapper;
	
	@InjectMocks
	private TransactionServiceImpl transactionService;
	
	@Test
	void getTransactionsFinancialYearAllMonths() throws TaxTrackerException {
		Integer userId = 1;
		String financialYear = "2024";
		Pageable pageable  = PageRequest.of(0, 10, Sort.by("txnDate").descending());
		
		Transaction txn = new Transaction();
		Page<Transaction> page = new PageImpl<Transaction>(List.of(txn), pageable, 1);
		
		when(transactionRepo.findByUserUserIdAndFinancialYear(eq(userId), eq(financialYear), any())).thenReturn(page);
		
		when(modelMapper.map(any(Transaction.class), eq(TransactionDTO.class))).thenReturn(new TransactionDTO());
		
		TransactionPageResponse response = transactionService.getTransactions(userId, 0, 10, financialYear, null);
		
		
		assertEquals(1, response.getTransactions().size());
		assertEquals(1, response.getTotalRecords());
		verify(transactionRepo).findByUserUserIdAndFinancialYear(eq(userId), eq(financialYear), Mockito.any(Pageable.class));
	}
	
	@Test
	void getTransactionsYearAndMonth() throws TaxTrackerException{
		Integer userId = 1;
		
		Page<Transaction> page = new PageImpl<Transaction>(List.of(new Transaction()));
		
		when(transactionRepo.findByUserYearAndMonth(eq(userId), eq("2024"), eq(5), any())).thenReturn(page);
		
		when(modelMapper.map(any(),	eq(TransactionDTO.class))).thenReturn(new TransactionDTO());
		
		TransactionPageResponse res = transactionService.getTransactions(userId, 0, 10, "2024", 5);
		
		assertEquals(1, res.getTransactions().size());
		
	}
	
	@Test
	void getTransactionsOnlyMonth() throws TaxTrackerException{
		Integer userId = 1;
		
		Page<Transaction> page = new PageImpl<Transaction>(List.of(new Transaction()));
		
		when(transactionRepo.findByUserAndMonth(eq(userId), eq(5), any())).thenReturn(page);
		
		when(modelMapper.map(any(), eq(TransactionDTO.class))).thenReturn(new TransactionDTO());
		
		TransactionPageResponse res = transactionService.getTransactions(userId, 0, 10, null, 5);
		
		assertEquals(1, res.getTransactions().size());
	}
	
	@Test
	void getTransactionsOnlyYear() throws TaxTrackerException{
		Integer userId = 1;
		
		Page<Transaction> page = new PageImpl<Transaction>(List.of(new Transaction()));
		
		when(transactionRepo.findByUserUserIdAndFinancialYear(eq(userId), eq("2024"), any())).thenReturn(page);
		
		when(modelMapper.map(any(), eq(TransactionDTO.class))).thenReturn(new TransactionDTO());
		
		TransactionPageResponse res = transactionService.getTransactions(userId, 0, 10, "2024", null);
		
		assertEquals(1, res.getTransactions().size());
	}
	
	@Test
	void getTransactionsDefaultNoFilters() throws TaxTrackerException{
		Integer userId = 1;
		
		Page<Transaction> page = new PageImpl<Transaction>(List.of(new Transaction()));
		
		when(transactionRepo.findByUserUserId(eq(userId), any())).thenReturn(page);
		
		when(modelMapper.map(any(), eq(TransactionDTO.class))).thenReturn(new TransactionDTO());
		
		TransactionPageResponse res = transactionService.getTransactions(userId, 0, 10, null, null);
		
		assertEquals(1, res.getTransactions().size());
	}
	
	
	@Test
	void getTransactionOfUserSuccess() throws TaxTrackerException{
		Integer userid = 1;
		
		Transaction txn = new Transaction();
		List<Transaction> txnList = List.of(txn);
		
		TransactionDTO dto = new TransactionDTO();
		
		when(transactionRepo.findByUserUserId(userid)).thenReturn(txnList);
		
		when(modelMapper.map(any(Transaction.class), eq(TransactionDTO.class))).thenReturn(dto);
		
		List<TransactionDTO> result = transactionService.getTransactionOfUser(userid);
		
		assertEquals(1, result.size());
		
		verify(transactionRepo).findByUserUserId(userid);
		verify(modelMapper).map(any(Transaction.class), eq(TransactionDTO.class));
	}
	
	@Test
	void getTransactionOfUserEmptyList() throws TaxTrackerException {
		when(transactionRepo.findByUserUserId(Mockito.any())).thenReturn(Collections.emptyList());
		
		List<TransactionDTO> result = transactionService.getTransactionOfUser(1);
		
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
	
	
	@Test
	void getTransactionOfUserException() {
		when(transactionRepo.findByUserUserId(Mockito.any())).thenThrow(new RuntimeException("DB error"));
		
		assertThrows(RuntimeException.class, () -> transactionService.getTransactionOfUser(1));
	}
	
	
}
