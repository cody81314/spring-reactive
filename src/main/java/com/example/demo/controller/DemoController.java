package com.example.demo.controller;

import com.example.demo.dto.PersonDTO;
import com.example.demo.error.DemoException;
import com.example.demo.service.DemoService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/demo")
@AllArgsConstructor
public class DemoController {
    private final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);
    private final WebClient petStoreClient;
    private final DemoService demoService;

    @GetMapping("/{userId}")
    public Mono<String> getUser(@PathVariable Long userId) {
        return Mono.just("Hello World!");
    }

    @GetMapping(value = "/{userId}/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<String>> getUserCustomers(@PathVariable Long userId) {
        throw new DemoException(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{userId}")
    public Mono<Void> deleteUser(@PathVariable Long userId) {
        return Mono.empty();
    }

    @GetMapping(value = "/pet/{petId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Object> getPetById(@PathVariable Long petId) {
        LOGGER.info("Get Pet by id: {}", petId);
        return this.petStoreClient.get()
                .uri("/pet/{petId}", petId)
                .retrieve()
                .bodyToMono(Object.class);
    }

    @PostMapping("/person")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PersonDTO> addPerson(@RequestBody PersonDTO personDTO) {
        LOGGER.info("Add person: {}", personDTO);
        return demoService.addPerson(personDTO.getName(), personDTO.getAge());
    }

    @GetMapping("/person")
    public Flux<PersonDTO> listPerson() {
        return demoService.listPerson();
    }
}
