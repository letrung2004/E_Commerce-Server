package com.ecom.webapp.model.responseDto;

import com.ecom.webapp.model.*;
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

    private int commentId;

    private Instant dateCreated;
    private int rate;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.storeId = review.getStore().getId();
        this.productId = review.getProduct().getId();
        this.commentId = review.getComment().getId();
        this.dateCreated = review.getDateCreated();
        this.rate = review.getRate();
    }
}
