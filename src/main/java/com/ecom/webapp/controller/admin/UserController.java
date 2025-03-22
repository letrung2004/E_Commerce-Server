package com.ecom.webapp.controller.admin;

import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.UserDto;
import com.ecom.webapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin/user")
    public String getUserPage(Model model) {
        model.addAttribute("users", this.userService.getUsers());
        return "admin/user/users";
    }

    @GetMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable("id") Integer id) {
        User user = this.userService.getUserById(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        model.addAttribute("dateOfBirth_", user.getDateOfBirth().format(formatter));

        model.addAttribute("user", user);
        return "admin/user/detail";
    }

    @PostMapping("/admin/user/update")
    public String updateProduct(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("error");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.getDefaultMessage());
            });
            return "admin/user/detail";
        }
        System.out.println(userDto);
        System.out.println(userDto.getDateOfBirth().getClass().getSimpleName());
        this.userService.update(userDto);

        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/create")
    public String getCreateUserPage(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "admin/user/create";
    }
}
