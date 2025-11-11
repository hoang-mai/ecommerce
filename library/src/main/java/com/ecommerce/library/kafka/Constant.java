package com.ecommerce.library.kafka;

public final class Constant {
    public static final String USER_SERVICE_TOPIC = "user-service-topic";
    public static final String USER_SERVICE_GROUP = "user-service-group";

    public static final String AUTH_SERVICE_TOPIC = "auth-service-topic";
    public static final String AUTH_SERVICE_GROUP = "auth-service-group";

    public static final String READ_SERVICE_TOPIC = "read-service-topic";

    // Specific topics
    public static final String CREATE_USER_TOPIC = READ_SERVICE_TOPIC + ".create-user";
    public static final String UPDATE_USER_TOPIC = READ_SERVICE_TOPIC + ".update-user";
    public static final String UPDATE_ROLE_TOPIC = READ_SERVICE_TOPIC + ".update-role";
    public static final String UPDATE_ACCOUNT_STATUS_TOPIC = READ_SERVICE_TOPIC + ".update-account-status";
    public static final String UPDATE_AVATAR_URL_TOPIC = READ_SERVICE_TOPIC + ".update-avatar-url";
    public static final String READ_SERVICE_GROUP = "read-service-group";
}
