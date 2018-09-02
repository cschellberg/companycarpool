package com.eligaapps.companycarpool.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="ride")
public class Ride {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
    
	@OneToOne(fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name = "personId")
    private Person driver;
    

	@OneToMany(fetch = FetchType.EAGER,cascade=CascadeType.ALL, orphanRemoval = true)
    private List<Person> passengers;
    
	public Ride(){
		
	}
	
	public Ride(Person driver, Person passenger) {
		this.driver=driver;
		this.passengers.add(passenger);
	}
	public Person getDriver() {
		return driver;
	}
	public void setDriver(Person driver) {
		this.driver = driver;
	}
	
	public List<Person> getPassengers() {
		return passengers;
	}
	public void setPassengers(List<Person> passengers) {
		this.passengers = passengers;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public boolean hasPassengers(Person person) {
		for ( Person tmpPerson:passengers){
			if ( person.getEmail().equals(tmpPerson.getEmail())){
				return true;
			}
		}
		return false;
	}
    
}
