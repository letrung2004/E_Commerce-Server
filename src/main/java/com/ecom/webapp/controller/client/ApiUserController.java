package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.responseDto.UserResponse;
import com.ecom.webapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        if (principal == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = principal.getName();
        System.out.println(username);
        return new ResponseEntity<>(this.userDetailService.getUserResponseByUsername(principal.getName()), HttpStatus.OK);
    }

    @GetMapping("/secure/search/{username}")
    public ResponseEntity<?> searchUserByUsername(Principal principal, @PathVariable(value = "username") String username) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You must login first!");
        }
        String currentUsername = principal.getName();


        if (username.equalsIgnoreCase(currentUsername)) {
            return ResponseEntity.badRequest().body("Can not chat with yourself!");
        }
        try {
            UserResponse foundUser = userDetailService.getUserResponseByUsername(username);
            if (foundUser != null) {
                return ResponseEntity.ok(foundUser);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with username: " + username);

            }
        } catch (UsernameNotFoundException e) { // Bắt exception nếu service ném ra
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with username: " + username);
        } catch (Exception e) {
            // Log lỗi server
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi server khi tìm kiếm người dùng.");
        }

    }
}
