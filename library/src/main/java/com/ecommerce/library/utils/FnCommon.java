package com.ecommerce.library.utils;


import com.ecommerce.library.enumeration.Role;
import io.grpc.Status;

public final class FnCommon {

    /**
     * Kiểm tra xem chuỗi có null hoặc rỗng hay không
     *
     * @param str chuỗi cần kiểm tra
     * @return false nếu chuỗi là null hoặc rỗng, true nếu không
     */
    public static boolean isNotNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    /**
     * Kiểm tra xem đối tượng có null hay không
     *
     * @param obj đối tượng cần kiểm tra
     * @return false nếu đối tượng là null, true nếu không
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
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

    /**
     * Chuyển đổi Role java sang Role proto
     *
     * @param role đại diện cho Role trong Java
     * @return role đại diện cho Role trong Proto
     */
    public static com.ecommerce.enumeration.Role convertRoleToRoleProto(Role role) {
        return switch (role) {
            case ADMIN -> com.ecommerce.enumeration.Role.ADMIN;
            case OWNER -> com.ecommerce.enumeration.Role.OWNER;
            case CASHIER -> com.ecommerce.enumeration.Role.CASHIER;
            case USER -> com.ecommerce.enumeration.Role.USER;
        };
    }

    /**
     * Chuyển đổi Role proto sang Role java
     *
     * @param role đại diện cho Role trong proto
     * @return role đại diện cho Role trong java
     */
    public static Role convertRoleProtoToRole(com.ecommerce.enumeration.Role role) {
        return switch (role) {
            case ADMIN -> Role.ADMIN;
            case OWNER -> Role.OWNER;
            case CASHIER -> Role.CASHIER;
            case USER -> Role.USER;
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