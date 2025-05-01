package com.campus.campuscommunity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login-page";
    }

    @GetMapping("/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/login-success")
    public String loginSuccessPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "login-success"; // login-success.html 템플릿을 렌더링
    }
}