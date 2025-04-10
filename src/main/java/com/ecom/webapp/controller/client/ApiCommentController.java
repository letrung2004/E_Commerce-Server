package com.ecom.webapp.controller.client;

import com.ecom.webapp.model.dto.CommentDto;
import com.ecom.webapp.model.responseDto.CommentResponse;
import com.ecom.webapp.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
public class ApiCommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentDto commentDto) {
        System.out.println(commentDto);
        this.commentService.createComment(commentDto);
        return ResponseEntity.ok("Create comment successfully!");
    }



}
