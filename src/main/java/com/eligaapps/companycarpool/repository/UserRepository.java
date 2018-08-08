package com.eligaapps.companycarpool.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.eligaapps.companycarpool.model.Person;



@Repository
public interface UserRepository extends CrudRepository<Person, Long> {
	Optional<Person> findById(Long id);
	Person findByEmailAndPassword(String email, String password);
	Person findByEmail(String email);
}