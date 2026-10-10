package com.infy.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.dto.DocumentUploadDTO;
import com.infy.exception.TaxTrackerException;
import com.infy.repository.UserRepository;
import com.infy.service.DocumentUploadService;

@RestController
@RequestMapping(value = "/tax-tracker")
public class DocumentUploadController {
	
	@Autowired
	private DocumentUploadService documentService;
	
	@Autowired
	private UserRepository userRepo;
	
	@PostMapping(value="/uploads")
	public ResponseEntity<String> uploadDocument(@RequestBody DocumentUploadDTO dto) throws TaxTrackerException{
		
		Integer userId =(Integer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		Integer formId=(Integer)userRepo.findById(userId).get().getForms().getFormId();
		
		documentService.uplaodDocument(formId, dto);
		String message = "File Uploaded successfully";
		
		return new ResponseEntity<>(message, HttpStatus.OK); 
	}
}
