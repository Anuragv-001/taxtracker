package com.infy.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.infy.dto.DocumentUploadDTO;
import com.infy.entity.Form90C;
import com.infy.exception.TaxTrackerException;
import com.infy.repository.DocumentRepository;
import com.infy.repository.Form90CRepository;
import com.infy.service.DocumentUploadServiceImpl;

@ExtendWith(MockitoExtension.class)
class DocumentUploadServiceImplTest {

	@Mock
	private Form90CRepository form90CRepo;
	
	@InjectMocks
	private DocumentUploadServiceImpl documentUploadService;
	
	@Mock
	private DocumentRepository docRepo;
	
	@Test
	void uploadDocumentSuccess() throws TaxTrackerException {
		Integer formId = 1;
		
		DocumentUploadDTO dto = new DocumentUploadDTO();
		dto.setName("test.pdf");
		dto.setData(Base64.getEncoder().encodeToString("dummy".getBytes()));
		
		Form90C form = new Form90C();
		form.setDocuments(new ArrayList<>());
		
		when(form90CRepo.findById(formId)).thenReturn(Optional.of(form));
		
		documentUploadService.uplaodDocument(formId, dto);
		
		assertEquals(1, form.getDocuments().size());
		assertEquals("test.pdf", form.getDocuments().get(0).getName());
		verify(form90CRepo).save(form);
	}
	
	@Test
	void uploadDocumentFormNotFound() {
		Integer formId = 99;
		
		DocumentUploadDTO dto = new DocumentUploadDTO();
		dto.setName("file.pdf");
		dto.setData("abcd");
		
		when(form90CRepo.findById(formId)).thenReturn(Optional.empty());
		
		TaxTrackerException exception = assertThrows(TaxTrackerException.class, () -> documentUploadService.uplaodDocument(formId, dto));
		
		assertEquals("Form Not Found", exception.getMessage());
		verify(form90CRepo, Mockito.never()).save(Mockito.any());
		
	}
	
	@Test
	void uploadDocumentWithCommaBase64ShouldSplitAndSave() throws TaxTrackerException {

	    Integer formId = 1;

	    String pureBase64 = Base64.getEncoder().encodeToString("dummy".getBytes());

	    DocumentUploadDTO dto = new DocumentUploadDTO();
	    dto.setName("comma.pdf");

	    dto.setData("data:application/pdf;base64," + pureBase64);

	    Form90C form = new Form90C();
	    form.setDocuments(new ArrayList<>());

	    when(form90CRepo.findById(formId)).thenReturn(Optional.of(form));

	    documentUploadService.uplaodDocument(formId, dto);

	    assertEquals(1, form.getDocuments().size());
	    assertEquals("comma.pdf", form.getDocuments().get(0).getName());

	    verify(docRepo).save(Mockito.any());
	    verify(form90CRepo).save(form);
	}
	
}
