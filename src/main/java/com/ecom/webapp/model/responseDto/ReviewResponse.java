package com.ecom.webapp.model.responseDto;

import com.ecom.webapp.model.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private int id;

    private int userId;

    private int storeId;

    private int productId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CommentResponse comment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Instant dateCreated;


    private int rate;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.storeId = review.getStore().getId();
        this.productId = review.getProduct().getId();
        Comment comment = review.getComment();
        if (comment != null) {
            this.comment = new CommentResponse(comment);
        } else {
            this.comment = null;
        }
        this.dateCreated = review.getDateCreated();
        this.rate = review.getRate();
    }
}
