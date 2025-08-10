package com.example.edu.school.library.utils;


import com.example.edu.school.library.enumeration.Role;
import io.grpc.Status;

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
        return str.replaceAll("[àáạảãâầấẩẫậăắặẳẵ]", "a").replaceAll("[èéẹẻẽêềếểễệ]", "e").replaceAll("[ìíịỉĩ]", "i").replaceAll("[òóọỏõôồốổỗộơờớởỡợ]", "o").replaceAll("[ùúụủũưừứửữự]", "u").replaceAll("[ỳýỵỷỹ]", "y").replaceAll("đ", "d").replaceAll("[ÀÁẠẢÃÂẦẤẨẪẬĂẮẶẲẴ]", "A").replaceAll("[ÈÉẸẺẼÊỀẾỂỄỆ]", "E").replaceAll("[ÌÍỊỈĨ]", "I").replaceAll("[ÒÓỌỎÕÔỒỐỔỖỘƠỜỚỞỠỢ]", "O").replaceAll("[ÙÚỤỦŨƯỪỨỬỮỰ]", "U").replaceAll("[ỲÝỴỶỸ]", "Y").replaceAll("Đ", "D");
    }

    /**
     * Chuyển đổi Role java sang Role proto
     *
     * @param role đại diện cho Role trong Java
     * @return role đại diện cho Role trong Proto
     */
    public static com.example.edu.school.enumeration.Role convertRoleToRoleProto(Role role) {
        return switch (role) {
            case ADMIN -> com.example.edu.school.enumeration.Role.ADMIN;
            case PRINCIPAL -> com.example.edu.school.enumeration.Role.PRINCIPAL;
            case ASSISTANT -> com.example.edu.school.enumeration.Role.ASSISTANT;
            case TEACHER -> com.example.edu.school.enumeration.Role.TEACHER;
            case STUDENT -> com.example.edu.school.enumeration.Role.STUDENT;
            case PARENT -> com.example.edu.school.enumeration.Role.PARENT;
        };
    }

    /**
     * Chuyển đổi Role proto sang Role java
     *
     * @param role đại diện cho Role trong proto
     * @return role đại diện cho Role trong java
     */
    public static Role convertRoleProtoToRole(com.example.edu.school.enumeration.Role role) {
        return switch (role) {
            case ADMIN -> Role.ADMIN;
            case PRINCIPAL -> Role.PRINCIPAL;
            case ASSISTANT -> Role.ASSISTANT;
            case TEACHER -> Role.TEACHER;
            case STUDENT -> Role.STUDENT;
            case PARENT -> Role.PARENT;
            default -> throw new IllegalArgumentException(MessageError.INVALID_ROLE);
        };
    }

    /**
     * Chuyển đổi status code từ gRPC sang HTTP
     *
     * @param code mã trạng thái từ gRPC
     * @return mã trạng thái HTTP tương ứng
     */
    public static int convertGrpcCodeToHttpStatus(Status.Code code) {
        if (code == null) {
            return 500;
        }
        return switch (code) {
            case OK -> 200;
            case CANCELLED -> 499;
            case UNKNOWN, DATA_LOSS, INTERNAL -> 500;
            case INVALID_ARGUMENT, FAILED_PRECONDITION, OUT_OF_RANGE -> 400;
            case DEADLINE_EXCEEDED -> 504;
            case NOT_FOUND -> 404;
            case ALREADY_EXISTS, ABORTED -> 409;
            case PERMISSION_DENIED -> 403;
            case RESOURCE_EXHAUSTED -> 429;
            case UNIMPLEMENTED -> 501;
            case UNAVAILABLE -> 503;
            case UNAUTHENTICATED -> 401;
        };
    }
}