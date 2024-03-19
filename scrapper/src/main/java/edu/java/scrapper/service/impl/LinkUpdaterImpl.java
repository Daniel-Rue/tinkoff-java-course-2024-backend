package edu.java.scrapper.service.impl;

import edu.java.model.dto.request.LinkUpdateRequest;
import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.domain.entity.Link;
import edu.java.scrapper.dto.github.GitHubCommitResponse;
import edu.java.scrapper.dto.github.GitHubLastUpdateResponse;
import edu.java.scrapper.dto.stackoverflow.StackOverFlowAnswersResponse;
import edu.java.scrapper.dto.stackoverflow.StackOverFlowQuestionLastActivityResponse;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.LinkUpdater;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
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
        int updatedCount = 0;
        Collection<Link> linksToCheck = linkService.findLinksToCheck(thresholdTime);

        for (Link link : linksToCheck) {
            LOGGER.info("Checking updates for link: {}", link.getUrl());
            if (isGitHubLink(link.getUrl())) {
                updatedCount += processGitHubLink(link);
            } else if (isStackOverflowLink(link.getUrl())) {
                updatedCount += processStackOverflowLink(link);
            }
        }

        LOGGER.info("Total updated links: {}", updatedCount);
        return updatedCount;
    }

    private int processGitHubLink(Link link) {
        String owner = extractOwner(link.getUrl());
        String repo = extractRepo(link.getUrl());
        LOGGER.info("Processing GitHub link: {}/{}", owner, repo);

        GitHubLastUpdateResponse lastPushResponse = gitHubClient.fetchRepoLastUpdated(owner, repo).block();
        if (lastPushResponse != null && lastPushResponse.pushedAt().isAfter(link.getLastCheckTime())) {
            return checkForNewGitHubCommits(link, owner, repo);
        } else {
            LOGGER.info("No new updates for GitHub repo: {}/{} since last check time.", owner, repo);
            return 0;
        }
    }

    private int checkForNewGitHubCommits(Link link, String owner, String repo) {
        List<GitHubCommitResponse> commits =
            gitHubClient.fetchCommitsSince(owner, repo, link.getLastCheckTime()).block();
        long newCommitsCount = commits.stream()
            .filter(commit -> commit.commit().committer().date().isAfter(link.getLastCheckTime()))
            .count();

        if (newCommitsCount > 0) {
            String commitDetails = commits.stream()
                .filter(commit -> commit.commit().committer().date().isAfter(link.getLastCheckTime()))
                .map(commit -> commit.commit().committer().date() + " (SHA: " + commit.sha() + ")")
                .collect(Collectors.joining(", "));

            LOGGER.info("New GitHub commits detected: {}. Details: {}", newCommitsCount, commitDetails);
            sendUpdateNotification(
                link,
                String.format("New GitHub commits detected: %d. Details: %s", newCommitsCount, commitDetails)
            );
            linkService.updateLastCheckTime(link.getId(), OffsetDateTime.now());
            return 1;
        }
        return 0;
    }

    private int processStackOverflowLink(Link link) {
        int questionId = extractQuestionId(link.getUrl());
        LOGGER.debug("Processing StackOverflow link, question ID: {}", questionId);

        StackOverFlowQuestionLastActivityResponse
            lastActivityResponse = stackOverflowClient.fetchQuestionLastActivity(questionId).block();
        if (lastActivityResponse != null && !lastActivityResponse.questions().isEmpty()) {
            StackOverFlowQuestionLastActivityResponse.Question question = lastActivityResponse.questions().get(0);
            if (question.lastActivityDate().isAfter(link.getLastCheckTime())) {
                LOGGER.info("New updates found for StackOverflow link: {}", link.getUrl());
                fetchAndProcessNewStackOverflowAnswers(link, questionId);

                return 1;
            }
        }
        return 0;
    }

    private void fetchAndProcessNewStackOverflowAnswers(Link link, int questionId) {
        StackOverFlowAnswersResponse answersResponse = stackOverflowClient.fetchNewAnswers(questionId).block();
        if (answersResponse != null && !answersResponse.answers().isEmpty()) {
            long newAnswersCount = answersResponse.answers().stream()
                .filter(answer -> answer.creationDate().isAfter(link.getLastCheckTime()))
                .count();
            LOGGER.debug("New StackOverflow answers count: {}", newAnswersCount);
            if (newAnswersCount > 0) {
                sendUpdateNotification(link, "New StackOverflow answers detected: " + newAnswersCount);
            }
        }
        linkService.updateLastCheckTime(link.getId(), OffsetDateTime.now());
    }

    private void sendUpdateNotification(Link link, String message) {
        List<Long> tgChatIds = linkService.findSubscribedChats(link.getId());

        if (!tgChatIds.isEmpty()) {
            for (Long chatId : tgChatIds) {
                LinkUpdateRequest updateRequest = new LinkUpdateRequest(
                    link.getId(),
                    URI.create(link.getUrl()),
                    message,
                    List.of(chatId)
                );

                try {
                    botClient.sendUpdate(updateRequest).block(); // Используем block для синхронного ожидания
                    LOGGER.info("Update notification sent for link: {} to chatId: {}", link.getUrl(), chatId);
                } catch (Exception error) {
                    LOGGER.error(
                        "Failed to send update notification for link: {} to chatId: {}",
                        link.getUrl(),
                        chatId,
                        error
                    );
                }
            }
        } else {
            LOGGER.info("No chats subscribed for updates for link: {}", link.getUrl());
        }
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
