package com.ecom.webapp.controller.admin;
import java.util.Random;

public class RandomUserGenerator {
    public static String generateUsername(String email) {
        // Lấy phần đầu của email trước @ và thêm 4 số ngẫu nhiên
        String prefix = email.split("@")[0];
        int randomNum = new Random().nextInt(1000);
        return prefix + randomNum;
    }


    public static String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        int length = 8 + new Random().nextInt(5);

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }
}

