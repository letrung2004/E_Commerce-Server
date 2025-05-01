package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.dto.CommentDto;
import com.ecom.webapp.model.responseDto.CommentResponse;
import com.ecom.webapp.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/secure/comment")
public class ApiCommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentDto commentDto, Principal principal) {
        System.out.println(commentDto);
        String username = principal.getName();
        commentDto.setUsername(username);
        this.commentService.createComment(commentDto);
        return ResponseEntity.ok("Create comment successfully!");
    }



}
