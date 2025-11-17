-- Tạo database cho Temporal
CREATE DATABASE IF NOT EXISTS temporal;
CREATE DATABASE IF NOT EXISTS temporal_visibility;
CREATE USER IF NOT EXISTS 'temporal'@'%' IDENTIFIED BY 'temporal';
GRANT ALL PRIVILEGES ON temporal.* TO 'temporal'@'%';
GRANT ALL PRIVILEGES ON temporal_visibility.* TO 'temporal'@'%';

-- Tạo database cho Keycloak
CREATE DATABASE IF NOT EXISTS keycloak;
CREATE USER IF NOT EXISTS 'keycloak'@'%' IDENTIFIED BY 'keycloak';
GRANT ALL PRIVILEGES ON keycloak.* TO 'keycloak'@'%';

-- Tạo database cho user-service
CREATE DATABASE IF NOT EXISTS user_service;
CREATE USER IF NOT EXISTS 'user_service'@'%' IDENTIFIED BY 'user_service';
GRANT ALL PRIVILEGES ON user_service.* TO 'user_service'@'%';

-- Tạo database cho auth-service
CREATE DATABASE IF NOT EXISTS auth_service;
CREATE USER IF NOT EXISTS 'auth_service'@'%' IDENTIFIED BY 'auth_service';
GRANT ALL PRIVILEGES ON auth_service.* TO 'auth_service'@'%';

-- Tạo database cho product-service
CREATE DATABASE IF NOT EXISTS product_service;
CREATE USER IF NOT EXISTS 'product_service'@'%' IDENTIFIED BY 'product_service';
GRANT ALL PRIVILEGES ON product_service.* TO 'product_service'@'%';

-- Tạo database cho notification-service
CREATE DATABASE IF NOT EXISTS notification_service;
CREATE USER IF NOT EXISTS 'notification_service'@'%' IDENTIFIED BY 'notification_service';
GRANT ALL PRIVILEGES ON notification_service.* TO 'notification_service'@'%';

FLUSH PRIVILEGES;
