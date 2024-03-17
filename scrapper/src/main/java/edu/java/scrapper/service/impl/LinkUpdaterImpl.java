package edu.java.scrapper.service.impl;

import edu.java.model.dto.request.LinkUpdateRequest;
import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.domain.entity.Link;
import edu.java.scrapper.dto.github.GitHubRepoResponse;
import edu.java.scrapper.dto.stackoverflow.StackOverFlowQuestionsResponse;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.LinkUpdater;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdaterImpl implements LinkUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkUpdaterImpl.class);
    private static final String GITHUB_COM = "github.com";
    private static final String STACKOVERFLOW_COM = "stackoverflow.com";
    private static final int GITHUB_OWNER_INDEX = 3;
    private static final int GITHUB_REPO_INDEX = 4;
    private static final int STACKOVERFLOW_QUESTION_ID_INDEX = 4;

    private final LinkService linkService;
    private final GitHubClient gitHubClient;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    @Override
    public int update(OffsetDateTime thresholdTime) {
        LOGGER.info("Checking for updates to links.");
        Collection<Link> links = linkService.findLinksToCheck(thresholdTime);
        int updatedCount = 0;

        for (Link link : links) {
            boolean hasUpdate = false;
            if (isGitHubLink(link.getUrl())) {
                try {
                    GitHubRepoResponse
                        response =
                        gitHubClient.fetchRepoInfo(extractOwner(link.getUrl()), extractRepo(link.getUrl())).block();
                    LOGGER.debug(
                        "Last check time: {}, GitHub updated at: {}",
                        link.getLastCheckTime(),
                        response.updatedAt()
                    );
                    hasUpdate = response != null && response.updatedAt().isAfter(link.getLastCheckTime());
                    LOGGER.debug("Has update: {}", hasUpdate);
                } catch (Exception e) {
                    LOGGER.error("Error checking updates for GitHub link: {}", link.getUrl(), e);
                }
            } else if (isStackOverflowLink(link.getUrl())) {
                try {
                    StackOverFlowQuestionsResponse
                        response = stackOverflowClient.fetchQuestionInfo(extractQuestionId(link.getUrl())).block();
                    hasUpdate = response != null && response.questions().stream()
                        .anyMatch(q -> q.updatedAt().isAfter(link.getLastCheckTime()));
                } catch (Exception e) {
                    LOGGER.error("Error checking updates for StackOverflow link: {}", link.getUrl(), e);
                }
            }

            if (hasUpdate) {
                LOGGER.info("Update found for link: {}", link.getUrl());
                List<Long> tgChatIds = linkService.findSubscribedChats(link.getId());
                botClient.sendUpdate(new LinkUpdateRequest(
                    link.getId(),
                    URI.create(link.getUrl()),
                    "Link has been updated",
                    tgChatIds
                )).block();
                linkService.updateLastCheckTime(link.getId(), OffsetDateTime.now());
                updatedCount++;
            }
        }

        return updatedCount;
    }

    private boolean isGitHubLink(String url) {
        return url.contains(GITHUB_COM);
    }

    private boolean isStackOverflowLink(String url) {
        return url.contains(STACKOVERFLOW_COM);
    }

    private String extractOwner(String url) {
        return url.split("/")[GITHUB_OWNER_INDEX];
    }

    private String extractRepo(String url) {
        return url.split("/")[GITHUB_REPO_INDEX];
    }

    private int extractQuestionId(String url) {
        String[] parts = url.split("/");
        return Integer.parseInt(parts[STACKOVERFLOW_QUESTION_ID_INDEX]);
    }
}
