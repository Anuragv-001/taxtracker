package com.infy.service;

import com.infy.dto.SubmissionRequestDTO;
import com.infy.exception.TaxTrackerException;

public interface SubmissionService {
	public void submitForm(SubmissionRequestDTO request) throws TaxTrackerException;
}
