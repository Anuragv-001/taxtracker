package com.infy.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.infy.dto.SubmissionRequestDTO;
import com.infy.entity.Form90C;
import com.infy.entity.Form90CTransaction;
import com.infy.entity.Transaction;
import com.infy.entity.User;
import com.infy.exception.TaxTrackerException;
import com.infy.repository.Form90CRepository;
import com.infy.repository.TransactionRepository;
import com.infy.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service(value = "submissionService")
@Transactional
public class SubmissionServiceImpl implements SubmissionService{

	@Autowired
	private Form90CRepository form90CRepository;
	
	@Autowired
	private TransactionRepository transactionRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public void submitForm(SubmissionRequestDTO request) throws TaxTrackerException {
		Form90C form = form90CRepository.findById(request.getFormId()).orElseThrow(() -> new TaxTrackerException("Form Not Found"));
		
		List<Transaction> transactions = new ArrayList<>();
		
		for(Form90CTransaction ftxn : form.getTransactions()) {
			Transaction txn = new Transaction();
			txn.setOrganizationName(ftxn.getOrganizationName());
			txn.setAmount(ftxn.getAmount());
			txn.setTaxAmount(ftxn.getTaxAmount());
			txn.setType(ftxn.getType());
			txn.setUser(form.getUser());
			txn.setTxnDate(ftxn.getTxnDate());
			txn.setFinancialYear(form.getFinancialYear());
			
			transactions.add(txn);
		}
		
		Integer userId =(Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user=userRepo.findById(userId).orElseThrow(() -> new TaxTrackerException("User Not Found"));
		user.setForms(form);
		userRepo.save(user);
	
		transactionRepo.saveAll(transactions);
		
		form.setStatus("SUBMITTED");

		form90CRepository.save(form);
		
	}

}
