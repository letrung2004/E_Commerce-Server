package com.ecom.webapp.model.responseDto;

import com.ecom.webapp.model.User;
import lombok.Data;

@Data
public class UserReviewResponse {
    private int id;
    private String fullName;
    private String avatar;

    public UserReviewResponse(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.avatar = user.getAvatar();
    }
}
