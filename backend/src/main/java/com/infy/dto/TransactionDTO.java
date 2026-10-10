package com.infy.dto;

import java.time.LocalDate;


import lombok.Data;

@Data
public class TransactionDTO {
	private Integer txnId;
	private LocalDate date;
	private Double amount;
	private Double taxAmount;
	private String type;
	private String organizationName;
}
