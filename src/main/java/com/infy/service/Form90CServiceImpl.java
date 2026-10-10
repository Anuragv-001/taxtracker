package com.infy.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infy.dto.Form90CDTO;
import com.infy.dto.Form90CTransactionDTO;
import com.infy.entity.Form90C;
import com.infy.entity.Form90CTransaction;
import com.infy.entity.User;
import com.infy.exception.TaxTrackerException;
import com.infy.repository.Form90CRepository;
import com.infy.repository.Form90CTransactionRepository;
import com.infy.repository.UserRepository;


@Service(value = "form90CService")
@Transactional
public class Form90CServiceImpl implements Form90CService{
	
	@Autowired
	private Form90CRepository formRepo;
	@Autowired
	private Form90CTransactionRepository formTxnRepo;
	@Autowired
	private UserRepository userRepo;
	
	private ModelMapper modelMapper = new ModelMapper();
	
	public Integer saveForm(Form90CDTO formDto) throws TaxTrackerException{
		Integer userId =(Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user  = userRepo.findById(userId)
				.orElseThrow(() -> new TaxTrackerException("User Not Found"));

		Form90C form=formRepo.findByUserUserId(user.getUserId());
		if(form==null) {
			form=new Form90C();
		}
		form.setName(formDto.getName());
		form.setMobileNumber(formDto.getMobileNumber());
		form.setFinancialYear(formDto.getFinancialYear());
		form.setStatus("DRAFT");
		form.setUser(user);
		List<Form90CTransaction> list=new ArrayList<>();
		List<Form90CTransactionDTO> x=formDto.getTransactionHistory();
		formTxnRepo.deleteByForm90CFormId(form.getFormId());
		for(Form90CTransactionDTO a:x) {
			Form90CTransaction ele=modelMapper.map(a, Form90CTransaction.class);
			ele.setForm90C(form);
			ele.setTxnDate(a.getTxnDate());
			list.add(ele);
		}
		form.setTransactions(list);
		
		
		return formRepo.save(form).getFormId();
	}



	@Override
	public Form90CDTO getFormByEmail(String emailId) throws TaxTrackerException{
		
		User user = userRepo.findByEmail(emailId).orElseThrow(() -> new TaxTrackerException("User not found"));
		
		Form90C  form = formRepo.findByUserUserId(user.getUserId());
		
		
		if(form == null) {
			throw new TaxTrackerException("form details not found!!");
		}
		
		Form90CDTO formDto = modelMapper.map(form, Form90CDTO.class);
		
		List<Form90CTransactionDTO> txnList = 
					form.getTransactions()
					.stream()
					.map(txn -> modelMapper.map(txn, Form90CTransactionDTO.class))
					.toList();

		formDto.setTransactionHistory(txnList);
		formDto.setName(form.getName());
		return formDto;
		
	}

}
