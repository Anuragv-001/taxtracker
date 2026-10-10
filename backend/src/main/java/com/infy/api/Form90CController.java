package com.infy.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.dto.Form90CDTO;
import com.infy.exception.TaxTrackerException;
import com.infy.service.Form90CService;

@RestController
@RequestMapping(value = "/tax-tracker")
public class Form90CController {
	
	@Autowired
	private Form90CService service;
	
	@PostMapping("/forms/90c")
	public ResponseEntity<Integer> saveForm(@RequestBody Form90CDTO dto) throws TaxTrackerException{
		Integer id = service.saveForm(dto);
		return new ResponseEntity<>(id, HttpStatus.CREATED);
	}
	
	
	@GetMapping(value = "/forms/90c/{emailId}")
	public ResponseEntity<Form90CDTO> getForm90C(@PathVariable String emailId) throws TaxTrackerException{
		Form90CDTO form90CDto = service.getFormByEmail(emailId);
		return new ResponseEntity<>(form90CDto, HttpStatus.OK);
	}
}
