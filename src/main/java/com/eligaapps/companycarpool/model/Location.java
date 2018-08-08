package com.eligaapps.companycarpool.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="location")
public class Location {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long locationId;
    
    @NotNull
    private String street;
    
    @NotNull
    private String state;
    
    @NotNull
    private String city;
    
    @NotNull
    private String country;
    
    private Double latitude;
    
    private Double longitude;


	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		return "Location [id=" + locationId + ", street=" + street + ", state=" + state + ", country=" + country + ", latitude="
				+ latitude + ", longitude=" + longitude + "]";
	}
    
	
}
