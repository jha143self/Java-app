package com.user.application.listener;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.user.application.model.User;
import com.user.application.registration.OnRegistrationCompleteEvent;
import com.user.application.service.IUserService;

public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

	 @Autowired
	    private IUserService service;

	    @Autowired
	    private MessageSource messages;

	    @Autowired
	    private JavaMailSender mailSender;

	    @Autowired
	    private Environment env;
	
	 @Override
	    public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
	        this.confirmRegistration(event);
	    }


	 private void confirmRegistration(final OnRegistrationCompleteEvent event) {
	        final User user = event.getUser();
	        final String token = UUID.randomUUID().toString();
	        service.createVerificationTokenForUser(user, token);

	        final SimpleMailMessage email = constructEmailMessage(event, user, token);
	        mailSender.send(email);
	    }

	 private final SimpleMailMessage constructEmailMessage(final OnRegistrationCompleteEvent event, final User user, final String token) {
	        final String recipientAddress = user.getEmail();
	        final String subject = "Registration Confirmation";
	        final String confirmationUrl = event.getAppUrl() + "/registrationConfirm.html?token=" + token;
	        final String message = messages.getMessage("message.regSucc", null, event.getLocale());
	        final SimpleMailMessage email = new SimpleMailMessage();
	        email.setTo(recipientAddress);
	        email.setSubject(subject);
	        email.setText(message + " \r\n" + confirmationUrl);
	        email.setFrom(env.getProperty("support.email"));
	        return email;
	    }

	 

}

