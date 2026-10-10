package com.infy.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="users")
public class User{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int userId;
	private String name;
	
	@Column(unique=true)
	private String email;
	private String password;
	private String addressLine1;
	private String addressLine2;
	private String area;
	private String city;
	private String state;
	private String pinCode;
	
	@Column(unique=true)
	private String mobileNumber;
	
	@OneToMany(mappedBy="user",cascade = CascadeType.ALL)
	private List<Transaction> transactions;
	
	@OneToOne(mappedBy="user",cascade=CascadeType.ALL)
	private Form90C forms;
	
	
}
