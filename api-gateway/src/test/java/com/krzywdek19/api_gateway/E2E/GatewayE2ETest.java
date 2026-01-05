package com.krzywdek19.api_gateway.E2E;

import com.krzywdek19.api_gateway.dto.LoginRequest;
import com.krzywdek19.api_gateway.dto.RegisterRequest;
import com.krzywdek19.api_gateway.dto.TokenResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;


import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Tag("e2e")
class GatewayE2ETest {

    private String email = "";
    private final String PASSWORD = "password123";

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        email = "testuser+" + System.currentTimeMillis() + "@example.com";
        var registerRequest = new RegisterRequest(email, PASSWORD);

        webTestClient.post()
                .uri("/api/v1/auth/register")
                .bodyValue(registerRequest)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void fullAuthFlowShouldWork() {
        var login = new LoginRequest(email, PASSWORD);

        var token = Objects.requireNonNull(webTestClient.post()
                        .uri("/api/v1/auth/login")
                        .bodyValue(login)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(TokenResponse.class)
                        .returnResult()
                        .getResponseBody())
                .accessToken();

        webTestClient.get()
                .uri("/api/v1/workouts/test")
                .headers(h -> h.setBearerAuth(token))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deletedUserShouldNotAccessSecuredEndpoint() {
        var login = new LoginRequest(email, PASSWORD);

        var token = Objects.requireNonNull(webTestClient.post()
                        .uri("/api/v1/auth/login")
                        .bodyValue(login)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(TokenResponse.class)
                        .returnResult()
                        .getResponseBody())
                .accessToken();

        webTestClient.delete()
                .uri("/api/v1/users/me")
                .headers(h -> h.setBearerAuth(token))
                .exchange()
                .expectStatus().is2xxSuccessful();

        webTestClient.get()
                .uri("/api/v1/workouts/test")
                .headers(h -> h.setBearerAuth(token))
                .exchange()
                .expectStatus().isForbidden();
    }
}
