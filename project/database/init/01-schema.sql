-- 饮食健康管理系统数据库初始化脚本
-- 数据库: nutrition_db

USE nutrition_db;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) NULL COMMENT '昵称',
  `email` VARCHAR(100) NULL COMMENT '邮箱',
  `phone` VARCHAR(20) NULL COMMENT '手机号',
  `avatar` VARCHAR(255) NULL COMMENT '头像URL',
  `gender` TINYINT(1) NULL COMMENT '性别 0-未知 1-男 2-女',
  `birthday` DATE NULL COMMENT '生日',
  `height` DECIMAL(5,2) NULL COMMENT '身高(cm)',
  `weight` DECIMAL(5,2) NULL COMMENT '体重(kg)',
  `activity_level` VARCHAR(20) NULL COMMENT '活动水平 sedentary-久坐 light-轻度 moderate-中度 active-活跃 very_active-非常活跃',
  `health_goal` VARCHAR(50) NULL COMMENT '健康目标 lose_weight-减重 maintain-维持 gain_weight-增重 build_muscle-增肌',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `last_login_time` DATETIME NULL COMMENT '最后登录时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 食物库表
CREATE TABLE IF NOT EXISTS `food` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '食物ID',
  `name` VARCHAR(100) NOT NULL COMMENT '食物名称',
  `category` VARCHAR(50) NOT NULL COMMENT '分类 staple-主食 vegetable-蔬菜 fruit-水果 protein-蛋白质 dairy-乳制品 snack-零食 drink-饮品',
  `brand` VARCHAR(100) NULL COMMENT '品牌',
  `barcode` VARCHAR(50) NULL COMMENT '条形码',
  `unit` VARCHAR(20) NOT NULL DEFAULT 'g' COMMENT '单位 g-克 ml-毫升 个 份',
  `serving_size` DECIMAL(10,2) NOT NULL DEFAULT 100 COMMENT '每份大小',
  `calories` DECIMAL(10,2) NOT NULL COMMENT '热量(kcal)',
  `protein` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '蛋白质(g)',
  `carbohydrate` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '碳水化合物(g)',
  `fat` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '脂肪(g)',
  `fiber` DECIMAL(10,2) NULL COMMENT '膳食纤维(g)',
  `sodium` DECIMAL(10,2) NULL COMMENT '钠(mg)',
  `sugar` DECIMAL(10,2) NULL COMMENT '糖(g)',
  `vitamin_a` DECIMAL(10,2) NULL COMMENT '维生素A(μg)',
  `vitamin_c` DECIMAL(10,2) NULL COMMENT '维生素C(mg)',
  `calcium` DECIMAL(10,2) NULL COMMENT '钙(mg)',
  `iron` DECIMAL(10,2) NULL COMMENT '铁(mg)',
  `image_url` VARCHAR(255) NULL COMMENT '图片URL',
  `description` TEXT NULL COMMENT '描述',
  `is_verified` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否官方认证',
  `source` VARCHAR(50) NULL COMMENT '数据来源',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_category` (`category`),
  KEY `idx_barcode` (`barcode`),
  FULLTEXT KEY `ft_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食物库表';

