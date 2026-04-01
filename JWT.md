# JWT 说明文档

本文档汇总当前项目中与 JWT 相关的文件、配置与流程。

## 一、认证服务（mall-chat-auth）

### 1. 生成 Token
- 文件：`D:\新建文件夹\mall-chat\mall-chat-auth\src\main\java\com\mallchat\auth\util\JwtUtil.java`
- 作用：生成 JWT，包含 `userId`、`username`。

### 2. 登录逻辑
- 文件：`D:\新建文件夹\mall-chat\mall-chat-auth\src\main\java\com\mallchat\auth\service\impl\AuthServiceImpl.java`
- 作用：校验用户名密码，通过后生成 Token 并返回。

### 3. JWT 配置
- 文件：`D:\新建文件夹\mall-chat\mall-chat-auth\src\main\resources\application.yml`
- 关键配置：
  - `app.jwt.secret`
  - `app.jwt.expire-minutes`

## 二、业务服务（mall-chat-service）

### 1. 解析 Token
- 文件：`D:\新建文件夹\mall-chat\mall-chat-service\src\main\java\com\mallchat\util\JwtUtil.java`
- 作用：解析 JWT 并校验签名。

### 2. JWT 过滤器
- 文件：`D:\新建文件夹\mall-chat\mall-chat-service\src\main\java\com\mallchat\security\JwtAuthFilter.java`
- 作用：
  - 从 `Authorization: Bearer <token>` 读取 Token
  - 校验 Token
  - 提取 `userId` 写入 `UserContext`
  - 对部分公开接口跳过鉴权

### 3. 用户上下文
- 文件：`D:\新建文件夹\mall-chat\mall-chat-service\src\main\java\com\mallchat\security\UserContext.java`
- 作用：用 ThreadLocal 保存当前请求用户 ID。

### 4. 使用登录态的接口
- 文件：
  - `D:\新建文件夹\mall-chat\mall-chat-service\src\main\java\com\mallchat\controller\PostController.java`
  - `D:\新建文件夹\mall-chat\mall-chat-service\src\main\java\com\mallchat\controller\CommentController.java`
- 作用：
  - 发帖、发评、删帖、删评必须已登录
  - 从 `UserContext` 读取用户 ID

### 5. JWT 配置
- 文件：`D:\新建文件夹\mall-chat\mall-chat-service\src\main\resources\application.yml`
- 关键配置：
  - `app.jwt.secret`（必须与 mall-chat-auth 一致）
  - `app.jwt.expire-minutes`

## 三、前端（mall-chat-web）

### 1. 请求头注入
- 文件：`D:\新建文件夹\mall-chat\mall-chat-web\src\api.ts`
- 作用：
  - 登录成功后保存 `mallchat_token`
  - 调用受保护接口时自动附加 `Authorization: Bearer <token>`

## 四、整体流程

1. 用户在前端 `/auth` 登录
2. `mall-chat-auth` 生成 JWT 返回
3. 前端存储 Token
4. 前端访问受保护接口时携带 `Authorization` 头
5. `mall-chat-service` 解析 JWT 获取用户 ID

## 五、注意事项

- `app.jwt.secret` 两个服务必须一致
- 密钥长度需 >= 32 字符
- 过期时间由 `app.jwt.expire-minutes` 控制

