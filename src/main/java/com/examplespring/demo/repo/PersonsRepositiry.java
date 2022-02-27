package com.examplespring.demo.repo;

import com.examplespring.demo.models.persons;
import org.springframework.data.repository.CrudRepository;

public interface PersonsRepositiry extends CrudRepository<persons, Long> {

}
