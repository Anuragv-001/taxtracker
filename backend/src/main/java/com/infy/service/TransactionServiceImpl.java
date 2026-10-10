package com.infy.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.infy.dto.TransactionDTO;
import com.infy.dto.TransactionPageResponse;
import com.infy.entity.Transaction;
import com.infy.exception.TaxTrackerException;
import com.infy.repository.TransactionRepository;

@Service(value="transactionService")
public class TransactionServiceImpl implements TransactionService{

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Cacheable(value="transactions",
			key="T(java.util.Objects).hash("
					+ "#userId,#pageNumber,"
					+ "#pageSize,"
					+ "#financialYear,"
					+ "#month)")
	public TransactionPageResponse getTransactions(Integer userId,int pageNumber,int pageSize,String financialYear,Integer month) throws TaxTrackerException {
		Pageable pageable=PageRequest.of(pageNumber, pageSize,Sort.by("txnDate").descending());
		
		Page<Transaction> page;
		if(financialYear!=null&&month!=null) {
			page=transactionRepository.findByUserYearAndMonth(userId, financialYear, month, pageable);
		}else if(financialYear!=null) {
			page=transactionRepository.findByUserUserIdAndFinancialYear(userId, financialYear, pageable);
		}else if(month!=null) {
			page=transactionRepository.findByUserAndMonth(userId, month, pageable);
		}else {
			page=transactionRepository.findByUserUserId(userId, pageable);
		}
		
		List<TransactionDTO> transactions=page.getContent().stream()
															.map(txn->modelMapper.map(txn, TransactionDTO.class)).toList();
		
		TransactionPageResponse response=new TransactionPageResponse();
		response.setPageNumber(page.getNumber());
		response.setPageSize(page.getSize());
		response.setTotalRecords(page.getTotalElements());
		response.setTransactions(transactions);
		
		return response;
	}
	public List<TransactionDTO> getTransactionOfUser(Integer userId) throws TaxTrackerException{
		List<Transaction> txnList=transactionRepository.findByUserUserId(userId);
		List<TransactionDTO> res=new ArrayList<>();
		for(Transaction x:txnList) {
			res.add(modelMapper.map(x, TransactionDTO.class));
		}
		return res;
	}
	
}
