package com.krzywdek19.api_gateway.E2E;

import com.krzywdek19.api_gateway.dto.LoginRequest;
import com.krzywdek19.api_gateway.dto.RegisterRequest;
import com.krzywdek19.api_gateway.dto.TokenResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("e2e")
public class GatewayE2ETest {
    private final String EMAIL = "test@test.com";
    private final String PASSWORD = "password123";

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        var registerRequest = new RegisterRequest(EMAIL, PASSWORD);

        restTemplate.postForEntity("/api/v1/auth/register", registerRequest, Void.class);
    }

    @AfterEach
    void tearDown() {
        var login = new LoginRequest(EMAIL, PASSWORD);

        var loginResp = restTemplate.postForEntity("/api/v1/auth/login", login, TokenResponse.class);
        if (!loginResp.getStatusCode().is2xxSuccessful() || loginResp.getBody() == null) {
            return;
        }

        var token = loginResp.getBody().accessToken();

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        restTemplate.exchange(
                "/api/v1/users/me",
                HttpMethod.DELETE,
                new HttpEntity<>(null, headers),
                Void.class
        );
    }

    @Test
    void fullAuthFlowShouldWork() {
        var login = new LoginRequest(EMAIL, PASSWORD);

        var tokenResponse = restTemplate.postForEntity("/api/v1/auth/login", login, TokenResponse.class);
        assertThat(tokenResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tokenResponse.getBody()).isNotNull();

        var token = tokenResponse.getBody().accessToken();

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        var entity = new HttpEntity<Void>(null, headers);

        var response = restTemplate.exchange("/api/v1/workouts/test", HttpMethod.GET, entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deletedUserShouldNotAccessSecuredEndpoint() {
        var login = new LoginRequest(EMAIL, PASSWORD);

        var loginResp = restTemplate.postForEntity("/api/v1/auth/login", login, TokenResponse.class);
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResp.getBody()).isNotNull();

        var token = loginResp.getBody().accessToken();

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        var entity = new HttpEntity<Void>(null, headers);

        restTemplate.exchange("/api/v1/users/me", HttpMethod.DELETE, entity, Void.class);

        var response = restTemplate.exchange("/api/v1/workouts/test", HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
