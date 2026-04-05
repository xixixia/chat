# 代码审查报告（2026-04-01）

## 范围
- 提交：`7d44e560583f69798aba9448d67aaf99cc07f68b`
- 重点：点赞功能（前后端）

## 主要问题

### 1) 前端构建阻塞：新增导出指向不存在的模块
- `src/api/index.ts` 新增了 `export * from "./like";`，但仓库中不存在 `src/api/like.ts`。
- 这会直接导致 Vite/Rollup 构建失败，属于发布阻塞问题。

### 2) 前端调用阻塞：页面代码引用了未定义 API
- `PostListView.vue` 与 `PostDetailView.vue` 通过 `../api` 解构导入 `likePost/unlikePost`。
- 由于 `./like` 模块不存在，这两个符号无法解析，页面点赞交互不可用。

### 3) 后端实现不完整风险：服务层已依赖 LikeService
- `PostServiceImpl` 已注入并调用 `LikeService`（用于填充 `likedByMe`）。
- 但当前仓库未检索到 `LikeService` 接口/实现文件，说明本次提交可能遗漏了关键后端代码，存在编译或运行风险。

## 建议修复顺序
1. 先补齐 `mall-chat-web/src/api/like.ts`，并导出 `likePost/unlikePost`（以及必要的状态查询接口）。
2. 补齐后端 `LikeService`、Controller、Mapper 与 XML，并与 `schema.sql` 的 `user_like` 保持一致。
3. 补充最小可用回归：
   - 前端：`npm run build`
   - 后端：`mvn test`（至少 `mvn -DskipTests compile`）
