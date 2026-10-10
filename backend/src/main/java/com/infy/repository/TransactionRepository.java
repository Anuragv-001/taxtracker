package com.infy.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.infy.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction,Integer>{
	List<Transaction> findByUserUserId(Integer userId);
	Page<Transaction> findByUserUserId(Integer userId,Pageable pageable);
	Page<Transaction> findByUserUserIdAndFinancialYear(Integer userId,String financialYear,Pageable pageable);
	
	@Query("SELECT t FROM Transaction t WHERE t.user.userId= :userId AND t.financialYear= :financialYear AND MONTH(t.txnDate)=:month")
	Page<Transaction> findByUserYearAndMonth(@Param("userId") Integer userId,@Param("financialYear") String financialYear,@Param("month") Integer month,Pageable pageable);
	
	@Query("SELECT t FROM Transaction t WHERE t.user.userId= :userId AND MONTH(t.txnDate)=:month")
	Page<Transaction> findByUserAndMonth(@Param("userId") Integer userId,@Param("month") Integer month,Pageable pageable);
}
