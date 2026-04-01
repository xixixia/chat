-- Test data for user, category, tag, post, comment, sign_record, moderation tables

-- Users (10)
INSERT INTO user_account (id, username, password_hash, nickname, avatar_url, status)
VALUES
  (1, 'user01', 'hash_user01', '用户01', 'https://example.com/avatar/01.png', 1),
  (2, 'user02', 'hash_user02', '用户02', 'https://example.com/avatar/02.png', 1),
  (3, 'user03', 'hash_user03', '用户03', 'https://example.com/avatar/03.png', 1),
  (4, 'user04', 'hash_user04', '用户04', 'https://example.com/avatar/04.png', 1),
  (5, 'user05', 'hash_user05', '用户05', 'https://example.com/avatar/05.png', 1),
  (6, 'user06', 'hash_user06', '用户06', 'https://example.com/avatar/06.png', 1),
  (7, 'user07', 'hash_user07', '用户07', 'https://example.com/avatar/07.png', 1),
  (8, 'user08', 'hash_user08', '用户08', 'https://example.com/avatar/08.png', 1),
  (9, 'user09', 'hash_user09', '用户09', 'https://example.com/avatar/09.png', 1),
  (10, 'user10', 'hash_user10', '用户10', 'https://example.com/avatar/10.png', 1)
ON DUPLICATE KEY UPDATE
  username = VALUES(username),
  password_hash = VALUES(password_hash),
  nickname = VALUES(nickname),
  avatar_url = VALUES(avatar_url),
  status = VALUES(status);

-- Categories (13)
INSERT INTO category (id, name, parent_id, level, sort, status)
VALUES
  (1, '默认', NULL, 1, 0, 1),
  (2, '技术', NULL, 1, 10, 1),
  (3, '生活', NULL, 1, 9, 1),
  (4, '后端', 2, 2, 10, 1),
  (5, '前端', 2, 2, 9, 1),
  (6, '数据库', 2, 2, 8, 1),
  (7, '旅行', 3, 2, 10, 1),
  (8, '运动', 3, 2, 9, 1),
  (9, 'Spring', 4, 3, 10, 1),
  (10, 'Vue', 5, 3, 10, 1),
  (11, 'MySQL', 6, 3, 10, 1),
  (12, '徒步', 7, 3, 9, 1),
  (13, '跑步', 8, 3, 9, 1)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  parent_id = VALUES(parent_id),
  level = VALUES(level),
  sort = VALUES(sort),
  status = VALUES(status);

-- Tags (8)
INSERT INTO tag (id, name, status)
VALUES
  (1, 'Java', 1),
  (2, 'Spring', 1),
  (3, 'Vue', 1),
  (4, 'MySQL', 1),
  (5, 'Redis', 1),
  (6, 'OAuth', 1),
  (7, '生活', 1),
  (8, '运动', 1)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  status = VALUES(status);

-- Sensitive words (6)
INSERT INTO sensitive_word (id, word, level, enabled)
VALUES
  (1, '高危词示例', 3, 1),
  (2, '中危词示例', 2, 1),
  (3, '低危词示例', 1, 1),
  (4, '测试敏感', 2, 1),
  (5, '广告', 1, 1),
  (6, '辱骂示例', 3, 1)
ON DUPLICATE KEY UPDATE
  word = VALUES(word),
  level = VALUES(level),
  enabled = VALUES(enabled);

-- Sensitive whitelist (2)
INSERT INTO sensitive_whitelist (id, word, enabled)
VALUES
  (1, '白名单示例', 1),
  (2, '广告位', 1)
ON DUPLICATE KEY UPDATE
  word = VALUES(word),
  enabled = VALUES(enabled);

