package com.ecom.webapp.service;

import com.ecom.webapp.model.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByCommentParentId(int commentParentId);
    void createComment(Comment comment);
    void deleteComment(Comment comment);
}
