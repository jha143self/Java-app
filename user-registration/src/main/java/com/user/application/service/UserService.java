package com.user.application.service;

import java.util.Arrays;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.application.exception.UserAlreadyExistException;
import com.user.application.model.User;
import com.user.application.repository.RoleRepository;
import com.user.application.repository.UserRepository;
import com.user.application.repository.VerificationTokenRepository;
import com.user.application.web.UserDto;
import com.user.application.model.VerificationToken;

@Service
public class UserService implements IUserService {

	@Autowired
    private UserRepository repository;
	 @Autowired
	 private PasswordEncoder passwordEncoder;
	 @Autowired
	    private VerificationTokenRepository tokenRepository;
	 @Autowired
	  private UserDetailsService userDetailsService;

	 @Autowired
	    private RoleRepository roleRepository;
	 
	    public static final String TOKEN_INVALID = "invalidToken";
	    public static final String TOKEN_EXPIRED = "expired";
	    public static final String TOKEN_VALID = "valid";

	
	    public static String APP_NAME = "RakeshJha";
	 @Override
	public User registerNewUserAccount(UserDto accountDto) throws UserAlreadyExistException {
		if (emailExists(accountDto.getEmail())) {   
            throw new UserAlreadyExistException("There is an account with that email adress: " + accountDto.getEmail());
        }
        User user = new User();    
        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        user.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
       // user.setRoles();
        return repository.save(user);       
    }
    private boolean emailExists(String email) {
        User user = repository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    	}
    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken =tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
            .getTime()
            - cal.getTime()
                .getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
        // tokenRepository.delete(verificationToken);
        repository.save(user);
        return TOKEN_VALID;
    }
    
    @Override
    public User getUser(final String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser();
        }
        return null;
    }

    @Override
    public void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }


}
