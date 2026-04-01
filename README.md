# Mall Chat

## 项目简介
Mall Chat 是一个基于 Spring Boot + Vue 的论坛型应用，拆分为业务服务与认证服务，提供发帖、评论、登录注册、第三方登录与验证码能力。

## 功能概览
- 论坛基础：发帖、看帖、评论、回复、删除（软删除）
- 账号体系：JWT 登录与注册，短信验证码登录
- 第三方登录：QQ OAuth2（通用 OAuth 接口预留微信）
- 注册方式：短信注册（腾讯云短信）、邮箱注册（SMTP）
- 帖子分类（最多 3 级，发帖必选）
- 帖子标签（系统自动生成，预留模型接口）
- 签到功能：每日签到、补签、连续签到（Redis Bitmap + 数据库记录）
- 服务拆分：业务服务 + 认证服务
- Consul 服务注册
- Redis 存储验证码、限流与签到状态
- Markdown 富文本编辑（前端 Toast UI Editor）
- 权限控制：未登录返回 401，无权限返回 403
- 内容审核：敏感词过滤 + 自动审核 + 管理端审核接口

## 项目结构
```
mall-chat/
├── mall-chat-service/            # 业务服务
│   ├── pom.xml
│   ├── src/
│   │   └── main/
│   │       ├── java/com/mallchat/...
│   │       └── resources/
│   │           ├── application.yml
│   │           ├── mapper/
│   │           └── schema.sql
│   └── api.http
├── mall-chat-auth/               # 认证服务
│   ├── pom.xml
│   ├── src/
│   │   └── main/
│   │       ├── java/com/mallchat/auth/...
│   │       └── resources/
│   │           ├── application.yml
│   │           ├── mapper/
│   │           └── schema-oauth.sql
├── mall-chat-web/                # 前端
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
│       ├── api/
│       ├── router.ts
│       ├── styles.css
│       ├── components/
│       │   ├── ToastEditor.vue
│       │   └── ToastViewer.vue
│       └── views/
│           ├── AuthView.vue
│           ├── PostListView.vue
│           ├── PostDetailView.vue
│           └── QqCallbackView.vue
├── ALTER.sql                      # 数据库升级脚本
├── seed_test_data.sql             # 测试数据脚本
└── JWT.md                         # JWT 说明文档
```

## 数据库说明
### 核心表
- `user_account`：用户账号
- `post`：帖子（含 `audit_status` / `audit_reason` / `audit_updated_at`）
- `comment`：评论（含 `audit_status` / `audit_reason` / `audit_updated_at`）
- `category`：帖子分类（最多 3 级）
- `tag`：标签
- `post_tag`：帖子-标签关联
- `sign_record`：签到记录
- `user_oauth`：第三方账号绑定

### 审核相关表
- `sensitive_word`：敏感词库（`level` 1低/2中/3高）
- `sensitive_whitelist`：敏感词白名单
- `content_audit`：内容审核记录

### 审核状态定义
- `audit_status`：0待审，1通过，2拒绝
- 帖子/评论列表与详情默认只展示 `audit_status=1`

## 快速启动
### 1. 准备数据库
```sql
CREATE DATABASE mall_chat DEFAULT CHARACTER SET utf8mb4;
```

导入表结构（初次）：
```bash
mysql -u root -p mall_chat < D:\新建文件夹\mall-chat\mall-chat-service\src\main\resources\schema.sql
mysql -u root -p mall_chat < D:\新建文件夹\mall-chat\mall-chat-auth\src\main\resources\schema-oauth.sql
```

已有库升级：
```bash
mysql -u root -p mall_chat < D:\新建文件夹\mall-chat\ALTER.sql
```

可选导入测试数据：
```bash
mysql -u root -p mall_chat < D:\新建文件夹\mall-chat\seed_test_data.sql
```

### 2. 启动 Redis
确保 Redis 已启动并监听 `localhost:6379`。

### 3. 启动 Consul
确保 Consul 已启动并监听 `localhost:8500`。

### 4. 启动后端服务
业务服务：
```bash
cd D:\新建文件夹\mall-chat\mall-chat-service
mvn spring-boot:run
```

认证服务：
```bash
cd D:\新建文件夹\mall-chat\mall-chat-auth
mvn spring-boot:run
```

### 5. 启动前端
```bash
cd D:\新建文件夹\mall-chat\mall-chat-web
npm install
npm run dev
```

前端访问：`http://localhost:5173`

## 关键配置
### 业务服务（mall-chat-service）
`src/main/resources/application.yml`
- MySQL
- Redis
- SMTP 邮箱
- 腾讯云短信
- JWT（必须与 mall-chat-auth 一致）
- 分类与标签
  - `app.tag.model-endpoint`：模型接口地址（可为空，自动回退本地规则）
- 图形验证码过期时间
  - `app.captcha.expire-minutes`
- 审核管理员
  - `app.moderation.admin-user-ids`：审核管理员用户ID列表（逗号分隔）
- 过滤器鉴权规则（JWT）

### 认证服务（mall-chat-auth）
`src/main/resources/application.yml`
- MySQL
- Redis（短信验证码登录使用）
- JWT（必须与 mall-chat-service 一致）
- QQ OAuth 参数（`app.qq.*`）

### QQ 互联回调地址
请在 QQ 互联后台配置回调地址：
```
http://localhost:5173/auth/qq-callback
```

## 审核词库维护
### 维护方式
- 直接维护数据库表 `sensitive_word` 与 `sensitive_whitelist`
- 生效规则：`enabled=1` 的词会被加载

