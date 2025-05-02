# 二次元收藏品管理系统

## 1. 项目概述

二次元收藏品管理系统是一个专为二次元爱好者设计的 Web 应用，提供便捷、直观的平台用于管理个人收藏品。用户可以添加、修改、删除收藏品信息，为收藏品添加自定义标签，并进行分类管理。

## 2. 核心功能

- **用户管理**：用户注册、登录、信息管理
- **收藏品管理**：收藏品的增删改查，支持分页查询
- **标签管理**：用户自定义标签的增删改查
- **收藏品-标签关联管理**：关联和解除关联收藏品与标签

## 3. 技术栈

- **编程语言**：Java 17
- **Web 框架**：Spring Boot 3.4.4
- **安全框架**：Spring Security
- **ORM 框架**：MyBatis-Plus 3.5.12
- **数据库**：MySQL 8.0+
- **认证方式**：JWT (JSON Web Token) 0.11.5
- **构建工具**：Maven
- **其他工具库**：
  - Lombok：简化 Java 代码
  - Hutool：Java 工具类库

## 4. 系统架构

项目采用经典的三层架构：

- **表示层 (Controller)**：处理 API 请求和响应，实现 RESTful 接口
- **业务逻辑层 (Service)**：处理核心业务逻辑，实现业务规则
- **数据访问层 (Mapper/DAO)**：基于 MyBatis-Plus 实现与数据库的交互

## 5. 数据库设计

### 用户信息表（users）

| 字段名     | 类型         | 约束                        | 说明             |
| ---------- | ------------ | --------------------------- | ---------------- |
| user_id    | BIGINT       | PRIMARY KEY, AUTO_INCREMENT | 用户 ID          |
| username   | VARCHAR(50)  | NOT NULL, UNIQUE            | 用户名           |
| password   | VARCHAR(255) | NOT NULL                    | 密码（加密存储） |
| email      | VARCHAR(100) | UNIQUE                      | 邮箱             |
| created_at | DATETIME     | DEFAULT CURRENT_TIMESTAMP   | 注册时间         |

### 收藏品信息表（collections）

| 字段名         | 类型           | 约束                                                  | 说明          |
| -------------- | -------------- | ----------------------------------------------------- | ------------- |
| collection_id  | BIGINT         | PRIMARY KEY, AUTO_INCREMENT                           | 收藏品 ID     |
| user_id        | BIGINT         | NOT NULL, INDEX                                       | 关联的用户 ID |
| name           | VARCHAR(255)   | NOT NULL                                              | 收藏品名称    |
| purchase_date  | DATE           |                                                       | 购买日期      |
| purchase_price | DECIMAL(10, 2) |                                                       | 购买价格      |
| type           | VARCHAR(50)    |                                                       | 收藏品类型    |
| notes          | TEXT           |                                                       | 备注信息      |
| created_at     | DATETIME       | DEFAULT CURRENT_TIMESTAMP                             | 添加时间      |
| updated_at     | DATETIME       | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间      |

### 标签表（tags）

| 字段名      | 类型         | 约束                        | 说明          |
| ----------- | ------------ | --------------------------- | ------------- |
| tag_id      | BIGINT       | PRIMARY KEY, AUTO_INCREMENT | 标签 ID       |
| user_id     | BIGINT       | NOT NULL, INDEX             | 关联的用户 ID |
| tag_name    | VARCHAR(50)  | NOT NULL                    | 标签名        |
| description | VARCHAR(255) |                             | 标签描述      |
| created_at  | DATETIME     | DEFAULT CURRENT_TIMESTAMP   | 创建时间      |

**注意**：user_id 和 tag_name 有唯一组合索引约束

### 收藏品-标签关联表（collection_tags）

| 字段名        | 类型     | 约束                        | 说明            |
| ------------- | -------- | --------------------------- | --------------- |
| id            | BIGINT   | PRIMARY KEY, AUTO_INCREMENT | 关联 ID         |
| collection_id | BIGINT   | NOT NULL, INDEX             | 关联的收藏品 ID |
| tag_id        | BIGINT   | NOT NULL, INDEX             | 关联的标签 ID   |
| created_at    | DATETIME | DEFAULT CURRENT_TIMESTAMP   | 关联时间        |

**注意**：collection_id 和 tag_id 有唯一组合索引约束

## 6. API 接口设计

### 用户认证

- `POST /users/login`：用户登录（返回 JWT 令牌）
- `POST /users/register`：用户注册

### 收藏品管理（需要认证）

- `GET /collections`：获取当前用户收藏品列表（支持分页）
- `POST /collections`：添加收藏品
- `GET /collections/{id}`：获取单个收藏品详情
- `PUT /collections/{id}`：更新收藏品信息
- `DELETE /collections/{id}`：删除收藏品
- `GET /collections/{id}/tags`：获取收藏品关联的标签
- `POST /collections/{id}/tags`：为收藏品添加标签
- `DELETE /collections/{id}/tags/{tagId}`：移除收藏品的标签

### 标签管理（需要认证）

- `GET /tags`：获取当前用户的所有标签
- `POST /tags`：创建新标签
- `GET /tags/{id}`：获取标签详情
- `PUT /tags/{id}`：更新标签信息
- `DELETE /tags/{id}`：删除标签
- `GET /tags/{id}/collections`：获取标签关联的收藏品

## 7. 运行环境要求

- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+
- IDE（推荐 IntelliJ IDEA）

## 8. 配置说明

主要配置文件位于：

- `src/main/resources/application.yml`：主配置文件
- `src/main/resources/application-dev.yml`：开发环境配置
- `src/main/resources/application-pro.yml`：生产环境配置

关键配置项包括：

```yaml
# 服务器配置
server:
  port: 20611

# JWT配置
jwt:
  secret: YourSuperSecretKeyWhichShouldBeLongAndRandomAndStoredSecurely
  expiration: 86400000 # 24小时

# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/treasure?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&useSSL=true
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

**重要**：在生产环境中，请务必修改 JWT 密钥和数据库连接信息！

## 9. 本地启动步骤

1. **克隆仓库**

   ```bash
   git clone <repository-url>
   cd treasure
   ```

2. **数据库准备**

   - 创建 MySQL 数据库：`treasure`
   - 执行初始化 SQL 脚本：`docs/init-mysql.sql`

3. **修改配置**

   - 根据本地环境修改`application-dev.yml`中的数据库连接信息

4. **构建项目**

   ```bash
   mvn clean package
   ```

5. **运行项目**

   ```bash
   # 开发环境
   java -jar target/treasure-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

   # 或直接使用Maven运行
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

6. **访问 API**
   - 默认地址：`http://localhost:20611`
   - 可使用 Postman 等工具测试 API

## 10. 安全说明

- 本项目使用 Spring Security 进行安全控制
- 用户密码使用 BCrypt 算法加密存储
- 使用 JWT 进行无状态认证，令牌默认有效期为 24 小时
- API 访问需在请求头中携带`Authorization: Bearer <token>`
