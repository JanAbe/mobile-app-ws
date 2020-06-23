package com.appsdeveloperblog.app.ws.mobileappws.ui.controller;

import com.appsdeveloperblog.app.ws.mobileappws.ui.controller.model.request.LoginRequestModel;

import io.swagger.annotations.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthenticationController {
	
	@ApiOperation("User login")
	@ApiResponses(value = {
		@ApiResponse(code = 200, 
			message = "Response Headers", 
			responseHeaders = {
				@ResponseHeader(name = "authorization", 
						description = "Bearer <JWT value here>"),
				@ResponseHeader(name = "userId", 
						description = "<Public User Id value here>")
			}
		)  
	})
	@PostMapping("/users/login")
	public void theFakeLogin(@RequestBody LoginRequestModel loginRequestModel) {
		throw new IllegalStateException("This method should not be called. It is actually implemented by Spring Security");
	}
}