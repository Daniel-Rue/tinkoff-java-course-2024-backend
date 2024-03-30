package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.configuration.client.StackOverflowConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@WireMockTest(httpPort = 8089)
public class StackOverflowClientTest {

    private StackOverflowClient stackOverflowClient;

    @BeforeEach
    public void setUp() {
        StackOverflowConfig stackOverflowConfig = Mockito.mock(StackOverflowConfig.class);
        Mockito.when(stackOverflowConfig.getBaseUrl()).thenReturn("http://localhost:8089");
        stackOverflowClient = new StackOverflowClient(stackOverflowConfig);
    }

    @Test
    void fetchQuestionLastActivityShouldReturnDataOnSuccess() {
        // Мокирование успешного ответа
        stubFor(get(urlPathEqualTo("/questions/123456"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody("{\"items\": [{\"question_id\": 123456, \"last_activity_date\": 1609459200}]}")));

        StepVerifier.create(stackOverflowClient.fetchQuestionLastActivity(123456))
            .expectNextMatches(response ->
                response.questions().size() == 1 &&
                response.questions().get(0).questionId() == 123456)
            .verifyComplete();
    }

    @Test
    void fetchNewAnswersShouldReturnDataOnSuccess() {
        stubFor(get(urlPathMatching("/questions/123456/answers"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody("{\"items\": [{\"answer_id\": 654321, \"creation_date\": 1609459200}]}")));

        StepVerifier.create(stackOverflowClient.fetchNewAnswers(123456))
            .expectNextMatches(response ->
                response.answers().size() == 1 &&
                response.answers().get(0).answerId() == 654321)
            .verifyComplete();
    }

}
