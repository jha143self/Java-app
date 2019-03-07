package com.user.application.web;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import com.user.application.model.*;
import com.user.application.service.IUserService;

@Controller

public class Registration {

	@Autowired
    private IUserService userService;
	
	@Autowired
    private MessageSource messages;


	public Registration() {
		super();
	}
	@RequestMapping(value = "/user/registration", method = RequestMethod.GET)
	public String showRegistrationForm(WebRequest request, Model model) {
	    UserDto userDto = new UserDto();
	    model.addAttribute("user", userDto);
	    return "registration";
	}
	 @RequestMapping(value = "/user/registration", method = RequestMethod.POST)
	    @ResponseBody
	    public GenericResponse registerUserAccount(@Valid final UserDto accountDto, final HttpServletRequest request) {
	       // LOGGER.debug("Registering user account with information: {}", accountDto);

	        final User registered = userService.registerNewUserAccount(accountDto);
	      //  eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
	        return new GenericResponse("success");
	    }
	 
	 
	  @RequestMapping(value = "/registrationConfirm", method = RequestMethod.GET)
	    public String confirmRegistration(final HttpServletRequest request, final Model model, @RequestParam("token") final String token) throws UnsupportedEncodingException {
	        Locale locale = request.getLocale();
	        final String result = userService.validateVerificationToken(token);
	        if (result.equals("valid")) {
	            final User user = userService.getUser(token);
	            // if (user.isUsing2FA()) {
	            // model.addAttribute("qr", userService.generateQRUrl(user));
	            // return "redirect:/qrcode.html?lang=" + locale.getLanguage();
	            // }
	            authWithoutPassword(user);
	            model.addAttribute("message", messages.getMessage("message.accountVerified", null, locale));
	            return "redirect:/console.html?lang=" + locale.getLanguage();
	        }

	        model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
	        model.addAttribute("expired", "expired".equals(result));
	        model.addAttribute("token", token);
	        return "redirect:/badUser.html?lang=" + locale.getLanguage();
	    }
	  
	  public void authWithoutPassword(User user) {
	        List<Privilege> privileges = user.getRoles().stream().map(role -> role.getPrivileges()).flatMap(list -> list.stream()).distinct().collect(Collectors.toList());
	        List<GrantedAuthority> authorities = privileges.stream().map(p -> new SimpleGrantedAuthority(p.getName())).collect(Collectors.toList());

	        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);

	        SecurityContextHolder.getContext().setAuthentication(authentication);
	    }
	 
}
