package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.Comment;
import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.CommentDto;
import com.ecom.webapp.repository.CommentRepository;
import com.ecom.webapp.repository.UserRepository;
import com.ecom.webapp.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Comment> getCommentsByCommentParentId(int commentParentId) {
        return List.of();
    }

    @Override
    public Comment getCommentByParentId(int commentParentId) {
        return null;
    }

    @Override
    public void createComment(CommentDto commentDto) {
        Comment commentParent = this.commentRepository.getCommentById(commentDto.getCommentParentId());
        if (commentParent == null) {
            throw new EntityNotFoundException("Comment parent not found with id: " + commentDto.getCommentParentId());
        }
        User user = this.userRepository.getById(commentDto.getUserId());
        if (user == null) {
            throw new EntityNotFoundException("User not found with id: " + commentDto.getUserId());
        }
        Comment comment = new Comment();
        comment.setCommentParent(commentParent);
        comment.setUser(user);
        comment.setContent(commentDto.getContent());
        this.commentRepository.createComment(comment);
    }

    @Override
    public void deleteComment(Comment comment) {

    }
}
