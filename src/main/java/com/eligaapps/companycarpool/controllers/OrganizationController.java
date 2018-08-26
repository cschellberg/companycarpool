package com.eligaapps.companycarpool.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eligaapps.companycarpool.model.Organization;
import com.eligaapps.companycarpool.model.Result;
import com.eligaapps.companycarpool.repository.OrganizationRepository;

@Controller
public class OrganizationController {
	
	@Autowired
	private OrganizationRepository organizationRepository;
	
	@RequestMapping(value = "/organization", method = RequestMethod.POST)
	public @ResponseBody Result saveUser(@RequestBody Organization organization) throws Exception {
		try {
			organizationRepository.save(organization);
			return new Result(0, "success");
		} catch (Exception ex) {
			return new Result(1, ex.getMessage());
		}
	}


	@RequestMapping(value = "/organization/{name:.+}", method = RequestMethod.GET)
	public @ResponseBody Organization get(@PathVariable("name") String name) throws UnsupportedEncodingException {
		name=URLDecoder.decode(name, "UTF-8");
		Organization organization = organizationRepository.findByName(name);
		return organization;
	}
}
