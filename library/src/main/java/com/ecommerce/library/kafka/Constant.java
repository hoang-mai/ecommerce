package com.ecommerce.library.kafka;

public final class Constant {

    // Read Service Kafka Topics and Group
    public static final String READ_SERVICE_TOPIC = "read-service-topic";
    public static final String READ_SERVICE_GROUP = "read-service-group";
    public static final String CREATE_PRODUCT_TOPIC = READ_SERVICE_TOPIC + ".create-product";
    public static final String UPDATE_PRODUCT_TOPIC = READ_SERVICE_TOPIC + ".update-product";
    public static final String UPDATE_STATUS_PRODUCT_TOPIC = READ_SERVICE_TOPIC + ".update-status-product";
    public static final String UPDATE_VARIANT_STATUS_PRODUCT_TOPIC= READ_SERVICE_TOPIC + ".update-variant-status-product";
    public static final String CREATE_USER_TOPIC = READ_SERVICE_TOPIC + ".create-user";
    public static final String UPDATE_USER_TOPIC = READ_SERVICE_TOPIC + ".update-user";
    public static final String UPDATE_ROLE_TOPIC = READ_SERVICE_TOPIC + ".update-role";
    public static final String UPDATE_ACCOUNT_STATUS_TOPIC = READ_SERVICE_TOPIC + ".update-account-status";
    public static final String UPDATE_AVATAR_URL_TOPIC = READ_SERVICE_TOPIC + ".update-avatar-url";


    // Order Service Kafka Topics and Group
    public static final String ORDER_SERVICE_TOPIC = "order-service-topic";
    public static final String ORDER_SERVICE_GROUP = "order-service-group";
    public static final String CREATE_PRODUCT_CACHE_TOPIC = ORDER_SERVICE_TOPIC + ".create-product-cache";
    public static final String UPDATE_ORDER_STATUS_TOPIC = ORDER_SERVICE_TOPIC + ".update-order-status";

    // Product Service Kafka Topics and Group
    public static final String PRODUCT_SERVICE_TOPIC = "product-service-topic";
    public static final String PRODUCT_SERVICE_GROUP = "product-service-group";
    public static final String CREATE_ORDER_TOPIC = PRODUCT_SERVICE_TOPIC + ".create-order";

    // Chat Service Kafka Topics and Group
    public static final String CHAT_SERVICE_TOPIC = "chat-service-topic";
    public static final String ORDER_STATUS_TOPIC = CHAT_SERVICE_TOPIC + ".order-status";
    public static final String CHAT_SERVICE_GROUP = "chat-service-group";
}
