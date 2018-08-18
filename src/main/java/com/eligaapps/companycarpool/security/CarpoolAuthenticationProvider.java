package com.eligaapps.companycarpool.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.eligaapps.companycarpool.model.Person;
import com.eligaapps.companycarpool.repository.UserRepository;


@Service
public class CarpoolAuthenticationProvider implements AuthenticationProvider {

	private static Logger logger = (Logger) LoggerFactory.getLogger(CarpoolAuthenticationProvider.class);

	@Autowired
	private CryptoConverter cryptoConverter;
	
	@Autowired
	private UserRepository userRepository;
	

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String name = authentication.getName();
		String password = cryptoConverter.convertToDatabaseColumn(authentication.getCredentials().toString());
		Person person = userRepository.findByEmailAndPassword(name, password);
		if (person != null && person.isActive()) {
			return createToken(name, password, person.getRole().toString());
		}else {
			return null;
		}
	}
	
	public Authentication createToken(String name, String password, String role) {
		List<GrantedAuthority> grantedAuths = new ArrayList<>();
		grantedAuths.add(new SimpleGrantedAuthority(role));
		Authentication auth = new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
		return auth;
	}


	@Override
	public boolean supports(Class<?> authentication) {
		logger.info("supports {}", authentication);
		return true;
	}
}

