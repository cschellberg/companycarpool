package com.eligaapps.companycarpool.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DOSFilter implements Filter {

	@Autowired
	private DOSDetector dosDetector;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String realPath = httpRequest.getRequestURI().toString().toLowerCase();
		if (realPath.contains("register")) {
			if (dosDetector.hasExceededLimitsIncr(request.getRemoteAddr())) {
				System.out.println("limit exceed");
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.setStatus(406);
			}else{
				chain.doFilter(request, response);
			}
		} else if (dosDetector.hasExceededLimits(request.getRemoteAddr())) {
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.setStatus(406);
		} else {
			chain.doFilter(request, response);
		}

	}

	@Override
	public void destroy() {
	}

}
