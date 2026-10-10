package com.infy.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.dto.LoginRequest;
import com.infy.dto.LoginResponse;
import com.infy.dto.UserDTO;
import com.infy.exception.TaxTrackerException;
import com.infy.service.UserService;


@RestController
@RequestMapping("tax-tracker")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	private static final String MESSAGE = "message";
	
	@PostMapping("register")
	public ResponseEntity<String> registerUser(@RequestBody UserDTO userdto) throws TaxTrackerException{
		Integer x=userService.registerUser(userdto);
		String str="succesfully registered"+x;
		return new ResponseEntity<>(str,HttpStatus.OK);
	}
	@PostMapping("login")
	public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest req) throws TaxTrackerException{
		LoginResponse resp = userService.login(req);
        return ResponseEntity.ok(resp);
	}
	@GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, String> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put(MESSAGE, "No token provided.");
            return ResponseEntity.badRequest().body(response);
        }

        String token = authHeader.substring(7);

        if (token.isBlank()) {
            response.put(MESSAGE, "Invalid token.");
            return ResponseEntity.badRequest().body(response);
        }


        response.put(MESSAGE, "Logged out successfully. Token deleted on client side.");
        response.put("tokenStatus", "invalidate-on-client");

        return ResponseEntity.ok(response);
    }
	
}
