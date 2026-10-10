package com.infy.service;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infy.dto.DocumentUploadDTO;
import com.infy.entity.Document;
import com.infy.entity.Form90C;
import com.infy.exception.TaxTrackerException;
import com.infy.repository.DocumentRepository;
import com.infy.repository.Form90CRepository;


@Service(value = "documentUploadService")
@Transactional
public class DocumentUploadServiceImpl implements DocumentUploadService{
	
	@Autowired
	private Form90CRepository form90CRepo;
	
	@Autowired
	private DocumentRepository docRepo;

	@Override
	public void uplaodDocument(Integer formId,DocumentUploadDTO dto) throws TaxTrackerException {
		Form90C form = form90CRepo.findById(formId).orElseThrow(() -> new TaxTrackerException("Form Not Found"));
		
		Document document = new Document();
		document.setName(dto.getName());
		String base64=dto.getData();
		if(base64.contains(",")) {
			base64=base64.split(",")[1];
		}
		document.setData(Base64.getDecoder().decode(base64));
		document.setForm90C(form);

		form.getDocuments().add(document);
		docRepo.save(document);
		form90CRepo.save(form);
	}

}
