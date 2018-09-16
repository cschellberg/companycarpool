package com.eligaapps.companycarpool.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eligaapps.companycarpool.model.OrgEvent;
import com.eligaapps.companycarpool.model.Organization;
import com.eligaapps.companycarpool.model.Person;
import com.eligaapps.companycarpool.model.Result;
import com.eligaapps.companycarpool.repository.OrgEventRepository;
import com.eligaapps.companycarpool.repository.OrganizationRepository;
import com.eligaapps.companycarpool.repository.UserRepository;

@Controller
public class OrganizationController {

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private OrgEventRepository orgEventRepository;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping(value = "/organization", method = RequestMethod.POST)
	public @ResponseBody Result saveOrganization(@RequestBody Organization organization) throws Exception {
		try {
			organizationRepository.save(organization);
			return new Result(0, "success");
		} catch (Exception ex) {
			return new Result(1, ex.getMessage());
		}
	}

	@RequestMapping(value = "/orgEvent", method = RequestMethod.POST)
	public @ResponseBody Result saveOrgEvent(@RequestBody OrgEvent orgEvent) throws Exception {
		try {
			Optional<Organization> optional = organizationRepository.findById(orgEvent.getOrganization().getId());
			if (optional.isPresent()) {
				Organization organization = optional.get();
				orgEvent.setOrganization(organization);
				organization.getEvents().add(orgEvent);
				organizationRepository.save(organization);
				return new Result(0, "success");
			} else {
				return new Result(2, "Unable to find organization");
			}

		} catch (Exception ex) {
			return new Result(1, ex.getMessage());
		}
	}

	@RequestMapping(value = "/addRideRequest", method = RequestMethod.POST)
	public @ResponseBody Result addRideRequest(@RequestParam Long orgEventId, @RequestParam String email) {
		try {
			Optional<OrgEvent> optOrgEvent = orgEventRepository.findById(orgEventId);
			if (optOrgEvent.isPresent()) {
				OrgEvent orgEvent = optOrgEvent.get();
				Person person = userRepository.findByEmail(email);
				if (person != null) {
					if (orgEvent.addRideRequest(person)) {
						orgEventRepository.save(orgEvent);
					}
				}
			}
			return new Result(0, "success");
		} catch (Exception ex) {
			return new Result(1, ex.getMessage());
		}
	}

	@RequestMapping(value = "/offerRide", method = RequestMethod.POST)
	public @ResponseBody Result offerRide(@RequestParam Long orgEventId, @RequestParam String driverEmail, @RequestParam String passengerEmail) {
		try {
			Optional<OrgEvent> optOrgEvent = orgEventRepository.findById(orgEventId);
			if (optOrgEvent.isPresent()) {
				OrgEvent orgEvent = optOrgEvent.get();
				Person driver = userRepository.findByEmail(driverEmail);
				Person passenger = userRepository.findByEmail(passengerEmail);
				if (driver != null && passenger != null) {
					orgEvent.offerRide(driver, passenger);
				    orgEventRepository.save(orgEvent);
				}
			}
			return new Result(0, "success");
		} catch (Exception ex) {
			return new Result(1, ex.getMessage());
		}
	}

	
	@RequestMapping(value = "/cancelOffer", method = RequestMethod.POST)
	public @ResponseBody Result cancelOffer(@RequestParam Long orgEventId, @RequestParam String driverEmail) {
		try {
			Optional<OrgEvent> optOrgEvent = orgEventRepository.findById(orgEventId);
			if (optOrgEvent.isPresent()) {
				OrgEvent orgEvent = optOrgEvent.get();
				Person driver = userRepository.findByEmail(driverEmail);
				if (driver != null) {
					orgEvent.cancelOffer(driver);
				    orgEventRepository.save(orgEvent);
				}
			}
			return new Result(0, "success");
		} catch (Exception ex) {
			return new Result(1, ex.getMessage());
		}
	}
	
	@RequestMapping(value = "/cancelRequest", method = RequestMethod.POST)
	public @ResponseBody Result cancelRequest(@RequestParam Long orgEventId, @RequestParam String passengerEmail) {
		try {
			Optional<OrgEvent> optOrgEvent = orgEventRepository.findById(orgEventId);
			if (optOrgEvent.isPresent()) {
				OrgEvent orgEvent = optOrgEvent.get();
				Person passenger= userRepository.findByEmail(passengerEmail);
				if (passenger != null) {
					orgEvent.cancelRequest(passenger);
				    orgEventRepository.save(orgEvent);
				}
			}
			return new Result(0, "success");
		} catch (Exception ex) {
			return new Result(1, ex.getMessage());
		}
	}

	
	
	@RequestMapping(value = "/orgEvent/{id:.+}", method = RequestMethod.DELETE)
	public @ResponseBody Result deleteOrgEvent(@PathVariable("id") Long id) {
		try {
			Optional<OrgEvent> optOrgEvent = orgEventRepository.findById(id);
			if (optOrgEvent.isPresent()) {
				OrgEvent orgEvent = optOrgEvent.get();
				Organization organization = orgEvent.getOrganization();
				organization.removeEvent(orgEvent);
				organizationRepository.save(organization);
			}
			return new Result(0, "success");
		} catch (Exception ex) {
			return new Result(1, ex.getMessage());
		}
	}

	@RequestMapping(value = "/organization/{name:.+}", method = RequestMethod.GET)
	public @ResponseBody Organization get(@PathVariable("name") String name) throws UnsupportedEncodingException {
		name = URLDecoder.decode(name, "UTF-8");
		Organization organization = organizationRepository.findByName(name);
		return organization;
	}
}
