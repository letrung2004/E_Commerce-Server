package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.UserRegisterDTO;
import com.ecom.webapp.repository.UserRepository;
import com.ecom.webapp.service.UserService;
import com.ecom.webapp.utils.JwtUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class ApiAuthController {
    @Autowired
    private UserService userDetailsService;

    @Autowired
    private Validator validator;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User u) {

        if (this.userDetailsService.authenticate(u.getUsername(), u.getPassword())) {
            try {
                String token = JwtUtils.generateToken(u.getUsername());
                return ResponseEntity.ok().body(Collections.singletonMap("token", token));
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Lỗi khi tạo JWT");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai thông tin đăng nhập");
    }

    @PostMapping(path = "/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestParam Map<String, String> params,
                                       @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        // Validation Register User ( chưa tối ưu cần xem lại)
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername(params.get("username"));
        registerDTO.setPassword(params.get("password"));
        registerDTO.setFullName(params.get("fullName"));
        registerDTO.setEmail(params.get("email"));
        registerDTO.setPhoneNumber(params.get("phoneNumber"));

        Set<ConstraintViolation<UserRegisterDTO>> violations = validator.validate(registerDTO);
        if (!violations.isEmpty()) {
            Map<String, String> errors = violations.stream()
                    .collect(Collectors.toMap(
                            violation -> violation.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    ));
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        if(userRepository.existUsername(registerDTO.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("username", "Username đã tồn tại"));
        }
        if(userRepository.existEmail(registerDTO.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("email", "Email đã tồn tại"));
        }
        return new ResponseEntity<>(this.userDetailsService.registerUser(params, avatar), HttpStatus.CREATED);
    }
}
