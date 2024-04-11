package edu.java.scrapper.service;

import edu.java.model.dto.request.LinkUpdateRequest;

public interface NotificationSenderService {
    void sendUpdate(LinkUpdateRequest updateRequest);
}