### 词库示例
```sql
INSERT INTO sensitive_word (word, level, enabled) VALUES
  ('高危词示例', 3, 1),
  ('中危词示例', 2, 1),
  ('低危词示例', 1, 1);

INSERT INTO sensitive_whitelist (word, enabled) VALUES
  ('可豁免词示例', 1);
```

### 刷新词库
应用启动时自动加载词库。如需热更新，可重启服务使其重新加载。

## JWT 与过滤器说明
### JWT 使用
- 登录成功后返回 `token`，前端需在请求头添加：`Authorization: Bearer <token>`。
- `mall-chat-service` 与 `mall-chat-auth` 的 `app.jwt.secret` 必须一致。

### 过滤器鉴权（mall-chat-service）
鉴权由 `JwtAuthFilter` 统一处理，规则如下：

| 方法 | 路径 | 是否需要登录 | 说明 |
| --- | --- | --- | --- |
| GET | `/health/**` | 否 | 健康检查 |
| POST | `/register/**` | 否 | 注册相关 |
| GET | `/posts` | 否 | 帖子列表 |
| GET | `/posts/{id}` | 否 | 帖子详情 |
| GET | `/comments?postId=` | 否 | 评论列表 |
| GET | `/categories` | 否 | 分类列表 |
| POST | `/posts` | 是 | 创建帖子 |
| POST | `/comments` | 是 | 创建评论 |
| DELETE | `/posts/{id}` | 是 | 删除帖子（作者） |
| DELETE | `/comments/{id}` | 是 | 删除评论（作者） |
| GET | `/admin/audit` | 是 | 审核列表（管理员） |
| POST | `/admin/audit/{id}/pass` | 是 | 审核通过（管理员） |
| POST | `/admin/audit/{id}/reject` | 是 | 审核拒绝（管理员） |

返回码：
- 未登录：`401`
- 无权限：`403`

## 接口文档
### 业务服务（mall-chat-service，端口 8080）
#### 健康检查
- `GET /health`
- `GET /health/db`

#### 分类
- `GET /categories`
  - 返回：`[{ id, name, parentId, level, sort }]`

#### 帖子
- `GET /posts?page=&size=&title=&categoryId=`
  - 返回：`{ total, list }`
  - 列表项包含：`categoryId`, `categoryName`, `tags`, `auditStatus`
- `GET /posts/{id}`
  - 返回包含：`categoryId`, `categoryName`, `tags`, `auditStatus`
- `POST /posts`（需要登录）
  - Body: `{ categoryId, title, content }`
  - 返回：`{ id, auditStatus, message }`
- `DELETE /posts/{id}`（需要登录，软删除，仅作者可删）

#### 评论
- `GET /comments?postId=`
- `POST /comments`（需要登录）
  - Body: `{ postId, content, parentId? }`
  - 返回：`{ id, auditStatus, message }`
- `DELETE /comments/{id}`（需要登录，软删除，仅作者可删）

#### 图形验证码
- `POST /register/captcha`
  - 返回：`{ key, image }`（`image` 为 base64 的 svg）

#### 短信注册
- `POST /register/sms/send`
  - Body: `{ phone, captchaKey, captchaCode }`
- `POST /register/sms`
  - Body: `{ phone, code, password, nickname }`

#### 邮箱注册
- `POST /register/email/send`
  - Body: `{ email, captchaKey, captchaCode }`
- `POST /register/email`
  - Body: `{ email, code, password, nickname }`

#### 签到
- `POST /sign/check-in`（需要登录）
  - 返回：`{ signedToday, firstSign, streak, total, year, month, day, targetDate, retro }`
- `POST /sign/retro?date=yyyy-MM-dd`（需要登录）
  - 返回同上
- `GET /sign/status`（需要登录）
  - 返回：`{ signedToday, streak, total, year, month, day }`
- `GET /sign/calendar?year=&month=`（需要登录）
  - 返回：`{ year, month, daysInMonth, signedDays }`
- `POST /sign/reward/dispatch`（需要登录，预留）
  - 返回：`奖励发放暂未实现`

#### 审核（管理员）
- `GET /admin/audit?status=&page=&size=`
- `POST /admin/audit/{id}/pass`
  - Body: `{ reason? }`
- `POST /admin/audit/{id}/reject`
  - Body: `{ reason? }`

### 认证服务（mall-chat-auth，端口 8081）
#### 注册
- `POST /auth/register`
  - Body: `{ username, password, nickname }`

#### 登录
- `POST /auth/login`
  - Body: `{ username, password }`
  - 返回：`{ userId, username, nickname, token }`
- `POST /auth/login/sms`
  - Body: `{ phone, code }`
  - 返回：`{ userId, username, nickname, token }`

#### QQ 登录
- `GET /oauth/qq/authorize`（获取授权地址，兼容保留）
- `GET /oauth/qq/login?code=xxx`（登录并返回 JWT，兼容保留）

#### 通用第三方登录（预留微信）
- `GET /oauth/{provider}/authorize`（获取授权地址）
- `GET /oauth/{provider}/login?code=xxx`（登录并返回 JWT）
- 已支持：`provider=qq`
- 预留：`provider=wechat`（未实现）

## 说明
- JWT 密钥长度需 >= 32 字符，`mall-chat-service` 与 `mall-chat-auth` 必须一致。
- 短信手机号需使用 E.164 格式（如 `+8613812345678`）。
- 验证码限制：
  - 发送间隔：`app.verify.send-interval-seconds`
  - 错误次数上限：`app.verify.max-attempts`
  - 过期时间：`app.verify.expire-minutes`
- 前端发帖/评论/删除若未登录会自动跳转登录页。
- 登录成功后由认证服务通过 Feign 调用业务服务 `/sign/check-in` 自动签到（依赖 Consul 服务发现，失败不影响登录）。
