package org.me.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RouterController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/templates")
    public String template() {
        return "template";
    }

    @GetMapping("/schedules")
    public String schedule() {
        return "schedule";
    }

    @RequestMapping("/")
    public String index() {
        return "redirect:/templates";
    }
}
