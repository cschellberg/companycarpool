package com.eligaapps.companycarpool.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.eligaapps.companycarpool.model.Person;
import com.eligaapps.companycarpool.repository.UserRepository;
import com.google.gson.Gson;

@Component
public class CarpoolSuccessfulAuthenticationHandler extends SimpleUrlAuthenticationSuccessHandler{

	@Autowired
	private UserRepository carpoolRepository;

	@Autowired
	private DOSDetector dosDetector;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		Person person = carpoolRepository.findByEmail(authentication.getPrincipal().toString());
		dosDetector.clearIP(request.getRemoteAddr());
		person.setPassword(null);
		response.setStatus(200);
		Gson gson=new Gson();
		String responseStr=gson.toJson(person);
		response.getOutputStream().write(responseStr.getBytes());
	}
	
	

}
