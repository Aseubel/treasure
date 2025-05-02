-- 用户信息表
DROP TABLE users CASCADE CONSTRAINTS;

-- 创建用户表
CREATE TABLE users (
    user_id NUMBER(19) PRIMARY KEY,
    username VARCHAR2(50) NOT NULL,
    password VARCHAR2(255) NOT NULL,
    email VARCHAR2(100),
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- 创建序列
DROP SEQUENCE users_seq;
CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 创建触发器实现自增
CREATE OR REPLACE TRIGGER users_trg
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    IF :NEW.user_id IS NULL THEN
        SELECT users_seq.NEXTVAL INTO :NEW.user_id FROM DUAL;
    END IF;
END;
/

-- 为username添加唯一索引
ALTER TABLE users ADD CONSTRAINT uk_username UNIQUE (username);
-- 为email添加唯一索引
ALTER TABLE users ADD CONSTRAINT uk_email UNIQUE (email);

-- 收藏品信息表
DROP TABLE collections CASCADE CONSTRAINTS;

-- 创建收藏品表
CREATE TABLE collections (
    collection_id NUMBER(19) PRIMARY KEY,
    user_id NUMBER(19) NOT NULL,
    name VARCHAR2(255) NOT NULL,
    purchase_date DATE,
    purchase_price NUMBER(10, 2),
    type VARCHAR2(50),
    notes CLOB,
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP,
    updated_at TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- 创建序列
DROP SEQUENCE collections_seq;
CREATE SEQUENCE collections_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 创建触发器实现自增
CREATE OR REPLACE TRIGGER collections_trg
BEFORE INSERT ON collections
FOR EACH ROW
BEGIN
    IF :NEW.collection_id IS NULL THEN
        SELECT collections_seq.NEXTVAL INTO :NEW.collection_id FROM DUAL;
    END IF;
END;
/

-- 创建触发器实现更新时间自动更新
CREATE OR REPLACE TRIGGER collections_upd_trg
BEFORE UPDATE ON collections
FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END;
/

-- 为user_id添加普通索引
CREATE INDEX ind_user_id ON collections(user_id);

-- 标签表
DROP TABLE tags CASCADE CONSTRAINTS;

-- 创建标签表
CREATE TABLE tags (
    tag_id NUMBER(19) PRIMARY KEY,
    user_id NUMBER(19) NOT NULL,
    tag_name VARCHAR2(50) NOT NULL,
    description VARCHAR2(255),
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- 创建序列
DROP SEQUENCE tags_seq;
CREATE SEQUENCE tags_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 创建触发器实现自增
CREATE OR REPLACE TRIGGER tags_trg
BEFORE INSERT ON tags
FOR EACH ROW
BEGIN
    IF :NEW.tag_id IS NULL THEN
        SELECT tags_seq.NEXTVAL INTO :NEW.tag_id FROM DUAL;
    END IF;
END;
/

-- 为user_id和tag_name添加唯一组合索引
ALTER TABLE tags ADD CONSTRAINT uk_user_id_tag_name UNIQUE (user_id, tag_name);
-- 为user_id添加普通索引
CREATE INDEX ind_user_id ON tags(user_id);

-- 收藏品 - 标签关联表
DROP TABLE collection_tags CASCADE CONSTRAINTS;

-- 创建收藏品-标签关联表
CREATE TABLE collection_tags (
    id NUMBER(19) PRIMARY KEY,
    collection_id NUMBER(19) NOT NULL,
    tag_id NUMBER(19) NOT NULL,
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- 创建序列
DROP SEQUENCE collection_tags_seq;
CREATE SEQUENCE collection_tags_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

-- 创建触发器实现自增
CREATE OR REPLACE TRIGGER collection_tags_trg
BEFORE INSERT ON collection_tags
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT collection_tags_seq.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

-- 为collection_id和tag_id添加唯一组合索引
ALTER TABLE collection_tags ADD CONSTRAINT uk_collection_id_tag_id UNIQUE (collection_id, tag_id);
-- 为collection_id添加普通索引
CREATE INDEX ind_collection_id ON collection_tags(collection_id);
-- 为tag_id添加普通索引
CREATE INDEX ind_tag_id ON collection_tags(tag_id);