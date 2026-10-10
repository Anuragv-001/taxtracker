package com.infy.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "form_90c")
public class Form90C {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer formId;
	private String name;
	private String mobileNumber;
	
	private String financialYear;
	private String status;
	
	@OneToOne
	@JoinColumn(name="user_id",unique=true)
	private User user;
		
	@OneToMany(mappedBy="form90C",cascade = CascadeType.ALL)
	private List<Form90CTransaction> transactions;
	
	@OneToMany(mappedBy="form90C",cascade = CascadeType.ALL)
	private List<Document> documents;
}
