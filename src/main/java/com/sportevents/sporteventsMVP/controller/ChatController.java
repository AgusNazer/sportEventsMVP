package com.sportevents.sporteventsMVP.controller;

import com.sportevents.sporteventsMVP.dto.chat.ChatRequest;
import com.sportevents.sporteventsMVP.dto.chat.ChatResponse;
import com.sportevents.sporteventsMVP.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest req) {
        return ResponseEntity.ok(chatService.chat(req));
    }
}