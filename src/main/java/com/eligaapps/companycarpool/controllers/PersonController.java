package com.eligaapps.companycarpool.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.security.PermitAll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eligaapps.companycarpool.model.*;
import com.eligaapps.companycarpool.repository.UserRepository;
import com.eligaapps.companycarpool.security.CryptoConverter;

@Controller
public class PersonController {
	private static Logger logger = LoggerFactory.getLogger(PersonController.class);

	@Autowired
	private UserRepository carpoolRepository;

	@Autowired
	private CryptoConverter cryptoConverter;

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
