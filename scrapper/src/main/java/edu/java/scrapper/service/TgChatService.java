package edu.java.scrapper.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TgChatService {

    public ResponseEntity<?> registerChat(Long id) {
        return new ResponseEntity<>("Chat registered successfully with ID: " + id, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteChat(Long id) {
        return new ResponseEntity<>("Chat deleted successfully with ID: " + id, HttpStatus.OK);
    }
}
