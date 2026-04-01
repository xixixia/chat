-- Users
CREATE TABLE IF NOT EXISTS user_account (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(32) NOT NULL UNIQUE COMMENT '用户名',
  password_hash VARCHAR(128) NOT NULL COMMENT '密码哈希',
  nickname VARCHAR(32) NOT NULL COMMENT '昵称',
  avatar_url VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0禁用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账号';

-- Posts
CREATE TABLE IF NOT EXISTS post (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
  user_id BIGINT NOT NULL COMMENT '作者用户ID',
  category_id BIGINT NOT NULL DEFAULT 1 COMMENT '分类ID',
  title VARCHAR(120) NOT NULL COMMENT '标题',
  content TEXT NOT NULL COMMENT '内容',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0删除',
  like_count BIGINT NOT NULL DEFAULT 0 COMMENT '点赞数',
  audit_status TINYINT NOT NULL DEFAULT 1 COMMENT '审核状态：0待审，1通过，2拒绝',
  audit_reason VARCHAR(255) DEFAULT NULL COMMENT '审核原因',
  audit_updated_at DATETIME DEFAULT NULL COMMENT '审核时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_post_user_id (user_id),
  INDEX idx_post_category_id (category_id),
  INDEX idx_post_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子';

-- Categories
CREATE TABLE IF NOT EXISTS category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  name VARCHAR(64) NOT NULL COMMENT '分类名称',
  parent_id BIGINT DEFAULT NULL COMMENT '父级ID',
  level TINYINT NOT NULL DEFAULT 1 COMMENT '层级',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0禁用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_category_parent_id (parent_id),
  INDEX idx_category_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子分类';

INSERT IGNORE INTO category (id, name, parent_id, level, sort, status)
VALUES (1, '默认', NULL, 1, 0, 1);

-- Tags
CREATE TABLE IF NOT EXISTS tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
  name VARCHAR(64) NOT NULL UNIQUE COMMENT '标签名称',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0禁用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签';

-- Post Tags
CREATE TABLE IF NOT EXISTS post_tag (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
  post_id BIGINT NOT NULL COMMENT '帖子ID',
  tag_id BIGINT NOT NULL COMMENT '标签ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_post_tag (post_id, tag_id),
  INDEX idx_post_tag_post_id (post_id),
  INDEX idx_post_tag_tag_id (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子-标签关联';

-- Comments
CREATE TABLE IF NOT EXISTS comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  post_id BIGINT NOT NULL COMMENT '帖子ID',
  parent_id BIGINT DEFAULT NULL COMMENT '父评论ID',
  user_id BIGINT NOT NULL COMMENT '评论用户ID',
  content TEXT NOT NULL COMMENT '评论内容',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常，0删除',
  audit_status TINYINT NOT NULL DEFAULT 1 COMMENT '审核状态：0待审，1通过，2拒绝',
  audit_reason VARCHAR(255) DEFAULT NULL COMMENT '审核原因',
  audit_updated_at DATETIME DEFAULT NULL COMMENT '审核时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_comment_post_id (post_id),
  INDEX idx_comment_parent_id (parent_id),
  INDEX idx_comment_user_id (user_id),
  INDEX idx_comment_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论';

-- Sign records
CREATE TABLE IF NOT EXISTS sign_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '签到记录ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  sign_date DATE NOT NULL COMMENT '签到日期',
  sign_time DATETIME NOT NULL COMMENT '签到时间',
  is_retro TINYINT NOT NULL DEFAULT 0 COMMENT '是否补签：0否，1是',
  reward_status TINYINT NOT NULL DEFAULT 0 COMMENT '奖励发放状态：0未发放，1已发放',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_sign_user_date (user_id, sign_date),
  INDEX idx_sign_user_id (user_id),
  INDEX idx_sign_date (sign_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到记录';

-- Sensitive words
CREATE TABLE IF NOT EXISTS sensitive_word (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '敏感词ID',
  word VARCHAR(64) NOT NULL COMMENT '敏感词',
  level TINYINT NOT NULL DEFAULT 1 COMMENT '等级：1低，2中，3高',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：1是，0否',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_sensitive_word (word)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词库';

-- Sensitive whitelist
CREATE TABLE IF NOT EXISTS sensitive_whitelist (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '白名单ID',
  word VARCHAR(64) NOT NULL COMMENT '白名单词',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：1是，0否',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_sensitive_whitelist (word)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词白名单';

-- Content audit
CREATE TABLE IF NOT EXISTS content_audit (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '审核记录ID',
  biz_type VARCHAR(16) NOT NULL COMMENT '业务类型：post/comment',
  biz_id BIGINT NOT NULL COMMENT '业务ID',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态：0待审，1通过，2拒绝',
  hit_level TINYINT NOT NULL DEFAULT 0 COMMENT '命中等级：0无，1低，2中，3高',
  hit_words VARCHAR(512) DEFAULT NULL COMMENT '命中词列表(逗号分隔)',
  operator_id BIGINT DEFAULT NULL COMMENT '审核人ID',
  reason VARCHAR(255) DEFAULT NULL COMMENT '审核原因',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_audit_biz (biz_type, biz_id),
  INDEX idx_audit_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容审核记录';

-- Notifications
CREATE TABLE IF NOT EXISTS user_notification (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '站内信ID',
  user_id BIGINT NOT NULL COMMENT '接收用户ID',
  actor_id BIGINT NOT NULL COMMENT '触发用户ID',
  action VARCHAR(16) NOT NULL COMMENT '动作：comment/reply',
  target_type VARCHAR(16) NOT NULL COMMENT '目标类型：post/comment',
  target_id BIGINT NOT NULL COMMENT '目标ID',
  post_id BIGINT NOT NULL COMMENT '帖子ID',
  content_snippet VARCHAR(200) DEFAULT NULL COMMENT '内容片段',
  is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0未读，1已读',
  read_at DATETIME DEFAULT NULL COMMENT '已读时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_notification_user (user_id, is_read, created_at),
  INDEX idx_notification_post (post_id),
  INDEX idx_notification_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内信提醒';

-- Likes
CREATE TABLE IF NOT EXISTS user_like (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞记录ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  target_type VARCHAR(16) NOT NULL COMMENT '目标类型：post/comment',
  target_id BIGINT NOT NULL COMMENT '目标ID',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1点赞，0取消',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_user_target (user_id, target_type, target_id),
  INDEX idx_target_status (target_type, target_id, status),
  INDEX idx_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞记录';
