package com.testcontainers.demo;

import io.micronaut.serde.annotation.Serdeable;
import java.util.List;

@Serdeable
public record Album(Long albumId, List<Photo> photos) {}

@Serdeable
record Photo(Long id, String title, String url, String thumbnailUrl) {}
