package spring.ai.example.spring_ai_demo.controller;

import org.springframework.web.bind.annotation.*;
import spring.ai.example.spring_ai_demo.service.ToolCallingService;

@RestController
@RequestMapping("/ai/tools")
public class ToolsController {

    private final ToolCallingService toolCallingService;

    public ToolsController(ToolCallingService toolCallingService) {
        this.toolCallingService = toolCallingService;
    }

    @GetMapping("/simple")
    public String simpleToolTest(@RequestParam String message) {
        return toolCallingService.askWithTools(message);
    }
}