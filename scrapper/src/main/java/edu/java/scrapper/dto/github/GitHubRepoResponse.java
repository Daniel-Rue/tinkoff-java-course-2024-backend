package edu.java.scrapper.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GitHubRepoResponse(
    @JsonProperty("name") String name,
    @JsonProperty("updated_at") OffsetDateTime updatedAt
) {}
