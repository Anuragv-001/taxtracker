package com.infy.service;


import com.infy.dto.DocumentUploadDTO;
import com.infy.exception.TaxTrackerException;

public interface DocumentUploadService {
	public void uplaodDocument(Integer formId, DocumentUploadDTO dto) throws TaxTrackerException;
}
