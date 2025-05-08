package com.ecom.webapp.service.impl;

import com.ecom.webapp.model.Comment;
import com.ecom.webapp.model.Review;
import com.ecom.webapp.model.User;
import com.ecom.webapp.model.dto.CommentDto;
import com.ecom.webapp.repository.CommentRepository;
import com.ecom.webapp.repository.ReviewRepository;
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
    @Autowired
    private ReviewRepository reviewRepository;


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
        Review review = this.reviewRepository.getReviewById(commentDto.getReviewId());
        if (review == null) {
            throw new EntityNotFoundException("Review not found with id: " + commentDto.getReviewId());
        }
        User user = this.userRepository.getUserByUsername(commentDto.getUsername());
        if (user == null) {
            throw new EntityNotFoundException("User not found with id: " + commentDto.getUsername());
        }
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setContent(commentDto.getContent());
        this.commentRepository.createComment(comment);

        review.setResponse(comment);
        this.reviewRepository.updateReview(review);
    }

    @Override
    public void deleteComment(Comment comment) {

    }
}
