package edu.java.scrapper.service;

import edu.java.model.dto.request.LinkUpdateRequest;

public interface UpdateNotificationService {
    void sendUpdate(LinkUpdateRequest updateRequest);
}
