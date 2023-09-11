package com.example.demo.controller;

import com.example.demo.dto.PersonDTO;
import com.example.demo.error.DemoException;
import com.example.demo.service.DemoService;
import lombok.AllArgsConstructor;
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
    private final WebClient petStoreClient;
    private final DemoService demoService;

    @GetMapping("/{userId}")
    public Mono<String> getUser(@PathVariable Long userId) {
        return Mono.just("Hello World!");
    }

    @GetMapping(value = "/{userId}/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<String>> getUserCustomers(@PathVariable Long userId) {
        throw new DemoException(HttpStatusCode.valueOf(400));
    }

    @DeleteMapping("/{userId}")
    public Mono<Void> deleteUser(@PathVariable Long userId) {
        return Mono.empty();
    }

    @GetMapping(value = "/pet/{petId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Object> getPetById(@PathVariable Long petId) {
        return this.petStoreClient.get()
                .uri("/pet/{petId}", petId)
                .retrieve()
                .bodyToMono(Object.class);
    }

    @PostMapping("/person")
    public Mono<PersonDTO> addPerson(@RequestBody PersonDTO personDTO) {
        return demoService.addPerson(personDTO.getName(), personDTO.getAge());
    }

    @GetMapping("/person")
    public Flux<PersonDTO> listPerson() {
        return demoService.listPerson();
    }
}
