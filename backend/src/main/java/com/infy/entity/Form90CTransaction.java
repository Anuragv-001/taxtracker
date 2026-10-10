package com.infy.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "form_90c_transactions")
public class Form90CTransaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer txnId;
	
	private String organizationName;
	private Double amount;
	private Double taxAmount;
	private String type;
	private LocalDate txnDate;
	
	@ManyToOne
	@JoinColumn(name="form_id",nullable=false)
	private Form90C form90C;
}
