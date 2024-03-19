package edu.java.scrapper.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record GitHubCommitResponse(
    @JsonProperty("sha") String sha,
    @JsonProperty("commit") Commit commit
) {
    public record Commit(
        @JsonProperty("committer") Committer committer
    ) {
        public record Committer(
            @JsonProperty("date") OffsetDateTime date
        ) {
        }
    }
}
