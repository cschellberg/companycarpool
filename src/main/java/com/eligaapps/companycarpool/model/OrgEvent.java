package com.eligaapps.companycarpool.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "org_event")
public class OrgEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String name;

	private String description;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	private Organization organization;

	@NotNull
	private Date time;

	@NotNull
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "locationId")
	private Location location;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<Ride> rides;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	private List<Person> rideRequests;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonIgnore
	public Organization getOrganization() {
		return organization;
	}

	@JsonProperty
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<Ride> getRides() {
		return rides;
	}

	public void setRides(List<Ride> rides) {
		this.rides = rides;
	}

	public List<Person> getRideRequests() {
		return rideRequests;
	}

	public void setRideRequests(List<Person> rideRequests) {
		this.rideRequests = rideRequests;
	}

	public boolean addRideRequest(Person person) {
		for (Person tmpPerson : rideRequests) {
			if (person.getPersonId().equals(tmpPerson.getPersonId())) {
				return false;
			}
		}
		for (Ride ride : rides) {
			if (ride.getDriver().getPersonId().equals(person.getPersonId())) {
				return false;
			}
			for (Person tmpPerson : ride.getPassengers()) {
				if (tmpPerson.getPersonId().equals(person.getPersonId())) {
					return false;
				}
			}
		}
		rideRequests.add(person);
		return true;
	}

	public void offerRide(Person driver, Person passenger) {
		Ride ride = null;
		for (Ride tmpRide : rides) {
			if (tmpRide.getDriver().getEmail().equals(driver.getEmail())) {
				ride = tmpRide;
				if (!ride.hasPassengers(passenger)) {
					ride.getPassengers().add(passenger);
				}
				break;
			}
		}
		if (ride == null) {
			ride = new Ride(driver, passenger);
			rides.add(ride);
		}
		this.rideRequests.remove(passenger);
	}

}
