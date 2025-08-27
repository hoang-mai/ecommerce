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

FLUSH PRIVILEGES;
