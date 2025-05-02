-- 用户信息表
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `user_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(100) UNIQUE,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 为username添加唯一索引
ALTER TABLE `users` ADD CONSTRAINT uk_username UNIQUE (username);
-- 为email添加唯一索引
ALTER TABLE `users` ADD CONSTRAINT uk_email UNIQUE (email);

-- 收藏品信息表
DROP TABLE IF EXISTS `collections`;
CREATE TABLE `collections` (
    `collection`_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `purchase_date` DATE,
    `purchase_price` DECIMAL(10, 2),
    `type` VARCHAR(50),
    `notes` TEXT,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 为user_id添加普通索引
ALTER TABLE `collections` ADD INDEX ind_user_id (user_id);

-- 标签表
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
    `tag_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT NOT NULL,
    `tag_name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(255),
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 为user_id和tag_name添加唯一组合索引
ALTER TABLE `tags` ADD CONSTRAINT uk_user_id_tag_name UNIQUE (user_id, tag_name);
-- 为user_id添加普通索引
ALTER TABLE `tags` ADD INDEX ind_user_id (user_id);

-- 收藏品 - 标签关联表
DROP TABLE IF EXISTS `collection_tags`;
CREATE TABLE `collection_tags` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `collection_id` BIGINT NOT NULL,
    `tag_id` BIGINT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 为collection_id和tag_id添加唯一组合索引
ALTER TABLE `collection_tags` ADD CONSTRAINT uk_collection_id_tag_id UNIQUE (collection_id, tag_id);
-- 为collection_id添加普通索引
ALTER TABLE `collection_tags` ADD INDEX ind_collection_id (collection_id);
-- 为tag_id添加普通索引
ALTER TABLE collection_tags ADD INDEX ind_tag_id (tag_id);