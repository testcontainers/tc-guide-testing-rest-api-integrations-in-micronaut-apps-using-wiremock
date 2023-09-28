package com.testcontainers.demo;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class AlbumControllerWireMockMappingTests {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort().usingFilesUnderClasspath("wiremock"))
            .build();

    private Map<String, Object> getProperties() {
        return Collections.singletonMap("micronaut.http.services.photosapi.url", wireMock.baseUrl());
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
}
