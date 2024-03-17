package edu.java.scrapper.sheduler;

import edu.java.scrapper.service.LinkUpdater;
import java.time.Duration;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class LinkUpdaterScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkUpdaterScheduler.class);
    private final LinkUpdater linkUpdater;

    @Value("${app.scheduler.interval}")
    private long schedulerInterval;

    @Scheduled(fixedDelayString = "${app.scheduler.interval}")
    public void update() {
        LOGGER.info("Scheduled link update job started.");
        OffsetDateTime thresholdTime = OffsetDateTime.now().minus(Duration.ofMillis(schedulerInterval));
        int updatedLinksCount = linkUpdater.update(thresholdTime);
        LOGGER.info("{} links have been updated.", updatedLinksCount);
    }
}
