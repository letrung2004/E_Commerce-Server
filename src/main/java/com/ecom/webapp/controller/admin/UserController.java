package com.ecom.webapp.controller.admin;

import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.UserDto;
import com.ecom.webapp.service.UserService;
import com.ecom.webapp.service.impl.MailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
    @Autowired
    private MailService mailService;

    @GetMapping("/admin/user")
    public String getUserPage(Model model) {
        model.addAttribute("users", this.userService.getUsers());
        return "admin/user/users";
    }

    @GetMapping("/admin/user/{id}")
    public String getUserDetailPage(Model model, @PathVariable("id") Integer id) {
        User user = this.userService.getUserById(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String formattedDob = (user.getDateOfBirth() != null)
                ? user.getDateOfBirth().format(formatter)
                : "Chưa cập nhật";

        model.addAttribute("dateOfBirth_", formattedDob);
        model.addAttribute("user", user);
        return "admin/user/detail";
    }


    @PostMapping("/admin/user/update")
    public String updateUser(@Valid @ModelAttribute("user") UserDto userDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("error");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.getDefaultMessage());
            });
            return "admin/user/detail";
        }

        this.userService.update(userDto);

        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/create")
    public String getCreateUserPage(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "admin/user/create";
    }

    public void sendEmail(String fullName, String to, String username, String password) {
        try {
            String subject = "Đăng ký thành công Shopii Admin members";
            String emailContent = "Xin chào " + fullName + ",\n\n" +
                    "Cảm ơn bạn đã đăng ký!.\n" +
                    "Username(Tên đăng nhập): " + username + "\n" +
                    "Password(Mật khẩu): " + password + "\n\n" +
                    "Xin vui lòng đổi mật khẩu sau lần đăng nhập đầu tiên.\n\n" +
                    "Trân trọng,\n" +
                    "Admin Team";

            mailService.sendSimpleMessage(to, subject, emailContent);

            // You might want to log success or add a flash message
            // redirectAttributes.addFlashAttribute("message", "User created and email sent successfully");
        } catch (Exception e) {
            // Handle the exception - maybe log it or notify admin
            System.err.println("Failed to send email: " + e.getMessage());
            // You might want to add a flash message here too
            // redirectAttributes.addFlashAttribute("error", "User created but email failed to send");
        }
    }

    @PostMapping("/admin/user/create")
    public String createUser(Model model, @Valid @ModelAttribute("user") UserDto userDto,
                             BindingResult bindingResult) throws MessagingException {
        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors) {
            System.out.println(">>> " + error.getField() + " - " + error.getDefaultMessage());
        }
        if (bindingResult.hasErrors()) {
            return "admin/user/create";
        }

        String username = RandomUserGenerator.generateUsername(userDto.getEmail());
        String password = RandomUserGenerator.generatePassword();

        System.out.println(userDto);

        this.userService.createUser(userDto, username, password);

        sendEmail(userDto.getFullName(), userDto.getEmail(), username, password);

        return "redirect:/admin/user";
    }

    @GetMapping("/admin/user/delete/{id}")
    public String getDeleteUserPage(Model model, @PathVariable("id") Integer id) {
        User user = this.userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/user/delete";
    }

    @PostMapping("/admin/user/delete")
    public String deleteUser(Model model, @ModelAttribute("user") User user) {
        this.userService.deleteUser(user.getId());
        return "redirect:/admin/user";
    }

}
