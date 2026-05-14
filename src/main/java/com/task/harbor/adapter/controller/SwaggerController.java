package com.task.harbor.adapter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    @GetMapping("/swagger-ui")
    public String redirectSwagger() {
        return "redirect:/swagger-ui/index.html";
    }

    @GetMapping("/swagger-ui.html")
    public String swaggerUi() {
        return "redirect:/swagger-ui/index.html";
    }
}