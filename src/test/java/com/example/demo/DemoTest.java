package com.example.demo;

import com.example.demo.controller.DemoController;
import com.example.demo.dto.PersonDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DemoTest {
    @Autowired
    DemoController demoController;

    WebTestClient webTestClient;
    PersonDTO persistPerson;

    @BeforeAll
    void initTest() {
        this.webTestClient = WebTestClient.bindToController(demoController)
                .build();
    }

    @Test
    void addPerson() {
        PersonDTO personDTO = PersonDTO.builder()
                .name("Vic")
                .age(31)
                .build();

        this.webTestClient
                .post()
                .uri("/demo/person")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(personDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PersonDTO.class).consumeWith(result -> {
                    PersonDTO responseBody = result.getResponseBody();
                    assertThat(responseBody.getName()).isEqualTo(personDTO.getName());
                    assertThat(responseBody.getAge()).isEqualTo(personDTO.getAge());
                    this.persistPerson = responseBody;
                });
    }

    @Test
    void listPerson() {
        List<PersonDTO> expectedResult = new ArrayList<>();
        expectedResult.add(this.persistPerson);

        this.webTestClient
                .get()
                .uri("/demo/person")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PersonDTO.class).isEqualTo(expectedResult);
    }

    @Test
    void httpClientExample() {

        this.webTestClient
                .get()
                .uri("/demo/pet/10")
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().is2xxSuccessful()
                .expectBody().json("{\"id\":10,\"category\":{\"id\":1,\"name\":\"Dogs\"},\"name\":\"doggie\",\"photoUrls\":[\"string\"],\"tags\":[{\"id\":0,\"name\":\"string\"}],\"status\":\"string\"}", true);

    }
}
