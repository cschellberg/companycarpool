package com.eligaapps.companycarpool.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.security.PermitAll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eligaapps.companycarpool.model.*;
import com.eligaapps.companycarpool.repository.UserRepository;
import com.eligaapps.companycarpool.security.CryptoConverter;
import com.eligaapps.companycarpool.types.ROLE;
import com.eligaapps.companycarpool.utils.MailUtils;
import com.eligaapps.companycarpool.utils.PasswordGenerator;

@Controller
public class PersonController {
	private static Logger logger = LoggerFactory.getLogger(PersonController.class);

	public static String TEST_ACTIVATION_KEY="4481186319513391";
			
	@Autowired
	JavaMailSender javaMailSender; 
	
	@Autowired
	private UserRepository carpoolRepository;

	@Autowired
	private CryptoConverter cryptoConverter;
	
	@Autowired
	private PasswordGenerator passwordGenerator;
	
	@Value("${carpool.host}")
	private String carpoolHost;
	
	@Value("${carpool.port}")
	private int carpoolPort;

	@Value("${test.email}")
	private String testEmail;
	
	@Value("${test.password}")
	private String testPassword;
	
	@RequestMapping(value = "/admin/person/list", method = RequestMethod.GET)
	public @ResponseBody List<Person> list() {
		List<Person> retList = new ArrayList<Person>();
		Iterator<Person> it = carpoolRepository.findAll().iterator();
		while (it.hasNext()) {
			retList.add(it.next());
		}
		return retList;
	}

	@RequestMapping(value = "/admin/person/{email:.+}", method = RequestMethod.GET)
	public @ResponseBody Person get(@PathVariable("email") String email) {
		Person person = carpoolRepository.findByEmail(email);
		return person;
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public @ResponseBody Result changePassord(@RequestParam String email, @RequestParam String currentPassword,
			@RequestParam String newPassword) throws Exception {
		String encyrptedCurrentPassword = cryptoConverter.convertToDatabaseColumn(currentPassword);
		Person person = carpoolRepository.findByEmailAndPassword(email, encyrptedCurrentPassword);
		if (person == null) {
			return new Result(1, "incorrect password");
		} else {
			String encyrptedNewPassword = cryptoConverter.convertToDatabaseColumn(newPassword);
		    person.setPassword(encyrptedNewPassword);
		    carpoolRepository.save(person);
			return new Result(0, "success");
		}
	}
	
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
	public @ResponseBody Result forgotPassord(@RequestParam String email) throws Exception {
		String generatedPassword=passwordGenerator.generatePassword();
		if ( email.equals(testEmail)){
			generatedPassword=testPassword;
		}
		String encyrptedGeneratedPassword = cryptoConverter.convertToDatabaseColumn(generatedPassword);
		Person person = carpoolRepository.findByEmail(email);
		if (person != null) {
			person.setPassword(encyrptedGeneratedPassword);
			javaMailSender.send(MailUtils.getForgotPasswordMessage( person.getEmail(), 
					generatedPassword));
		    carpoolRepository.save(person);
			
		}
		return new Result(0, "If your email matches our records we will send you a temporary password");
	}
	
	@RequestMapping(value = "/activate", method = RequestMethod.GET)
	public @ResponseBody Result activate(@RequestParam String email, @RequestParam String activationKey) throws Exception {
		Person person = carpoolRepository.findByEmailAndActivationKey(email, activationKey);
		if (person == null) {
			return new Result(1, "activation incorrect key");
		} else {
		    person.setActive(true);
		    carpoolRepository.save(person);
			return new Result(0, "success");
		}
	}

	@RequestMapping(value = "/person", method = RequestMethod.POST)
	public @ResponseBody Result saveUser(@RequestBody Person person) throws Exception {
		try {
			encryptPassword(person);
			carpoolRepository.save(person);
			return new Result(0, "success");
		} catch (Exception ex) {
			return new Result(1, ex.getMessage());
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public @ResponseBody Result registerUser(@RequestBody Person person) throws Exception {
		try {
			encryptPassword(person);
			if ( person.getRole() == null){
				person.setRole(ROLE.user);
			}
			if ( person.getEmail().equals(testEmail)){
				person.setActivationKey(TEST_ACTIVATION_KEY);
			} else {
				UUID uuid = UUID.randomUUID();
		        String randomUUIDString = uuid.toString();
				person.setActivationKey(randomUUIDString);
			}
			javaMailSender.send(MailUtils.getActivationMessage(carpoolHost, carpoolPort, person.getEmail(), 
					person.getActivationKey()));
			carpoolRepository.save(person);
			return new Result(0, "success");
		} catch (Exception ex) {
			return new Result(1, ex.getMessage());
		}
	}

	private void encryptPassword(Person person) {
		person.setPassword(cryptoConverter.convertToDatabaseColumn(person.getPassword()));
	}

}
