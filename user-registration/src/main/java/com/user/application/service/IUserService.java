package com.user.application.service;

import com.user.application.exception.UserAlreadyExistException;
import com.user.application.model.User;
import com.user.application.web.UserDto;

public interface IUserService {

	
	 User registerNewUserAccount(UserDto accountDto)     
		      throws UserAlreadyExistException;
	 
	    String validateVerificationToken(String token);

	 
	    User getUser(String verificationToken);
	    
	    void createVerificationTokenForUser(User user, String token);


		}
