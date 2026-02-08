-- ============================================
-- 数据库更新脚本 - 2025-02-07
-- 更新内容：
-- 1. 为重点设备使用上报表添加"每周工作天数"字段
-- 2. 更新"诊治例数"字段注释为"日均服务例数"
-- ============================================

-- 1. 添加"每周工作天数"字段
ALTER TABLE cst_key_equip_usage 
ADD COLUMN week_work_days INT(11) DEFAULT NULL COMMENT '每周工作天数' 
AFTER work_hours;

-- 2. 更新"诊治例数"字段注释为"日均服务例数"
ALTER TABLE cst_key_equip_usage 
MODIFY COLUMN treat_count INT(11) DEFAULT 0 COMMENT '日均服务例数';

-- 在用户表中添加所属科室字段
ALTER TABLE sys_user ADD COLUMN use_dept VARCHAR(100) DEFAULT '' COMMENT '所属科室(词典cst_use_dept)' AFTER dept_id;

