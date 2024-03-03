package edu.java.bot.service;

import edu.java.model.dto.request.LinkUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UpdateService {

    public ResponseEntity<?> sendUpdate(LinkUpdateRequest updateRequest) {

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
