package com.infy.api.test;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.api.SubmissionController;
import com.infy.dto.SubmissionRequestDTO;
import com.infy.entity.Form90C;
import com.infy.entity.User;
import com.infy.repository.UserRepository;
import com.infy.security.JwtFilter;
import com.infy.service.SubmissionService;
import com.infy.utility.JwtUtil;

@WebMvcTest(SubmissionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({JwtFilter.class})
class SubmissionControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockitoBean
    private SubmissionService submissionService;
	
	@MockitoBean
	private JwtUtil jwtutil;
	
	@MockitoBean
	private JwtFilter jwtfilter;

	@MockitoBean
    private UserRepository userRepo;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void submitFormSuccess() throws Exception {
    	
    	Authentication auth = mock(Authentication.class);

		when(auth.getName()).thenReturn("1");

		SecurityContext securityContext = mock(SecurityContext.class);

		when(securityContext.getAuthentication()).thenReturn(auth);

		SecurityContextHolder.setContext(securityContext);

        SubmissionRequestDTO dto = new SubmissionRequestDTO();

        dto.setFormId(10);
        
        Form90C form = new Form90C();
		form.setFormId(10);
		form.setFinancialYear("2024-25");
		form.setStatus("SUBMIT");
		
		User user=new User();
		user.setUserId(1);
		user.setForms(form);
        
        when(userRepo.findById(anyInt())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/submissions/final")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Form submitted successfully"));
    }

   

    @Test
    void submitFormUnauthorized() throws Exception {

    	Authentication auth = mock(Authentication.class);

		when(auth.getName()).thenReturn("1");

		SecurityContext securityContext = mock(SecurityContext.class);

		when(securityContext.getAuthentication()).thenReturn(auth);

		SecurityContextHolder.setContext(securityContext);

        SubmissionRequestDTO dto = new SubmissionRequestDTO();

        dto.setFormId(10);
        
        Form90C form = new Form90C();
		form.setFormId(1);
		form.setFinancialYear("2024-25");
		form.setStatus("SUBMIT");
		
		User user=new User();
		user.setUserId(1);
		user.setForms(form);
        
        when(userRepo.findById(anyInt())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/submissions/final")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Not your form"));
    }
	
}
