package com.ecom.webapp.model.responseDto;

import com.ecom.webapp.model.Comment;
import com.ecom.webapp.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentResponse {
    private int id;
    private int userId;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Instant createdDate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CommentResponse> replies;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.userId = comment.getUser().getId();
        this.content = comment.getContent();
        this.createdDate = comment.getDateCreated();

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            this.replies = comment.getReplies().stream()
                    .map(CommentResponse::new)
                    .collect(Collectors.toList());
        } else {
            this.replies = null; // hoặc có thể để null nếu không muốn field này xuất hiện
        }
    }

}