-- Posts (20)
INSERT INTO post (id, user_id, category_id, title, content, status, audit_status, audit_reason, audit_updated_at)
VALUES
  (1, 1, 9, 'Spring Boot 分层实践', '整理控制层、服务层、数据层的职责边界与常见坑。', 1, 1, NULL, NULL),
  (2, 2, 10, 'Vue3 组件封装规范', '从目录结构、命名、Props 设计谈组件封装。', 1, 1, NULL, NULL),
  (3, 3, 11, 'MySQL 索引优化案例', '索引失效排查与优化思路记录。', 1, 1, NULL, NULL),
  (4, 4, 12, '周末轻徒步路线', '适合新手的城市周边轻徒步路线推荐。', 1, 1, NULL, NULL),
  (5, 5, 13, '10km 跑步训练计划', '从 3km 进阶到 10km 的阶段性训练安排。', 1, 1, NULL, NULL),
  (6, 6, 4, '后端接口幂等设计', '结合实际业务的幂等性设计方案。', 1, 1, NULL, NULL),
  (7, 7, 5, '前端性能优化清单', '首屏、资源拆分、缓存策略的实践总结。', 1, 1, NULL, NULL),
  (8, 8, 6, '数据库字段设计建议', '字段类型选择与索引配合建议。', 1, 1, NULL, NULL),
  (9, 9, 7, '城市短途旅行攻略', '两天一夜的轻量旅行方案。', 1, 1, NULL, NULL),
  (10, 10, 8, '跑步装备入门', '跑鞋、手表和训练App的入门选择。', 1, 1, NULL, NULL),
  (11, 1, 2, '技术学习路线规划', '以项目为导向的学习路线与资源。', 1, 1, NULL, NULL),
  (12, 2, 3, '生活效率提升工具', '日常效率提升的小工具清单。', 1, 1, NULL, NULL),
  (13, 3, 9, 'Spring 事务传播', '传播行为在实际场景中的应用。', 1, 1, NULL, NULL),
  (14, 4, 10, 'Vue 状态管理取舍', '简单项目如何权衡 Pinia 与本地状态。', 1, 1, NULL, NULL),
  (15, 5, 11, 'SQL 慢查询定位', '慢查询日志与执行计划分析。', 1, 1, NULL, NULL),
  (16, 6, 12, '徒步装备清单', '轻量化徒步装备推荐。', 1, 1, NULL, NULL),
  (17, 7, 13, '跑步配速策略', '如何设定训练与比赛配速。', 1, 1, NULL, NULL),
  (18, 8, 4, '后端日志规范', '日志分级、字段与追踪ID规范。', 1, 1, NULL, NULL),
  (19, 9, 5, '前端表单校验实战', '表单校验规则与错误提示体验优化。', 1, 0, '命中敏感词，待审核', '2026-03-25 09:00:00'),
  (20, 10, 1, '默认分类测试帖', '包含中危词示例，需要审核。', 1, 0, '命中敏感词，待审核', '2026-03-25 09:10:00')
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  category_id = VALUES(category_id),
  title = VALUES(title),
  content = VALUES(content),
  status = VALUES(status),
  audit_status = VALUES(audit_status),
  audit_reason = VALUES(audit_reason),
  audit_updated_at = VALUES(audit_updated_at);

-- Post Tags (20)
INSERT INTO post_tag (id, post_id, tag_id)
VALUES
  (1, 1, 2),
  (2, 2, 3),
  (3, 3, 4),
  (4, 4, 7),
  (5, 5, 8),
  (6, 6, 1),
  (7, 7, 3),
  (8, 8, 4),
  (9, 9, 7),
  (10, 10, 8),
  (11, 11, 1),
  (12, 12, 7),
  (13, 13, 2),
  (14, 14, 3),
  (15, 15, 4),
  (16, 16, 7),
  (17, 17, 8),
  (18, 18, 1),
  (19, 19, 3),
  (20, 20, 6)
ON DUPLICATE KEY UPDATE
  post_id = VALUES(post_id),
  tag_id = VALUES(tag_id);

