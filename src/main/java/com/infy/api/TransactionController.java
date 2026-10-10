package com.infy.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infy.dto.TransactionDTO;
import com.infy.dto.TransactionPageResponse;
import com.infy.exception.TaxTrackerException;
import com.infy.service.TransactionService;



@RestController
@RequestMapping("tax-tracker")
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	@GetMapping("transactions")
	public ResponseEntity<TransactionPageResponse> getTransaction(@RequestParam(defaultValue="0") int pageNumber,@RequestParam(defaultValue="10") int pageSize,@RequestParam(required=false) String financialYear,@RequestParam(required=false) Integer month) throws TaxTrackerException{
		Integer userId =(Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TransactionPageResponse response=transactionService.getTransactions(userId, pageNumber, pageSize, financialYear, month);
        return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@GetMapping("transactions/download")
	public ResponseEntity<List<TransactionDTO>> getFile() throws TaxTrackerException{
		Integer userId =(Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<TransactionDTO> txnDto=transactionService.getTransactionOfUser(userId);
		return new ResponseEntity<>(txnDto,HttpStatus.OK);
	}
}
