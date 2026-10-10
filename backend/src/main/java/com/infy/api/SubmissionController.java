package com.infy.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.dto.SubmissionRequestDTO;
import com.infy.exception.TaxTrackerException;
import com.infy.repository.UserRepository;
import com.infy.service.SubmissionService;

@RestController
@RequestMapping(value = "/tax-tracker")
public class SubmissionController {

	@Autowired
	private SubmissionService submissionService;
	
	@Autowired
	private UserRepository userRepo;
	
	@PostMapping("submissions")
	public ResponseEntity<String> submitForm(@RequestBody SubmissionRequestDTO request) throws TaxTrackerException{
		Integer userId =Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName()); 
		Integer formId=(Integer)userRepo.findById(userId).get().getForms().getFormId();
		if(formId!=request.getFormId()) throw new TaxTrackerException("Not your form");
		submissionService.submitForm(request);
		String message = "Form submitted successfully";
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
}
