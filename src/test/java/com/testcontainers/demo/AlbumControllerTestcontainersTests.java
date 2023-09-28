package com.testcontainers.demo;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.runtime.server.EmbeddedServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

@Testcontainers(disabledWithoutDocker = true)
class AlbumControllerTestcontainersTests {

    @Container
    static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:2.35.0")
            .withMappingFromResource("mocks-config.json")
            .withFileFromResource("album-photos-response.json");

    @NonNull public Map<String, Object> getProperties() {
        return Collections.singletonMap("micronaut.http.services.photosapi.url", wiremockServer.getBaseUrl());
    }

    @Test
    void shouldGetAlbumById() {
        Long albumId = 1L;
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();

            given().contentType(ContentType.JSON)
                    .when()
                    .get("/api/albums/{albumId}", albumId)
                    .then()
                    .statusCode(200)
                    .body("albumId", is(albumId.intValue()))
                    .body("photos", hasSize(2));
        }
    }

    @Test
    void shouldReturnServerErrorWhenPhotoServiceCallFailed() {
        Long albumId = 2L;
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();
            given().contentType(ContentType.JSON)
                    .when()
                    .get("/api/albums/{albumId}", albumId)
                    .then()
                    .statusCode(500);
        }
    }

    @Test
    void shouldReturnEmptyPhotos() {
        Long albumId = 3L;
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, getProperties())) {
            RestAssured.port = server.getPort();
            given().contentType(ContentType.JSON)
                    .when()
                    .get("/api/albums/{albumId}", albumId)
                    .then()
                    .statusCode(200)
                    .body("albumId", is(albumId.intValue()))
                    .body("photos", nullValue());
        }
    }
}
