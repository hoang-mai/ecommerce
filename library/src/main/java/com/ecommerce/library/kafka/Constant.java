package com.ecommerce.library.kafka;

public final class Constant {

    // Common Topics
    public static final String CREATE_PRODUCT_CACHE_TOPIC = "create-product-cache";
    public static final String CREATE_ORDER_TOPIC = "create-order";
    public static final String CREATE_SHOP_TOPIC = "create-shop";
    public static final String UPDATE_USER_TOPIC = "update-user";
    public static final String CREATE_USER_TOPIC = "create-user";
    public static final String UPDATE_ACCOUNT_STATUS_TOPIC = "update-account-status";
    public static final String UPDATE_AVATAR_URL_TOPIC = "update-avatar-url";

    // Read Service Kafka Topics and Group
    public static final String READ_SERVICE_TOPIC = "read-service-topic";
    public static final String READ_SERVICE_GROUP = "read-service-group";
    public static final String UPDATE_ROLE_TOPIC = READ_SERVICE_TOPIC + ".update-role";
    public static final String CREATE_ORDER_VIEW_TOPIC = READ_SERVICE_TOPIC + ".create-order-view";
    public static final String UPDATE_ORDER_STATUS_VIEW_TOPIC = READ_SERVICE_TOPIC + ".update-order-status-view";
    public static final String CREATE_PRODUCT_TOPIC = READ_SERVICE_TOPIC + ".create-product";
    public static final String UPDATE_STATUS_PRODUCT_TOPIC = READ_SERVICE_TOPIC + ".update-status-product";
    public static final String UPDATE_STATUS_PRODUCT_VARIANT_TOPIC = READ_SERVICE_TOPIC + ".update-status-product-variant";
    public static final String CREATE_CART_TOPIC = READ_SERVICE_TOPIC + ".create-cart";
    public static final String UPDATE_CART_ITEM_TOPIC = READ_SERVICE_TOPIC + ".update-cart-item";
    public static final String DELETE_CART_ITEM_TOPIC = READ_SERVICE_TOPIC + ".delete-cart-item";
    public static final String DELETE_PRODUCT_CART_ITEM_TOPIC = READ_SERVICE_TOPIC + ".delete-product-cart-item";
    public static final String UPDATE_SHOP_STATUS_TOPIC = READ_SERVICE_TOPIC + ".update-shop-status";
    public static final String CREATE_REVIEW_VIEW_TOPIC = READ_SERVICE_TOPIC + ".create-review-view";
    public static final String UPDATE_REVIEW_VIEW_TOPIC = READ_SERVICE_TOPIC + ".update-review-view";
    public static final String DELETE_REVIEW_VIEW_TOPIC = READ_SERVICE_TOPIC + ".delete-review-view";
    public static final String CREATE_REVIEW_REPLY_TOPIC = READ_SERVICE_TOPIC + ".create-review-reply";
    public static final String UPDATE_REVIEW_REPLY_TOPIC = READ_SERVICE_TOPIC + ".update-review-reply";
    public static final String DELETE_REVIEW_REPLY_TOPIC = READ_SERVICE_TOPIC + ".delete-review-reply";
    // Order Service Kafka Topics and Group
    public static final String ORDER_SERVICE_TOPIC = "order-service-topic";
    public static final String ORDER_SERVICE_GROUP = "order-service-group";
    public static final String UPDATE_ORDER_STATUS_TOPIC = ORDER_SERVICE_TOPIC + ".update-order-status";
    public static final String CREATE_SHOP_CACHE_TOPIC = ORDER_SERVICE_TOPIC + ".create-shop-cache";

    // Product Service Kafka Topics and Group
    public static final String PRODUCT_SERVICE_TOPIC = "product-service-topic";
    public static final String PRODUCT_SERVICE_GROUP = "product-service-group";


    // Notification Service Kafka Topics and Group
    public static final String NOTIFICATION_SERVICE_TOPIC = "notification-service-topic";
    public static final String ORDER_STATUS_TOPIC = NOTIFICATION_SERVICE_TOPIC + ".order-status";
    public static final String NOTIFICATION_SERVICE_GROUP = "notification-service-group";

    public static final String REVIEW_TOPIC = "review-service-topic";
    public static final String REVIEW_SERVICE_GROUP = "review-service-group";

    public static final String CHAT_SERVICE_TOPIC = "chat-service-topic";
    public static final String CHAT_SERVICE_GROUP = "chat-service-group";
}
