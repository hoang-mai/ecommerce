package com.ecommerce.library.utils;


import com.ecommerce.library.enumeration.AccountStatus;
import com.ecommerce.library.enumeration.Gender;
import com.ecommerce.library.enumeration.Role;
import com.ecommerce.library.enumeration.UserVerificationStatus;
import io.grpc.Status;

import java.util.List;
import java.util.Map;

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
     * Kiểm tra xem danh sách có null hoặc rỗng hay không
     *
     * @param list danh sách cần kiểm tra
     * @return false nếu danh sách là null hoặc rỗng, true nếu không
     */
    public static boolean isNotNullOrEmptyList(List<?> list) {
        return list != null && !list.isEmpty();
    }

    /**
     * Kiểm tra xem map có null hoặc rỗng hay không
     *
     * @param map map cần kiểm tra
     * @return false nếu map là null hoặc rỗng, true nếu không
     */
    public static boolean isNotNullOrEmptyMap(Map<?, ?> map) {
        return map != null && !map.isEmpty();
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
            case EMPLOYEE -> com.ecommerce.enumeration.Role.EMPLOYEE;
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
            case EMPLOYEE -> Role.EMPLOYEE;
            case USER -> Role.USER;
            default -> throw new IllegalArgumentException(MessageError.INVALID_ROLE);
        };
    }

    /**
     * Chuyển đổi Gender java sang Gender proto
     *
     * @param gender đại diện cho Gender trong Java
     * @return gender đại diện cho Gender trong Proto
     */
    public static com.ecommerce.enumeration.Gender convertGenderToGenderProto(Gender gender) {
        return switch (gender) {
            case MALE -> com.ecommerce.enumeration.Gender.MALE;
            case FEMALE -> com.ecommerce.enumeration.Gender.FEMALE;
            case OTHER -> com.ecommerce.enumeration.Gender.OTHER;
        };
    }

    /**
     * Chuyển đổi Gender proto sang Gender java
     *
     * @param gender đại diện cho Gender trong proto
     *               @return gender đại diện cho Gender trong java
     */
    public static Gender convertGenderProtoToGender(com.ecommerce.enumeration.Gender gender) {
        return switch (gender) {
            case MALE -> Gender.MALE;
            case FEMALE -> Gender.FEMALE;
            case OTHER -> Gender.OTHER;
            default -> throw new IllegalArgumentException(MessageError.INVALID_GENDER);
        };
    }

    /**
     * Chuyển đổi AccountStatus java sang AccountStatus proto
     *
     * @param status đại diện cho AccountStatus trong Java
     * @return status đại diện cho AccountStatus trong Proto
     */
    public static com.ecommerce.enumeration.AccountStatus convertAccountStatusToAccountStatusProto(AccountStatus status) {
        return switch (status) {
            case ACTIVE -> com.ecommerce.enumeration.AccountStatus.ACTIVE;
            case INACTIVE -> com.ecommerce.enumeration.AccountStatus.INACTIVE;
            case SUSPENDED -> com.ecommerce.enumeration.AccountStatus.SUSPENDED;
        };
    }

    /**
     * Chuyển đổi AccountStatus proto sang AccountStatus java
     *
     * @param status đại diện cho AccountStatus trong proto
     * @return status đại diện cho AccountStatus trong java
     */
    public static AccountStatus convertAccountStatusProtoToAccountStatus(com.ecommerce.enumeration.AccountStatus status) {
        return switch (status) {
            case ACTIVE -> AccountStatus.ACTIVE;
            case INACTIVE -> AccountStatus.INACTIVE;
            case SUSPENDED -> AccountStatus.SUSPENDED;
            default -> throw new IllegalArgumentException(MessageError.INVALID_ACCOUNT_STATUS);
        };
    }

    /**
     * Chuyển đổi UserVerificationStatus java sang UserVerificationStatus proto
     *
     * @param status đại diện cho UserVerificationStatus trong Java
     * @return status đại diện cho UserVerificationStatus trong Proto
     */
    public static com.ecommerce.enumeration.UserVerificationStatus convertUserVerificationStatusToProto(UserVerificationStatus status) {
        return switch (status) {
            case PENDING -> com.ecommerce.enumeration.UserVerificationStatus.PENDING;
            case APPROVED -> com.ecommerce.enumeration.UserVerificationStatus.APPROVED;
            case REJECTED -> com.ecommerce.enumeration.UserVerificationStatus.REJECTED;
        };
    }

    /**
     * Chuyển đổi UserVerificationStatus proto sang UserVerificationStatus java
     *
     * @param status đại diện cho UserVerificationStatus trong proto
     * @return status đại diện cho UserVerificationStatus trong java
     */
    public static UserVerificationStatus convertUserVerificationStatusProtoToJava(com.ecommerce.enumeration.UserVerificationStatus status) {
        return switch (status) {
            case PENDING -> UserVerificationStatus.PENDING;
            case APPROVED -> UserVerificationStatus.APPROVED;
            case REJECTED -> UserVerificationStatus.REJECTED;
            default -> throw new IllegalArgumentException(MessageError.INVALID_USER_VERIFICATION_STATUS);
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