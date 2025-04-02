package com.ecom.webapp.repository;

import com.ecom.webapp.model.Comment;

import java.util.List;

public interface CommentRepository {
    List<Comment> getCommentsByCommentParentId(Comment commentParent);
    void createComment(Comment comment);
    void deleteComment(Comment comment);
}
