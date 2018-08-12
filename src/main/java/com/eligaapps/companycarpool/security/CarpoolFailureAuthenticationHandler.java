package com.eligaapps.companycarpool.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class CarpoolFailureAuthenticationHandler extends SimpleUrlAuthenticationFailureHandler{

	@Autowired 
	private DOSDetector dosDetector;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		response.setStatus(406);
		if ( dosDetector.hasExceededLimitsIncr(request.getRemoteAddr())){
			response.setStatus(406);
		}
	}
	
	

}
