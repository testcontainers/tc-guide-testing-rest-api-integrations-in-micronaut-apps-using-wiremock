package com.testcontainers.demo;

import static io.micronaut.scheduling.TaskExecutors.BLOCKING;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.scheduling.annotation.ExecuteOn;

@Controller("/api")
class AlbumController {

    private final PhotoServiceClient photoServiceClient;

    AlbumController(PhotoServiceClient photoServiceClient) {
        this.photoServiceClient = photoServiceClient;
    }

    @ExecuteOn(BLOCKING)
    @Get("/albums/{albumId}")
    public Album getAlbumById(@PathVariable Long albumId) {
        return new Album(albumId, photoServiceClient.getPhotos(albumId));
    }
}
