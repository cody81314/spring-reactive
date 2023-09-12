package com.example.demo.service;

import com.example.demo.dto.PersonDTO;
import com.example.demo.po.Person;
import com.example.demo.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class DemoService {
    private final Logger LOGGER = LoggerFactory.getLogger(DemoService.class);
    private final PersonRepository personRepository;

    @Transactional
    public Mono<PersonDTO> addPerson(String name, int age) {
        LOGGER.info("Create new Person PO.");
        Person addedPerson = Person.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .age(age)
                .build();

        LOGGER.info("Insert person to DB.");

        return personRepository.save(addedPerson)
                .map(PersonDTO::createBy);
    }

    public Flux<PersonDTO> listPerson() {
        return personRepository.findAll()
                .map(PersonDTO::createBy);
    }
}
