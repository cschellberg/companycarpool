package com.eligaapps.companycarpool.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.eligaapps.companycarpool.model.OrgEvent;
import com.eligaapps.companycarpool.model.Organization;
import com.eligaapps.companycarpool.model.Person;

@Repository
public interface OrgEventRepository extends CrudRepository<OrgEvent, Long>{
	OrgEvent findByName(String name);
}
