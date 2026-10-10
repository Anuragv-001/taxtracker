package com.infy.dto;

import java.util.List;

import lombok.Data;

@Data
public class Form90CDTO {
	private String name;
	private String mobileNumber;
	private String financialYear;
	private List<Form90CTransactionDTO> transactionHistory;
}
