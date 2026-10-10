package com.infy.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Form90CTransactionDTO {
	private String organizationName;
	private Double amount;
	private Double taxAmount;
	private String type;
	private LocalDate txnDate;
}
