CREATE TABLE IF NOT EXISTS question
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id 主键,',
    title       VARCHAR(512)                       NULL COMMENT '标题',
    content     TEXT                               NULL COMMENT '内容（详情）',
    tags        VARCHAR(1024)                      NULL COMMENT '标签列表（json数组）',
    answer      TEXT                               NULL COMMENT '题目答案',
    judgeCase   TEXT                               NULL COMMENT '判题用例（json数组）',
    judgeConfig TEXT                               NULL COMMENT '判题配置（json对象）',
    submitNum   INT      DEFAULT 0                 NOT NULL COMMENT '提交数',
    thumbNum    INT      DEFAULT 0                 NOT NULL COMMENT '点赞数',
    favourNum   INT      DEFAULT 0                 NOT NULL COMMENT '收藏数',
    userId      BIGINT                             NOT NULL COMMENT '创建用户id',
    createTime  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete    TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除',
    INDEX idx_userId (userId)
) COMMENT '题目' COLLATE = utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS question_submit
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'id 主键,',
    language   VARCHAR(32)                        NULL COMMENT '语言',
    code       TEXT                               NULL COMMENT '代码',
    judgeInfo  TEXT                               NULL COMMENT '判题信息',
    status     VARCHAR(32)                        NULL COMMENT '状态, 0:待判题, 1:判题中, 3:成功, 4:失败',
    questionId BIGINT                             NOT NULL COMMENT '题目id',
    userId     BIGINT                             NOT NULL COMMENT '用户id',
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete   TINYINT  DEFAULT 0                 NOT NULL COMMENT '是否删除',
    INDEX idx_questionId (questionId),
    INDEX idx_userId (userId)
) COMMENT '题目提交' COLLATE = utf8mb4_unicode_ci;
