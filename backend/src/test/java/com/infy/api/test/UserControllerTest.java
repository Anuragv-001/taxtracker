package com.infy.api.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infy.api.UserController;
import com.infy.dto.LoginRequest;
import com.infy.dto.LoginResponse;
import com.infy.dto.UserDTO;
import com.infy.repository.UserRepository;
import com.infy.service.UserService;
import com.infy.utility.JwtUtil;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserService userService;
	
	@MockitoBean
	private JwtUtil jwtutil;
	
	@MockitoBean
	private UserRepository userRepository;
	
	
	@Test
	void registerUserSuccess() throws Exception {
		
		UserDTO dto = new UserDTO();
		dto.setName("Sai");
		dto.setEmail("sai@test.com");
		dto.setPassword("password123");
		dto.setMobileNumber("9876543210");
		dto.setAddressLine1("street");
		dto.setAddressLine2("street2");
		dto.setArea("Area");
		dto.setCity("city");
		dto.setState("state");
		dto.setPinCode("534275");
		dto.setMobileNumber("7330993014");
		dto.setUserId(1);

		
		when(userService.registerUser(any(UserDTO.class))).thenReturn(1);
		
		mockMvc.perform(post("/tax-tracker/register")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(dto)))
		.andExpect(status().isOk())
		.andExpect(content().string("succesfully registered" + dto.getUserId()));
		
		verify(userService,times(1)).registerUser(any(UserDTO.class));
	}
	
    @Test
    void registerUserFailure() throws Exception {

        UserDTO dto = new UserDTO();
        dto.setEmail("test@gmail.com");

        when(userService.registerUser(any(UserDTO.class)))
                .thenThrow(new RuntimeException("Registration failed"));

        mockMvc.perform(post("/tax-tracker/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
    
    @Test
    void loginSuccess() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@gmail.com");
        request.setPassword("123");

        LoginResponse response = new LoginResponse();
        response.setToken("abc123");

        when(userService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/tax-tracker/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("abc123"));

        verify(userService, times(1)).login(any(LoginRequest.class));
    }
    @Test
    void logoutShouldReturn400WhenNoAuthorizationHeader() throws Exception {
        mockMvc.perform(post("/tax-tracker/logout"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("No token provided.")));
    }

    @Test
    void logoutShouldReturn400WhenInvalidBearerToken() throws Exception {
        mockMvc.perform(post("/tax-tracker/logout")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Invalid token.")));
    }

    @Test
    void logoutShouldReturn200WhenValidBearerToken() throws Exception {
        mockMvc.perform(post("/tax-tracker/logout")
                        .header("Authorization", "Bearer JWT_TOKEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message",
                        is("Logged out successfully. Token deleted on client side.")))
                .andExpect(jsonPath("$.tokenStatus", is("invalidate-on-client")));
    }
    
}
