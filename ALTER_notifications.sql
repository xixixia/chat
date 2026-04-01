-- Notification table migration
CREATE TABLE IF NOT EXISTS user_notification (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'notification id',
  user_id BIGINT NOT NULL COMMENT 'recipient user id',
  actor_id BIGINT NOT NULL COMMENT 'actor user id',
  action VARCHAR(16) NOT NULL COMMENT 'comment/reply',
  target_type VARCHAR(16) NOT NULL COMMENT 'post/comment',
  target_id BIGINT NOT NULL COMMENT 'target id',
  post_id BIGINT NOT NULL COMMENT 'post id',
  content_snippet VARCHAR(200) DEFAULT NULL COMMENT 'content snippet',
  is_read TINYINT NOT NULL DEFAULT 0 COMMENT 'read flag',
  read_at DATETIME DEFAULT NULL COMMENT 'read time',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'created at',
  INDEX idx_notification_user (user_id, is_read, created_at),
  INDEX idx_notification_post (post_id),
  INDEX idx_notification_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='user notifications';
