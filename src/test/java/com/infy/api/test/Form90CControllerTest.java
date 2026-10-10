package com.infy.api.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.api.Form90CController;
import com.infy.dto.Form90CDTO;
import com.infy.repository.UserRepository;
import com.infy.service.Form90CService;
import com.infy.utility.JwtUtil;

@WebMvcTest(Form90CController.class)
@AutoConfigureMockMvc(addFilters = false)
class Form90CControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private Form90CService service;
    
    @MockitoBean
    private UserRepository userRepo;
    
    
    @Test
    void saveFormSuccess() throws Exception {

        Form90CDTO dto = new Form90CDTO();

        when(service.saveForm(any())).thenReturn(10);

        mockMvc.perform(MockMvcRequestBuilders.post("/forms/save")
                .contentType(MediaType.APPLICATION_JSON)

                .content(objectMapper.writeValueAsString(dto)))

                .andExpect(MockMvcResultMatchers.status().isCreated())

                .andExpect(MockMvcResultMatchers.content().string("10"));

    }
    
    
    @Test
    void saveFormServiceException() throws Exception {

        Form90CDTO dto = new Form90CDTO();

        when(service.saveForm(any())).thenThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.post("/forms/save")
                .contentType(MediaType.APPLICATION_JSON)

                .content(objectMapper.writeValueAsString(dto)))

                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

    }
    
    @Test
    void getFormSuccess() throws Exception {

        Form90CDTO dto = new Form90CDTO();
        dto.setName("anurag");
        dto.setMobileNumber("7310522500");
        dto.setFinancialYear("2024");
        dto.setTransactionHistory(new ArrayList<>());


        when(service.getFormByEmail(anyString())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/forms/90c/test@gmail.com")
        		.with(SecurityMockMvcRequestPostProcessors.user("testUser"))
        		.with(SecurityMockMvcRequestPostProcessors.csrf()))

                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("anurag"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mobileNumber").value("7310522500"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.financialYear").value("2024"));

    }



    @Test

    void getFormNotFound() throws Exception {

        when(service.getFormByEmail(anyString()))

                .thenThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.get("/forms/90c/test@gmail.com")
        		.with(SecurityMockMvcRequestPostProcessors.user("testUser")))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());

    }


	
}
