package edu.java.bot.controller;

import edu.java.bot.service.UpdateService;
import edu.java.model.dto.request.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class UpdatesController {

    private final UpdateService updateService;

    @PostMapping
    public ResponseEntity<?> sendUpdate(@RequestBody LinkUpdateRequest updateRequest) {
        return updateService.sendUpdate(updateRequest);
    }
}

