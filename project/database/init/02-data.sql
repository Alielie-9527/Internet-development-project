-- 饮食健康管理系统初始数据
USE nutrition_db;

-- 插入管理员数据 (密码: admin123，BCrypt加密)
INSERT INTO `admin` (`username`, `password`, `nickname`, `email`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@example.com', 1);

-- 插入角色数据
INSERT INTO `role` (`name`, `code`, `description`, `status`) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有所有权限', 1),
('内容管理员', 'CONTENT_ADMIN', '管理营养内容和文章', 1),
('营养师', 'NUTRITIONIST', '提供营养咨询', 1);

-- 插入食物库数据 (常见食物营养信息)
INSERT INTO `food` (`name`, `category`, `unit`, `serving_size`, `calories`, `protein`, `carbohydrate`, `fat`, `fiber`, `sodium`, `is_verified`, `source`) VALUES
-- 主食类
('米饭', 'staple', 'g', 100, 116, 2.6, 25.9, 0.3, 0.3, 1, 1, '中国食物成分表'),
('馒头', 'staple', 'g', 100, 221, 7.0, 47.0, 1.1, 1.2, 178, 1, '中国食物成分表'),
('全麦面包', 'staple', 'g', 100, 246, 8.5, 44.3, 3.0, 6.7, 450, 1, '中国食物成分表'),
('燕麦片', 'staple', 'g', 100, 367, 15.0, 61.0, 7.2, 10.1, 5, 1, '中国食物成分表'),
('红薯', 'staple', 'g', 100, 99, 1.1, 24.7, 0.2, 1.6, 28, 1, '中国食物成分表'),
-- 蔬菜类
('西兰花', 'vegetable', 'g', 100, 34, 4.1, 6.6, 0.4, 2.6, 33, 1, '中国食物成分表'),
('番茄', 'vegetable', 'g', 100, 18, 0.9, 3.9, 0.2, 1.2, 5, 1, '中国食物成分表'),
('菠菜', 'vegetable', 'g', 100, 23, 2.9, 3.6, 0.3, 2.2, 79, 1, '中国食物成分表'),
('黄瓜', 'vegetable', 'g', 100, 16, 0.8, 3.6, 0.2, 0.5, 2, 1, '中国食物成分表'),
('胡萝卜', 'vegetable', 'g', 100, 41, 1.0, 9.6, 0.2, 2.8, 69, 1, '中国食物成分表'),
-- 水果类
('苹果', 'fruit', 'g', 100, 52, 0.3, 13.8, 0.2, 2.4, 1, 1, '中国食物成分表'),
('香蕉', 'fruit', 'g', 100, 89, 1.1, 22.8, 0.3, 2.6, 1, 1, '中国食物成分表'),
('橙子', 'fruit', 'g', 100, 47, 0.9, 11.8, 0.1, 2.4, 0, 1, '中国食物成分表'),
('草莓', 'fruit', 'g', 100, 32, 0.7, 7.7, 0.3, 2.0, 1, 1, '中国食物成分表'),
('西瓜', 'fruit', 'g', 100, 30, 0.6, 7.6, 0.2, 0.4, 2, 1, '中国食物成分表'),
-- 蛋白质类
('鸡胸肉', 'protein', 'g', 100, 133, 24.6, 2.5, 2.5, 0, 64, 1, '中国食物成分表'),
('鸡蛋', 'protein', '个', 50, 74, 6.3, 0.6, 5.2, 0, 71, 1, '中国食物成分表'),
('瘦牛肉', 'protein', 'g', 100, 125, 20.0, 0, 4.2, 0, 65, 1, '中国食物成分表'),
('三文鱼', 'protein', 'g', 100, 139, 20.0, 0, 6.3, 0, 44, 1, '中国食物成分表'),
('豆腐', 'protein', 'g', 100, 81, 8.1, 4.2, 3.7, 0.4, 7, 1, '中国食物成分表'),
-- 乳制品类
('牛奶', 'dairy', 'ml', 100, 54, 3.0, 3.4, 3.2, 0, 37, 1, '中国食物成分表'),
('酸奶', 'dairy', 'ml', 100, 72, 2.5, 9.3, 2.7, 0, 47, 1, '中国食物成分表'),
('脱脂牛奶', 'dairy', 'ml', 100, 35, 3.4, 4.8, 0.1, 0, 42, 1, '中国食物成分表'),
-- 零食类
('薯片', 'snack', 'g', 100, 548, 6.6, 49.2, 37.6, 4.2, 635, 1, '食品标签'),
('黑巧克力', 'snack', 'g', 100, 546, 4.9, 61.2, 31.3, 7.0, 20, 1, '食品标签');

-- 插入测试用户数据 (密码: test123)
INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `gender`, `birthday`, `height`, `weight`, `activity_level`, `health_goal`, `status`) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张小明', 'test@example.com', 1, '1995-06-15', 175.00, 70.00, 'moderate', 'lose_weight', 1),
('alice', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李娜', 'alice@example.com', 2, '1998-03-20', 165.00, 58.00, 'light', 'maintain', 1);

-- 插入每日营养目标
INSERT INTO `daily_nutrition_goal` (`user_id`, `target_calories`, `target_protein`, `target_carbohydrate`, `target_fat`, `target_fiber`, `target_water`, `is_active`) VALUES
(1, 1800, 90, 225, 50, 25, 2000, 1),
(2, 1600, 70, 200, 45, 25, 2000, 1);

-- 插入饮食日记示例 (最近3天的数据)
INSERT INTO `diet_diary` (`user_id`, `date`, `meal_type`, `meal_time`, `food_id`, `food_name`, `amount`, `unit`, `calories`, `protein`, `carbohydrate`, `fat`) VALUES
-- 用户1 今天的饮食
(1, CURDATE(), 'breakfast', '08:00:00', 2, '馒头', 100, 'g', 221, 7.0, 47.0, 1.1),
(1, CURDATE(), 'breakfast', '08:00:00', 17, '鸡蛋', 100, 'g', 148, 12.6, 1.2, 10.4),
(1, CURDATE(), 'breakfast', '08:00:00', 21, '牛奶', 250, 'ml', 135, 7.5, 8.5, 8.0),
(1, CURDATE(), 'lunch', '12:30:00', 1, '米饭', 150, 'g', 174, 3.9, 38.9, 0.5),
(1, CURDATE(), 'lunch', '12:30:00', 16, '鸡胸肉', 150, 'g', 199.5, 36.9, 3.8, 3.8),
(1, CURDATE(), 'lunch', '12:30:00', 6, '西兰花', 100, 'g', 34, 4.1, 6.6, 0.4),
(1, CURDATE(), 'snack', '15:00:00', 11, '苹果', 150, 'g', 78, 0.5, 20.7, 0.3),
(1, CURDATE(), 'dinner', '18:30:00', 1, '米饭', 100, 'g', 116, 2.6, 25.9, 0.3),
(1, CURDATE(), 'dinner', '18:30:00', 19, '三文鱼', 120, 'g', 166.8, 24.0, 0, 7.6),
(1, CURDATE(), 'dinner', '18:30:00', 8, '菠菜', 100, 'g', 23, 2.9, 3.6, 0.3),
-- 用户1 昨天的饮食
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'breakfast', '07:30:00', 4, '燕麦片', 50, 'g', 183.5, 7.5, 30.5, 3.6),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'breakfast', '07:30:00', 12, '香蕉', 100, 'g', 89, 1.1, 22.8, 0.3),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'lunch', '12:00:00', 1, '米饭', 150, 'g', 174, 3.9, 38.9, 0.5),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'lunch', '12:00:00', 18, '瘦牛肉', 100, 'g', 125, 20.0, 0, 4.2),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'dinner', '19:00:00', 20, '豆腐', 150, 'g', 121.5, 12.2, 6.3, 5.6),
-- 用户2 今天的饮食
(2, CURDATE(), 'breakfast', '08:30:00', 3, '全麦面包', 60, 'g', 147.6, 5.1, 26.6, 1.8),
(2, CURDATE(), 'breakfast', '08:30:00', 17, '鸡蛋', 50, 'g', 74, 6.3, 0.6, 5.2),
(2, CURDATE(), 'breakfast', '08:30:00', 22, '酸奶', 150, 'ml', 108, 3.8, 14.0, 4.1),
(2, CURDATE(), 'lunch', '12:00:00', 1, '米饭', 100, 'g', 116, 2.6, 25.9, 0.3),
(2, CURDATE(), 'lunch', '12:00:00', 19, '三文鱼', 100, 'g', 139, 20.0, 0, 6.3),
(2, CURDATE(), 'lunch', '12:00:00', 7, '番茄', 100, 'g', 18, 0.9, 3.9, 0.2),
(2, CURDATE(), 'snack', '16:00:00', 14, '草莓', 100, 'g', 32, 0.7, 7.7, 0.3);

