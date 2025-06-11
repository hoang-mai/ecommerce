package com.example.edu.school.user.utils;

public class VietnameseUtils {
    public static String removeVietnameseAccents(String str) {
        if (str == null) return null;
        return str.replaceAll("[àáạảãâầấẩẫậăắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếểễệ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốổỗộơờớởỡợ]", "o")
                .replaceAll("[ùúụủũưừứửữự]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("đ", "d")
                .replaceAll("[ÀÁẠẢÃÂẦẤẨẪẬĂẮẶẲẴ]", "A")
                .replaceAll("[ÈÉẸẺẼÊỀẾỂỄỆ]", "E")
                .replaceAll("[ÌÍỊỈĨ]", "I")
                .replaceAll("[ÒÓỌỎÕÔỒỐỔỖỘƠỜỚỞỠỢ]", "O")
                .replaceAll("[ÙÚỤỦŨƯỪỨỬỮỰ]", "U")
                .replaceAll("[ỲÝỴỶỸ]", "Y")
                .replaceAll("Đ", "D");
    }

}
