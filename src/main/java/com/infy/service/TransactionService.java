package com.infy.service;

import java.util.List;

import com.infy.dto.TransactionDTO;
import com.infy.dto.TransactionPageResponse;
import com.infy.exception.TaxTrackerException;

public interface TransactionService {
	public TransactionPageResponse getTransactions(Integer userId,int pageNumber,int pageSize,String financialYear,Integer month) throws TaxTrackerException;
	public List<TransactionDTO> getTransactionOfUser(Integer userId) throws TaxTrackerException;
}
