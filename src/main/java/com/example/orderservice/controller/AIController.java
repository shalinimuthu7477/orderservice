package com.example.orderservice.controller;

import com.example.orderservice.dto.AIResponse;
import com.example.orderservice.service.AIService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public AIResponse chat( @RequestParam String conversationId,@RequestBody String prompt) {

        String response = aiService.askAI(prompt,conversationId);

        return new AIResponse(response);
    }

    @GetMapping("/order/{orderId}")
    public AIResponse askAboutOrder(
            @PathVariable Long orderId,
            @RequestParam String question) {

        String response = aiService.askAboutOrder(orderId, question);

        return new AIResponse(response);
    }
}