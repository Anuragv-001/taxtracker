package com.infy.repository;


import org.springframework.data.repository.CrudRepository;

import com.infy.entity.Form90CTransaction;


public interface Form90CTransactionRepository extends CrudRepository<Form90CTransaction, Integer>{
	
   public void deleteByForm90CFormId(Integer formId);
}
