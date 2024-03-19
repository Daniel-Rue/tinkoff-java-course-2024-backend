package edu.java.scrapper.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

public record StackOverFlowQuestionLastActivityResponse(
    @JsonProperty("items") List<Question> questions
) {
    public record Question(
        @JsonProperty("question_id") long questionId,
        @JsonProperty("last_activity_date") OffsetDateTime lastActivityDate
    ) {}
}
