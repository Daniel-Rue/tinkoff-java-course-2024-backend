package edu.java.scrapper.service;

import java.time.OffsetDateTime;

public interface LinkUpdater {
    int update(OffsetDateTime thresholdTime);
}
