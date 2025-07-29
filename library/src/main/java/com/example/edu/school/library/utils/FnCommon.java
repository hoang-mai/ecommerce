package com.example.edu.school.library.utils;

public final class FnCommon {

    /**
     * Kiểm tra xem chuỗi có null hoặc rỗng hay không
     *
     * @param str chuỗi cần kiểm tra
     * @return true nếu chuỗi là null hoặc rỗng, false nếu không
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Loại bỏ dấu tiếng Việt trong chuỗi
     *
     * @param str chuỗi cần loại bỏ dấu
     * @return chuỗi đã loại bỏ dấu tiếng Việt
     */
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
