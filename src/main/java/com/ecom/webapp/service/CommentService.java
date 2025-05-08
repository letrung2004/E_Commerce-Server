package com.ecom.webapp.service;

import com.ecom.webapp.model.Comment;
import com.ecom.webapp.model.dto.CommentDto;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByCommentParentId(int reviewId);
    Comment getCommentByParentId(int commentParentId);
    void createComment(CommentDto comment);
    void deleteComment(Comment comment);
}
