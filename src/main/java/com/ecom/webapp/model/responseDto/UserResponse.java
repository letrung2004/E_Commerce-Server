package com.ecom.webapp.model.responseDto;
import com.ecom.webapp.model.Store;
import com.ecom.webapp.model.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponse {
    private int id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatar;
    private boolean gender;
    private LocalDate dateOfBirth;
    private boolean storeActive;
    private String role;
    private Integer storeId;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.avatar = user.getAvatar();
        this.gender = user.isGender();
        this.dateOfBirth = user.getDateOfBirth();
        this.storeActive = user.isStoreActive();
        this.role = user.getRole();
        if (user.getStore() != null) {
            this.storeId = user.getStore().getId();
        } else {
            this.storeId = null;
        }
    }
}
