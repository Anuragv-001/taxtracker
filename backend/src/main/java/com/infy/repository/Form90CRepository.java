package com.infy.repository;

import org.springframework.data.repository.CrudRepository;

import com.infy.entity.Form90C;

public interface Form90CRepository extends CrudRepository<Form90C, Integer>{
	public Form90C findByUserUserId(Integer userId);
}
