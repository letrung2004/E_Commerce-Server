package com.ecom.webapp.model.dto;

import lombok.Data;

@Data
public class OrderNotificationDto {
    private int orderId;
    private String customerUsername;
    private String storeUsername;
    private String notification;
}
