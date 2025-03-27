package com.ecom.webapp.controller.admin;

import com.ecom.webapp.model.User;
import com.ecom.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    public String getAdminPage() {
        return "admin/admin";
    }


    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/admin/my-account")
    public String getCurrentUser(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/admin";
        }
        String username = userDetails.getUsername();
        User user = this.userService.getUserByUsername(username);
        model.addAttribute("user", user);
        return "admin/account";
    }
}


