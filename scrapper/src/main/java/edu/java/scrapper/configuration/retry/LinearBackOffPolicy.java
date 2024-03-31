package edu.java.scrapper.configuration.retry;

import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.backoff.BackOffPolicy;

public class LinearBackOffPolicy implements BackOffPolicy {
    private static final Long DEFAULT_INITIAL_INTERVAL = 2000L;
    private static final Long DEFAULT_MAX_INTERVAL = 40000L;

    private Long initialInterval = DEFAULT_INITIAL_INTERVAL;
    private Long maxInterval = DEFAULT_MAX_INTERVAL;

    @Override
    public BackOffContext start(RetryContext context) {
        return new LinearBackOffContext();
    }

    @Override
    public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
        LinearBackOffContext linearBackOffContext = (LinearBackOffContext) backOffContext;
        long sleepTime = Math.min(initialInterval * (linearBackOffContext.attemptCount + 1), maxInterval);

        try {
            Thread.sleep(sleepTime);
            linearBackOffContext.incrementAttemptCount();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BackOffInterruptedException("Interrupted during back off", e);
        }
    }

    public void setInitialInterval(Long initialInterval) {
        if (initialInterval != null && initialInterval > 0) {
            this.initialInterval = initialInterval;
        }
    }

    public void setMaxInterval(Long maxInterval) {
        if (maxInterval != null && maxInterval > 0) {
            this.maxInterval = maxInterval;
        }
    }

    private static class LinearBackOffContext implements BackOffContext {
        private int attemptCount = 0;

        public void incrementAttemptCount() {
            this.attemptCount++;
        }
    }
}
