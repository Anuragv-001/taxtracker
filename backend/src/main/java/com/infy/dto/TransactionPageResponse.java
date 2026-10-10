package com.infy.dto;

import java.util.List;

import lombok.Data;

@Data
public class TransactionPageResponse {
	private long totalRecords;
	private int pageNumber;
	private int pageSize;
	private List<TransactionDTO> transactions;
}
