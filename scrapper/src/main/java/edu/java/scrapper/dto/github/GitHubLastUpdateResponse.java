package edu.java.scrapper.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GitHubLastUpdateResponse(
    @JsonProperty("name") String name,
    @JsonProperty("pushed_at") OffsetDateTime pushedAt
) {}
