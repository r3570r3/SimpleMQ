package com.messagequeue.pubsub.controllers;

import com.messagequeue.pubsub.entities.Message;
import com.messagequeue.pubsub.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class PubSubController {

    @Autowired
    private MessageService service;

    @GetMapping("/consume")
    public ResponseEntity<Message> consumeMessage() {
        return ResponseEntity.of(service.readMessage());
    }

    @PostMapping("/publish")
    public ResponseEntity<Message> publishMessage(@RequestBody Map<String, String> json) {
        Message m = service.createMessage(json.get("topic"), 1, json.get("payload"));
        return ResponseEntity.ok(m);
    }
}