-- 3. 饮食日记表
CREATE TABLE IF NOT EXISTS `diet_diary` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日记ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `date` DATE NOT NULL COMMENT '日期',
  `meal_type` VARCHAR(20) NOT NULL COMMENT '餐次 breakfast-早餐 lunch-午餐 dinner-晚餐 snack-加餐',
  `meal_time` TIME NULL COMMENT '用餐时间',
  `food_id` BIGINT NOT NULL COMMENT '食物ID',
  `food_name` VARCHAR(100) NOT NULL COMMENT '食物名称(冗余)',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '食用量',
  `unit` VARCHAR(20) NOT NULL COMMENT '单位',
  `calories` DECIMAL(10,2) NOT NULL COMMENT '热量(kcal)',
  `protein` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '蛋白质(g)',
  `carbohydrate` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '碳水化合物(g)',
  `fat` DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '脂肪(g)',
  `notes` TEXT NULL COMMENT '备注',
  `image_url` VARCHAR(255) NULL COMMENT '照片URL',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `date`),
  KEY `idx_food_id` (`food_id`),
  KEY `idx_meal_type` (`meal_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='饮食日记表';

-- 4. 每日营养目标表
CREATE TABLE IF NOT EXISTS `daily_nutrition_goal` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '目标ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `target_calories` DECIMAL(10,2) NOT NULL COMMENT '目标热量(kcal)',
  `target_protein` DECIMAL(10,2) NULL COMMENT '目标蛋白质(g)',
  `target_carbohydrate` DECIMAL(10,2) NULL COMMENT '目标碳水化合物(g)',
  `target_fat` DECIMAL(10,2) NULL COMMENT '目标脂肪(g)',
  `target_fiber` DECIMAL(10,2) NULL COMMENT '目标膳食纤维(g)',
  `target_water` DECIMAL(10,2) NULL COMMENT '目标饮水量(ml)',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日营养目标表';

-- 5. 体重记录表
CREATE TABLE IF NOT EXISTS `weight_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `weight` DECIMAL(5,2) NOT NULL COMMENT '体重(kg)',
  `bmi` DECIMAL(5,2) NULL COMMENT 'BMI',
  `body_fat_rate` DECIMAL(5,2) NULL COMMENT '体脂率(%)',
  `muscle_mass` DECIMAL(5,2) NULL COMMENT '肌肉量(kg)',
  `bone_mass` DECIMAL(5,2) NULL COMMENT '骨量(kg)',
  `water_rate` DECIMAL(5,2) NULL COMMENT '水分率(%)',
  `visceral_fat` DECIMAL(5,2) NULL COMMENT '内脏脂肪等级',
  `basal_metabolism` DECIMAL(10,2) NULL COMMENT '基础代谢(kcal)',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `notes` VARCHAR(500) NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='体重记录表';

-- 6. 运动记录表
CREATE TABLE IF NOT EXISTS `exercise_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `exercise_type` VARCHAR(50) NOT NULL COMMENT '运动类型 running-跑步 walking-走路 cycling-骑行 swimming-游泳 strength-力量训练 yoga-瑜伽',
  `duration` INT NOT NULL COMMENT '时长(分钟)',
  `calories_burned` DECIMAL(10,2) NULL COMMENT '消耗热量(kcal)',
  `distance` DECIMAL(10,2) NULL COMMENT '距离(km)',
  `intensity` VARCHAR(20) NULL COMMENT '强度 low-低 moderate-中 high-高',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `note` TEXT NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `record_date`),
  KEY `idx_exercise_type` (`exercise_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运动记录表';

-- 7. 饮水记录表
CREATE TABLE IF NOT EXISTS `water_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '饮水量(ml)',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `record_time` TIME NULL COMMENT '记录时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='饮水记录表';

-- 8. 营养分析报告表
CREATE TABLE IF NOT EXISTS `nutrition_report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `report_date` DATE NOT NULL COMMENT '报告日期',
  `report_type` VARCHAR(20) NOT NULL DEFAULT 'daily' COMMENT '报告类型 daily-每日 weekly-每周 monthly-每月',
  `total_calories` DECIMAL(10,2) NOT NULL COMMENT '总热量',
  `total_protein` DECIMAL(10,2) NOT NULL COMMENT '总蛋白质',
  `total_carbohydrate` DECIMAL(10,2) NOT NULL COMMENT '总碳水化合物',
  `total_fat` DECIMAL(10,2) NOT NULL COMMENT '总脂肪',
  `calories_burned` DECIMAL(10,2) NULL COMMENT '消耗热量',
  `net_calories` DECIMAL(10,2) NULL COMMENT '净热量',
  `goal_completion_rate` DECIMAL(5,2) NULL COMMENT '目标完成率(%)',
  `nutrition_balance_score` INT NULL COMMENT '营养均衡评分(0-100)',
  `ai_advice` TEXT NULL COMMENT 'AI建议',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `report_date`),
  KEY `idx_report_type` (`report_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='营养分析报告表';

-- 9. 文章表
CREATE TABLE IF NOT EXISTS `article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `summary` VARCHAR(500) NULL COMMENT '摘要',
  `content` TEXT NOT NULL COMMENT '内容',
  `cover_image` VARCHAR(255) NULL COMMENT '封面图',
  `category` VARCHAR(50) NOT NULL COMMENT '分类 nutrition-营养知识 recipe-健康食谱 fitness-健身 health-健康生活',
  `tags` VARCHAR(200) NULL COMMENT '标签',
  `author_id` BIGINT NOT NULL COMMENT '作者ID',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '阅读量',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态 0-草稿 1-已发布',
  `published_at` DATETIME NULL COMMENT '发布时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 10. 知识库表 (营养知识)
CREATE TABLE IF NOT EXISTS `knowledge` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '知识ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `category` VARCHAR(50) NOT NULL COMMENT '分类 nutrition-营养学 disease-疾病 ingredient-食材',
  `keywords` VARCHAR(200) NULL COMMENT '关键词',
  `embedding` JSON NULL COMMENT '向量嵌入(用于AI检索)',
  `source` VARCHAR(100) NULL COMMENT '来源',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  FULLTEXT KEY `ft_content` (`content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库表';

-- 11. 食谱表
CREATE TABLE IF NOT EXISTS `recipe` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '食谱ID',
  `name` VARCHAR(100) NOT NULL COMMENT '食谱名称',
  `description` TEXT NULL COMMENT '描述',
  `category` VARCHAR(50) NOT NULL COMMENT '分类 breakfast-早餐 lunch-午餐 dinner-晚餐 dessert-甜点',
  `difficulty` VARCHAR(20) NULL COMMENT '难度 easy-简单 medium-中等 hard-困难',
  `prep_time` INT NULL COMMENT '准备时间(分钟)',
  `cook_time` INT NULL COMMENT '烹饪时间(分钟)',
  `servings` INT NOT NULL DEFAULT 1 COMMENT '份数',
  `ingredients` JSON NOT NULL COMMENT '食材列表',
  `steps` JSON NOT NULL COMMENT '步骤',
  `nutrition_info` JSON NULL COMMENT '营养信息',
  `tags` VARCHAR(200) NULL COMMENT '标签',
  `image_url` VARCHAR(255) NULL COMMENT '图片',
  `author_id` BIGINT NOT NULL COMMENT '作者ID',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '查看次数',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 0-草稿 1-已发布',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_author_id` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食谱表';

-- 12. 管理员表
CREATE TABLE IF NOT EXISTS `admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) NULL COMMENT '昵称',
  `email` VARCHAR(100) NULL COMMENT '邮箱',
  `phone` VARCHAR(20) NULL COMMENT '手机号',
  `avatar` VARCHAR(255) NULL COMMENT '头像',
  `role_id` BIGINT NULL COMMENT '角色ID',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
  `last_login_time` DATETIME NULL COMMENT '最后登录时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 13. 聊天会话表 (营养咨询)
CREATE TABLE IF NOT EXISTS `chat_session` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(100) NULL COMMENT '会话标题',
  `type` VARCHAR(20) NOT NULL DEFAULT 'nutrition' COMMENT '会话类型 nutrition-营养咨询 recipe-食谱推荐',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 0-结束 1-进行中',
  `last_message_time` DATETIME NULL COMMENT '最后消息时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天会话表';

-- 14. 聊天消息表
CREATE TABLE IF NOT EXISTS `chat_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` BIGINT NOT NULL COMMENT '会话ID',
  `role` VARCHAR(20) NOT NULL COMMENT '角色 user-用户 assistant-AI',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `tokens` INT NULL COMMENT 'Token数量',
  `model` VARCHAR(50) NULL COMMENT '使用的模型',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- 15. AI配置表
CREATE TABLE IF NOT EXISTS `ai_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `name` VARCHAR(100) NOT NULL COMMENT '配置名称',
  `provider` VARCHAR(50) NOT NULL COMMENT '提供商 qwen, custom',
  `model` VARCHAR(100) NOT NULL COMMENT '模型名称',
  `api_url` VARCHAR(255) NOT NULL COMMENT 'API地址',
  `api_key` VARCHAR(255) NOT NULL COMMENT 'API密钥',
  `parameters` JSON NULL COMMENT '模型参数',
  `is_default` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI配置表';

-- 16. 角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(200) NULL COMMENT '描述',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';
