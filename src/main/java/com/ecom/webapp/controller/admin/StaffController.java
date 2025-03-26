package com.ecom.webapp.controller.admin;

import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.UserDto;
import com.ecom.webapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class StaffController {

    @Autowired
    private UserService userService;

    @GetMapping("/staff")
    public String staffPage(Model model) {
        return "staff/approve-user";
    }

    @GetMapping("/staff/my-account")
    public String getCurrentUser(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/staff";
        }
        String username = userDetails.getUsername();
        User user = this.userService.getUserByUsername(username);
        model.addAttribute("user", user);
        return "staff/account";
    }

    @PostMapping("/staff/user/update")
    public String updateUser(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("error");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.getDefaultMessage());
            });
            return "staff/account";
        }
        System.out.println(userDto);
        System.out.println(userDto.getDateOfBirth().getClass().getSimpleName());
        this.userService.update(userDto);

        return "redirect:/staff/my-account";
    }
}