-- Comments (20, with replies)
INSERT INTO comment (id, post_id, parent_id, user_id, content, status, audit_status, audit_reason, audit_updated_at)
VALUES
  (1, 1, NULL, 2, '分层清晰，很有帮助。', 1, 1, NULL, NULL),
  (2, 1, 1, 1, '谢谢认可，后续补充实践细节。', 1, 1, NULL, NULL),
  (3, 2, NULL, 3, '组件封装的目录结构能展开说下吗？', 1, 1, NULL, NULL),
  (4, 2, 3, 2, '我晚点补一份示例。', 1, 1, NULL, NULL),
  (5, 3, NULL, 4, '索引失效常见场景写得很实用。', 1, 1, NULL, NULL),
  (6, 4, NULL, 5, '路线里有补给点吗？', 1, 1, NULL, NULL),
  (7, 4, 6, 4, '起点附近就有补给点。', 1, 1, NULL, NULL),
  (8, 5, NULL, 6, '计划挺科学，准备试试。', 1, 1, NULL, NULL),
  (9, 6, NULL, 7, '幂等性方案写得很清楚。', 1, 1, NULL, NULL),
  (10, 7, NULL, 8, '首屏优化这段很有帮助。', 1, 1, NULL, NULL),
  (11, 8, NULL, 9, '字段设计建议不错。', 1, 1, NULL, NULL),
  (12, 9, NULL, 10, '这个攻略很实用。', 1, 1, NULL, NULL),
  (13, 10, NULL, 1, '装备推荐很细致。', 1, 1, NULL, NULL),
  (14, 11, NULL, 2, '学习路线可以参考一下。', 1, 1, NULL, NULL),
  (15, 12, NULL, 3, '效率工具清单收藏了。', 1, 1, NULL, NULL),
  (16, 13, NULL, 4, '事务传播这块讲清楚了。', 1, 1, NULL, NULL),
  (17, 14, NULL, 5, 'Pinia 的取舍点很中肯。', 1, 1, NULL, NULL),
  (18, 15, NULL, 6, '慢查询分析步骤清晰。', 1, 1, NULL, NULL),
  (19, 16, NULL, 7, '装备清单很实用。', 1, 0, '命中敏感词，待审核', '2026-03-25 09:05:00'),
  (20, 17, NULL, 8, '配速策略有参考价值。', 1, 2, '命中高危词，已拒绝', '2026-03-25 09:08:00')
ON DUPLICATE KEY UPDATE
  post_id = VALUES(post_id),
  parent_id = VALUES(parent_id),
  user_id = VALUES(user_id),
  content = VALUES(content),
  status = VALUES(status),
  audit_status = VALUES(audit_status),
  audit_reason = VALUES(audit_reason),
  audit_updated_at = VALUES(audit_updated_at);

-- Sign records (9)
INSERT INTO sign_record (id, user_id, sign_date, sign_time, is_retro, reward_status)
VALUES
  (1, 1, '2026-03-18', '2026-03-18 08:10:00', 0, 1),
  (2, 2, '2026-03-19', '2026-03-19 08:20:00', 0, 0),
  (3, 3, '2026-03-20', '2026-03-20 08:15:00', 0, 1),
  (4, 4, '2026-03-21', '2026-03-21 08:05:00', 0, 0),
  (5, 5, '2026-03-22', '2026-03-22 08:30:00', 0, 1),
  (6, 6, '2026-03-23', '2026-03-23 08:25:00', 0, 0),
  (7, 7, '2026-03-24', '2026-03-24 08:12:00', 0, 1),
  (8, 8, '2026-03-24', '2026-03-24 08:18:00', 0, 0),
  (9, 9, '2026-03-24', '2026-03-24 08:22:00', 1, 0)
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  sign_date = VALUES(sign_date),
  sign_time = VALUES(sign_time),
  is_retro = VALUES(is_retro),
  reward_status = VALUES(reward_status);

-- Content audit records (6)
INSERT INTO content_audit (id, biz_type, biz_id, status, hit_level, hit_words, operator_id, reason)
VALUES
  (1, 'post', 19, 0, 2, '中危词示例', NULL, '命中敏感词，待审核'),
  (2, 'post', 20, 0, 2, '中危词示例', NULL, '命中敏感词，待审核'),
  (3, 'comment', 19, 0, 2, '测试敏感', NULL, '命中敏感词，待审核'),
  (4, 'comment', 20, 2, 3, '高危词示例', 1, '命中高危词，已拒绝'),
  (5, 'post', 1, 1, 1, '低危词示例', 1, '命中低危词，自动通过'),
  (6, 'comment', 5, 1, 1, '低危词示例', 1, '命中低危词，自动通过')
ON DUPLICATE KEY UPDATE
  biz_type = VALUES(biz_type),
  biz_id = VALUES(biz_id),
  status = VALUES(status),
  hit_level = VALUES(hit_level),
  hit_words = VALUES(hit_words),
  operator_id = VALUES(operator_id),
  reason = VALUES(reason);
