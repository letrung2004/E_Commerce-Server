package com.ecom.webapp.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String getAdminPage() {
        return "admin/admin";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
