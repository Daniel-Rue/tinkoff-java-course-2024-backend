package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.configuration.GitHubConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;
import java.time.OffsetDateTime;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest
@WireMockTest(httpPort = 8089)
public class GitHubClientTest {

    @Autowired
    private GitHubClient gitHubClient;

    @BeforeEach
    public void setUp() {
        GitHubConfig gitHubConfig = Mockito.mock(GitHubConfig.class);
        Mockito.when(gitHubConfig.getBaseUrl()).thenReturn("http://localhost:8089");
        gitHubClient = new GitHubClient(gitHubConfig);
    }

    @Test
    void fetchRepoLastUpdatedShouldReturnLastUpdateOnSuccess() {
        stubFor(get(urlEqualTo("/repos/user/success-repo"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withStatus(200)
                .withBody("{\"name\": \"success-repo\", \"pushed_at\": \"2020-01-01T00:00:00Z\"}")));

        StepVerifier.create(gitHubClient.fetchRepoLastUpdated("user", "success-repo"))
            .expectNextMatches(lastUpdateResponse ->
                "success-repo".equals(lastUpdateResponse.name()) &&
                OffsetDateTime.parse("2020-01-01T00:00:00Z").isEqual(lastUpdateResponse.pushedAt()))
            .verifyComplete();
    }

    @Test
    void fetchRepoLastUpdatedShouldHandleErrorGracefully() {
        stubFor(get(urlEqualTo("/repos/user/error-repo"))
            .willReturn(aResponse()
                .withStatus(404)));

        StepVerifier.create(gitHubClient.fetchRepoLastUpdated("user", "error-repo"))
            .verifyComplete();
    }
}
