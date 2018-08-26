package com.eligaapps.companycarpool.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.eligaapps.companycarpool.model.Organization;
import com.eligaapps.companycarpool.model.Person;

@Repository
public interface OrganizationRepository extends CrudRepository<Organization, Long>{
	Organization findByName(String name);
}
