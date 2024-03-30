package edu.java.scrapper.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

public record StackOverFlowAnswersResponse(
    @JsonProperty("items") List<Answer> answers
) {
    public record Answer(
        @JsonProperty("answer_id") long answerId,
        @JsonProperty("creation_date") OffsetDateTime creationDate
    ) {}
}
