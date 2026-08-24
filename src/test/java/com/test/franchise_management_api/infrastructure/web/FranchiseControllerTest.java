package com.test.franchise_management_api.infrastructure.web;

import com.test.franchise_management_api.application.dto.FranchiseResponse;
import com.test.franchise_management_api.application.dto.ProductResponse;
import com.test.franchise_management_api.application.usecase.FranchiseUseCase;
import com.test.franchise_management_api.application.usecase.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FranchiseControllerTest {

    private WebTestClient webTestClient;
    private FranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = mock(FranchiseUseCase.class);
        FranchiseController controller = new FranchiseController(useCase);
        webTestClient = WebTestClient
                .bindToController(controller)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateFranchise() {
        when(useCase.createFranchise("Acme")).thenReturn(Mono.just(new FranchiseResponse("65f1a9e6a0c1f4d3b8a77a11", "Acme")));

        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Acme\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Acme");
    }

    @Test
    void shouldReturnBadRequestForInvalidData() {
        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void shouldReturnBadRequestForNegativeStock() {
        webTestClient.patch()
                .uri("/api/v1/franchises/65f1a9e6a0c1f4d3b8a77a11/branches/65f1a9e6a0c1f4d3b8a77a12/products/65f1a9e6a0c1f4d3b8a77a13/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"stock\":-1}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnNotFoundWhenDomainEntityMissing() {
        when(useCase.updateProductStock(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Mono.error(new NotFoundException("Product not found")));

        webTestClient.patch()
                .uri("/api/v1/franchises/65f1a9e6a0c1f4d3b8a77a11/branches/65f1a9e6a0c1f4d3b8a77a12/products/65f1a9e6a0c1f4d3b8a77a13/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"stock\":5}")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("NOT_FOUND");
    }

    @Test
    void shouldUpdateStock() {
        when(useCase.updateProductStock(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(new ProductResponse(
                        "65f1a9e6a0c1f4d3b8a77a13",
                        "65f1a9e6a0c1f4d3b8a77a11",
                        "65f1a9e6a0c1f4d3b8a77a12",
                        "Widget",
                        21
                )));

        webTestClient.patch()
                .uri("/api/v1/franchises/65f1a9e6a0c1f4d3b8a77a11/branches/65f1a9e6a0c1f4d3b8a77a12/products/65f1a9e6a0c1f4d3b8a77a13/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"stock\":21}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock").isEqualTo(21);
    }

    @Test
    void shouldReturnEmptyWhenNoMaxStockProducts() {
        when(useCase.getMaxStockProductsByBranch(anyString())).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/v1/franchises/65f1a9e6a0c1f4d3b8a77a11/products/max-stock")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(0);
    }
}
