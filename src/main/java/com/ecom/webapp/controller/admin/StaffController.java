package com.ecom.webapp.controller.admin;

import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.UserDto;
import com.ecom.webapp.service.StoreService;
import com.ecom.webapp.service.UserService;
import com.ecom.webapp.service.impl.MailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class StaffController {

    @Autowired
    private UserService userService;
    @Autowired
    private StoreService storeService;


    @GetMapping("/staff")
    public String staffPage(Model model) {
        return "staff/dashboard";
    }




    @GetMapping("/staff/approve-stores")
    public String staffApproveStorePage(Model model) {
        // List of stores that its own has storeActive field is false
        //  store.owner.storeActive == false

        List<Object[]> storesUnprocessed = storeService.getStoresUnprocessed();
        storesUnprocessed.forEach(stores -> {
            System.out.printf("%s - %s - %s - %s - %d\n", stores[0], stores[1], stores[2], stores[3], (int)stores[4]);
        });

        model.addAttribute("storesUnprocessed", storesUnprocessed);


        return "staff/approve-store";
    }

    @PostMapping("/staff/confirm-store/{ownerId}")
    public String approveStore(Model model, @PathVariable("ownerId") Object userId) {

        int ownerId = Integer.parseInt(userId.toString());
        this.userService.acceptStoreActivation(ownerId);



        return "redirect:/staff/approve-stores";
    }

    @PostMapping("/staff/reject-store/{storeId}")
    public String rejectStore(Model model, @PathVariable("storeId") Object id) {
        // Object id is storeId from html form
        int storeId = Integer.parseInt(id.toString());
        this.storeService.deleteStore(storeId);

        return "redirect:/staff/approve-stores";
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