-- 插入体重记录
INSERT INTO `weight_record` (`user_id`, `weight`, `bmi`, `body_fat_rate`, `record_date`) VALUES
(1, 72.50, 23.67, 22.5, DATE_SUB(CURDATE(), INTERVAL 7 DAY)),
(1, 71.80, 23.44, 22.0, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(1, 71.20, 23.25, 21.8, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(1, 70.50, 23.02, 21.5, DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(1, 70.00, 22.86, 21.2, CURDATE()),
(2, 58.50, 21.49, 24.5, DATE_SUB(CURDATE(), INTERVAL 7 DAY)),
(2, 58.20, 21.38, 24.3, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(2, 58.00, 21.30, 24.0, CURDATE());

-- 插入运动记录
INSERT INTO `exercise_record` (`user_id`, `exercise_type`, `duration`, `calories_burned`, `distance`, `intensity`, `record_date`) VALUES
(1, 'running', 30, 300, 5.0, 'moderate', CURDATE()),
(1, 'strength', 45, 180, NULL, 'moderate', DATE_SUB(CURDATE(), INTERVAL 1 DAY)),
(1, 'walking', 40, 150, 3.5, 'low', DATE_SUB(CURDATE(), INTERVAL 2 DAY)),
(2, 'yoga', 60, 150, NULL, 'low', CURDATE()),
(2, 'cycling', 45, 280, 12.0, 'moderate', DATE_SUB(CURDATE(), INTERVAL 1 DAY));

-- 插入饮水记录
INSERT INTO `water_record` (`user_id`, `amount`, `record_date`, `record_time`) VALUES
(1, 250, CURDATE(), '08:00:00'),
(1, 300, CURDATE(), '10:30:00'),
(1, 250, CURDATE(), '14:00:00'),
(1, 300, CURDATE(), '16:30:00'),
(1, 250, CURDATE(), '19:00:00'),
(2, 200, CURDATE(), '09:00:00'),
(2, 250, CURDATE(), '11:00:00'),
(2, 200, CURDATE(), '15:00:00'),
(2, 250, CURDATE(), '18:00:00');

-- 插入知识库数据 (营养知识)
INSERT INTO `knowledge` (`title`, `content`, `category`, `keywords`, `source`, `status`) VALUES
('蛋白质的重要性', '蛋白质是构成人体组织的主要成分，对于肌肉生长、免疫系统、激素合成等至关重要。成年人每日推荐摄入量为每公斤体重0.8-1.2克。优质蛋白质来源包括：瘦肉、鱼类、蛋类、豆制品、乳制品等。', 'nutrition', '蛋白质,营养素,肌肉', '营养学专业资料', 1),
('碳水化合物的选择', '碳水化合物是人体主要能量来源。选择复杂碳水化合物（如全谷物、燕麦、红薯）比精制碳水（如白米、白面）更有利于血糖稳定和长时间饱腹感。建议碳水化合物占总热量的45-65%。', 'nutrition', '碳水化合物,主食,血糖', '营养学专业资料', 1),
('健康脂肪vs不健康脂肪', '脂肪分为饱和脂肪、不饱和脂肪和反式脂肪。应多摄入不饱和脂肪（如橄榄油、坚果、深海鱼），限制饱和脂肪（动物脂肪），避免反式脂肪（加工食品）。脂肪应占总热量的20-35%。', 'nutrition', '脂肪,健康油脂,Omega-3', '营养学专业资料', 1),
('如何计算BMI', 'BMI（身体质量指数）= 体重(kg) / 身高²(m²)。标准范围：18.5-23.9为正常，<18.5为偏瘦，24-27.9为超重，≥28为肥胖。但BMI不能反映体脂率和肌肉量，需结合其他指标综合评估。', 'nutrition', 'BMI,体重管理,健康指标', '健康管理指南', 1),
('膳食纤维的益处', '膳食纤维有助于：1.促进肠道蠕动，预防便秘；2.增加饱腹感，控制体重；3.稳定血糖；4.降低胆固醇。成年人每日推荐摄入25-30克。富含纤维的食物：全谷物、蔬菜、水果、豆类。', 'nutrition', '膳食纤维,肠道健康,饱腹感', '营养学专业资料', 1),
('减肥的科学方法', '健康减重的关键：1.热量赤字（消耗>摄入）；2.均衡饮食，不极端节食；3.规律运动（有氧+力量）；4.充足睡眠；5.循序渐进，每周减重0.5-1kg为宜。避免快速减肥导致的肌肉流失和反弹。', 'nutrition', '减肥,减脂,体重管理', '健康管理指南', 1);

-- 插入食谱数据
INSERT INTO `recipe` (`name`, `description`, `category`, `difficulty`, `prep_time`, `cook_time`, `servings`, `ingredients`, `steps`, `nutrition_info`, `tags`, `author_id`, `status`) VALUES
('鸡胸肉蔬菜沙拉', '高蛋白低脂的健康午餐选择，富含优质蛋白质和多种维生素', 'lunch', 'easy', 10, 15, 2, 
'[{"name":"鸡胸肉","amount":200,"unit":"g"},{"name":"生菜","amount":100,"unit":"g"},{"name":"圣女果","amount":100,"unit":"g"},{"name":"黄瓜","amount":50,"unit":"g"},{"name":"橄榄油","amount":10,"unit":"ml"},{"name":"柠檬汁","amount":5,"unit":"ml"}]',
'[{"order":1,"content":"鸡胸肉洗净，用少许盐和黑胡椒腌制10分钟"},{"order":2,"content":"平底锅中火煎鸡胸肉，每面5-7分钟至熟透"},{"order":3,"content":"生菜撕成小片，圣女果对半切，黄瓜切片"},{"order":4,"content":"鸡胸肉切片，与蔬菜混合"},{"order":5,"content":"淋上橄榄油和柠檬汁，拌匀即可"}]',
'{"calories":320,"protein":42,"carbohydrate":12,"fat":11,"fiber":3}',
'减脂,高蛋白,低卡路里', 1, 1),

('燕麦香蕉早餐碗', '营养丰富的快手早餐，提供持久能量和膳食纤维', 'breakfast', 'easy', 5, 5, 1,
'[{"name":"燕麦片","amount":50,"unit":"g"},{"name":"牛奶","amount":200,"unit":"ml"},{"name":"香蕉","amount":1,"unit":"根"},{"name":"蓝莓","amount":30,"unit":"g"},{"name":"坚果","amount":15,"unit":"g"},{"name":"蜂蜜","amount":5,"unit":"g"}]',
'[{"order":1,"content":"燕麦片加牛奶，微波炉加热2-3分钟"},{"order":2,"content":"香蕉切片"},{"order":3,"content":"将香蕉片、蓝莓、坚果放在燕麦上"},{"order":4,"content":"淋上蜂蜜即可享用"}]',
'{"calories":380,"protein":14,"carbohydrate":58,"fat":12,"fiber":8}',
'早餐,快手,高纤维', 1, 1),

('三文鱼时蔬套餐', '富含Omega-3脂肪酸的营养晚餐，适合健身人群', 'dinner', 'medium', 15, 20, 2,
'[{"name":"三文鱼","amount":250,"unit":"g"},{"name":"西兰花","amount":150,"unit":"g"},{"name":"胡萝卜","amount":100,"unit":"g"},{"name":"糙米饭","amount":150,"unit":"g"},{"name":"橄榄油","amount":10,"unit":"ml"},{"name":"柠檬","amount":"0.5","unit":"个"}]',
'[{"order":1,"content":"三文鱼用盐、黑胡椒、柠檬汁腌制15分钟"},{"order":2,"content":"西兰花和胡萝卜切块，蒸10分钟"},{"order":3,"content":"平底锅热油，煎三文鱼每面3-4分钟"},{"order":4,"content":"糙米饭蒸熟"},{"order":5,"content":"将所有食材摆盘即可"}]',
'{"calories":550,"protein":38,"carbohydrate":48,"fat":20,"fiber":6}',
'增肌,健康脂肪,均衡营养', 1, 1);

-- 插入文章数据
INSERT INTO `article` (`title`, `summary`, `content`, `cover_image`, `category`, `tags`, `author_id`, `status`, `published_at`) VALUES
('如何制定个人减脂计划', '科学减脂不是节食，而是合理饮食+规律运动。本文教你如何制定适合自己的减脂方案。', 
'# 如何制定个人减脂计划\n\n## 一、计算每日热量需求\n1. 计算基础代谢率(BMR)\n2. 根据活动水平调整\n3. 设定热量赤字(通常减少300-500大卡)\n\n## 二、营养配比\n- 蛋白质：1.6-2.2g/kg体重\n- 碳水化合物：占总热量40-50%\n- 脂肪：占总热量20-30%\n\n## 三、运动安排\n- 有氧运动：每周3-5次，每次30-45分钟\n- 力量训练：每周2-3次，防止肌肉流失\n\n## 四、监测与调整\n- 每周称重1-2次\n- 记录饮食日记\n- 根据进展调整计划\n\n记住：健康减脂是长期过程，避免极端方法！', 
NULL, 'fitness', '减脂,健身,饮食计划', 1, 1, NOW()),

('10种超级食物及其营养价值', '这些营养密度极高的食物，能为身体提供丰富的维生素、矿物质和抗氧化剂。', 
'# 10种超级食物及其营养价值\n\n## 1. 蓝莓\n富含抗氧化剂，保护大脑和心血管健康\n\n## 2. 三文鱼\nOmega-3脂肪酸含量高，抗炎、护心\n\n## 3. 菠菜\n铁、维生素K、叶酸丰富，促进血液健康\n\n## 4. 燕麦\n高纤维、低GI，稳定血糖，增加饱腹感\n\n## 5. 鸡蛋\n完整蛋白质来源，含胆碱和多种维生素\n\n## 6. 西兰花\n十字花科蔬菜，富含维生素C和抗癌物质\n\n## 7. 坚果\n健康脂肪、蛋白质和微量元素\n\n## 8. 希腊酸奶\n高蛋白、益生菌，促进肠道健康\n\n## 9. 红薯\n复杂碳水、β-胡萝卜素、膳食纤维\n\n## 10. 绿茶\n儿茶素抗氧化，促进代谢\n\n建议：多样化饮食，这些食物搭配食用效果更佳！', 
NULL, 'nutrition', '营养,健康饮食,超级食物', 1, 1, NOW()),

('健身人群的蛋白质摄入指南', '想增肌？蛋白质摄入是关键。了解如何科学补充蛋白质。', 
'# 健身人群的蛋白质摄入指南\n\n## 蛋白质需求量\n- 普通人：0.8g/kg体重\n- 健身人群：1.6-2.2g/kg体重\n- 增肌期：2.0-2.2g/kg体重\n- 减脂期：1.8-2.0g/kg体重（保护肌肉）\n\n## 优质蛋白质来源\n### 动物性蛋白\n- 鸡胸肉、瘦牛肉、鱼类\n- 鸡蛋、牛奶、希腊酸奶\n\n### 植物性蛋白\n- 豆腐、豆浆、毛豆\n- 藜麦、扁豆\n\n## 蛋白质摄入时机\n- 运动后30分钟内：20-40g\n- 每餐均衡分配：20-30g\n- 睡前：缓释蛋白（如酪蛋白）\n\n## 蛋白粉的选择\n- 乳清蛋白：吸收快，适合运动后\n- 酪蛋白：吸收慢，适合睡前\n- 植物蛋白：素食者选择\n\n注意：过量蛋白质会增加肾脏负担，需适量！', 
NULL, 'fitness', '蛋白质,增肌,健身营养', 1, 1, NOW());
