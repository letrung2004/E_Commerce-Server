package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.responseDto.UserResponse;
import com.ecom.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiUserController {

    @Autowired
    private UserService userDetailService;

    @GetMapping("/secure/profile")
    @ResponseBody
    public ResponseEntity<UserResponse> getCurrentUser(Principal principal) {
        String username = principal.getName();
        System.out.println(username);
        return new ResponseEntity<>(this.userDetailService.getUserResponseByUsername(principal.getName()), HttpStatus.OK);
    }
}
