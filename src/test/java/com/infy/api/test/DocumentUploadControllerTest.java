package com.infy.api.test;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.api.DocumentUploadController;
import com.infy.dto.DocumentUploadDTO;
import com.infy.entity.Form90C;
import com.infy.entity.User;
import com.infy.repository.UserRepository;
import com.infy.security.JwtFilter;
import com.infy.service.DocumentUploadService;
import com.infy.utility.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@WebMvcTest(DocumentUploadController.class)
@Import({JwtFilter.class})
@AutoConfigureMockMvc(addFilters = false)
class DocumentUploadControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private DocumentUploadService documentService;

	@MockitoBean
	private UserRepository userRepo;
	
	@MockitoBean
	private JwtUtil jwtUtil;
	
	@MockitoBean
	private JwtFilter jwtFilter;
	

	private final String token = "dummy-token";
	
	@BeforeEach
	void setUp() {
		@SuppressWarnings("unchecked")
		Jws<Claims> mockJws = Mockito.mock(Jws.class);
		when(jwtUtil.validateToken(Mockito.anyString())).thenReturn(mockJws);
		when(jwtUtil.getUserIdFromToken(Mockito.anyString())).thenReturn(1);
		
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(1, null, null));
	}
	

	
	@Test
	void uploadDocumentSuccess() throws Exception {
		
		DocumentUploadDTO dto = new DocumentUploadDTO();
		dto.setName("file.pdf");
		dto.setData(Base64.getEncoder().encodeToString("dummy".getBytes()));

	    Form90C form = new Form90C();
	    form.setFormId(10);
	    
	    User user = new User();
	    user.setUserId(1);
	    user.setForms(form);
	    

	    when(userRepo.findById(anyInt())).thenReturn(Optional.of(user));

	    doNothing().when(documentService).uplaodDocument(anyInt(), ArgumentMatchers.any(DocumentUploadDTO.class));

	    mockMvc.perform(MockMvcRequestBuilders.post("/uploads/final")

	    		.header("Authorization", "Bearer"+token)
	            .contentType(MediaType.APPLICATION_JSON)

	            .content(objectMapper.writeValueAsString(dto)))

	            .andExpect(MockMvcResultMatchers.status().isOk())

	            .andExpect(MockMvcResultMatchers.content().string("File Uploaded successfully"));

	}
	
	@Test
	void uploadDocumentUserNotFound() throws Exception {
	
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(1, null,null));
		
	    when(userRepo.findById(anyInt())).thenReturn(Optional.empty());

	    mockMvc.perform(MockMvcRequestBuilders.post("/uploads/final")

	            

	            .contentType(MediaType.APPLICATION_JSON)

	            .content(objectMapper.writeValueAsString(new DocumentUploadDTO())))

	            .andExpect(MockMvcResultMatchers.status().isInternalServerError());

		
	}
	
}
