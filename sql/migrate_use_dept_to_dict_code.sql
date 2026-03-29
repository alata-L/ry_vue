-- =============================================================================
-- 使用科室：由「科室名称/旧 dict_value 文本」统一改为「sys_dict_data.dict_code」
-- 方案 B：sys_user_use_dept 关联表 + sys_user.use_dept 存逗号分隔的 dict_code
-- 执行前请备份数据库；建议在测试库先跑一遍。
-- =============================================================================

-- ----------------------------
-- 1、用户-科室关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_user_use_dept (
  user_id        BIGINT(20) NOT NULL COMMENT '用户ID',
  dept_dict_code BIGINT(20) NOT NULL COMMENT 'sys_dict_data.dict_code（dict_type=cst_use_dept）',
  PRIMARY KEY (user_id, dept_dict_code),
  KEY idx_user_use_dept_code (dept_dict_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户所属使用科室（多选）';

-- ----------------------------
-- 2、业务表与 sys_user.use_dept
--    时机：必须在「步骤 3 修改词典 dict_value」之前执行，否则只能依赖 dict_label 匹配。
--    作用：将下列表中仍存的中文科室名或旧 dict_value 文本，改为 sys_dict_data.dict_code（字符串）。
--    涉及表：
--      cst_life_usage   — 通用设备日使用上报
--      cst_life_equip   — 通用设备台账
--      cst_key_equip    — 重点设备台账
--      sys_user         — 系统用户「所属科室」单字段（多科室迁移后可能为逗号分隔，本段仅处理非纯数字行）
--    无需单独 UPDATE 的表：
--      cst_key_equip_usage — 重点设备「使用上报」主表（按日/次填报），仅有 equip_id 等业务字段，无 use_dept 列；
--                            列表/统计中的科室均来自 JOIN cst_key_equip.use_dept，只要上面已迁移 cst_key_equip 即可。
--    无法匹配的行保持原样，需人工处理。
-- ----------------------------
-- cst_life_usage：通用设备（生命支持类）日使用上报数据，use_dept 表示该条上报所属使用科室
-- ----------------------------
UPDATE cst_life_usage t
INNER JOIN sys_dict_data d ON d.dict_type = 'cst_use_dept'
  AND (d.dict_label = TRIM(t.use_dept) OR d.dict_value = TRIM(t.use_dept))
SET t.use_dept = CAST(d.dict_code AS CHAR)
WHERE t.use_dept IS NOT NULL AND TRIM(t.use_dept) <> '';

-- ----------------------------
-- cst_life_equip：通用设备台账（设备主数据），use_dept 表示设备归属使用科室
-- ----------------------------
UPDATE cst_life_equip t
INNER JOIN sys_dict_data d ON d.dict_type = 'cst_use_dept'
  AND (d.dict_label = TRIM(t.use_dept) OR d.dict_value = TRIM(t.use_dept))
SET t.use_dept = CAST(d.dict_code AS CHAR)
WHERE t.use_dept IS NOT NULL AND TRIM(t.use_dept) <> '';

-- ----------------------------
-- cst_key_equip：重点设备台账（设备主数据），use_dept 表示设备归属使用科室
-- ----------------------------
UPDATE cst_key_equip t
INNER JOIN sys_dict_data d ON d.dict_type = 'cst_use_dept'
  AND (d.dict_label = TRIM(t.use_dept) OR d.dict_value = TRIM(t.use_dept))
SET t.use_dept = CAST(d.dict_code AS CHAR)
WHERE t.use_dept IS NOT NULL AND TRIM(t.use_dept) <> '';

-- （以下为说明，无 SQL）重点设备上报表 cst_key_equip_usage 不包含 use_dept，不执行 UPDATE。

-- ----------------------------
-- sys_user：用户表「所属科室」use_dept（单字段）；已存纯数字/逗号分隔数字的视为已迁移，跳过避免误更新
-- ----------------------------
UPDATE sys_user u
INNER JOIN sys_dict_data d ON d.dict_type = 'cst_use_dept'
  AND (d.dict_label = TRIM(u.use_dept) OR d.dict_value = TRIM(u.use_dept))
SET u.use_dept = CAST(d.dict_code AS CHAR)
WHERE u.del_flag = '0'
  AND u.use_dept IS NOT NULL
  AND TRIM(u.use_dept) <> ''
  AND u.use_dept NOT REGEXP '^[0-9]+$|^[0-9]+(,[0-9]+)*$';

-- ----------------------------
-- 3、词典：将 dict_value 改为与 dict_code 一致（前端 dict.value、Excel 等与编码一致）
-- ----------------------------
UPDATE sys_dict_data
SET dict_value = CAST(dict_code AS CHAR)
WHERE dict_type = 'cst_use_dept';

-- ----------------------------
-- 4、从 sys_user.use_dept（逗号分隔的 dict_code）回填 sys_user_use_dept
-- ----------------------------
INSERT IGNORE INTO sys_user_use_dept (user_id, dept_dict_code)
SELECT u.user_id,
       CAST(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(CONCAT(',', REPLACE(u.use_dept, ' ', ''), ','), ',', n.n + 1), ',', -1)) AS UNSIGNED)
FROM sys_user u
INNER JOIN (
  SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
  UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) n ON n.n <= (CHAR_LENGTH(u.use_dept) - CHAR_LENGTH(REPLACE(u.use_dept, ',', '')) + 1)
WHERE u.del_flag = '0'
  AND u.use_dept IS NOT NULL
  AND TRIM(u.use_dept) <> ''
  AND u.use_dept REGEXP '^[0-9]+$|^[0-9]+(,[0-9]+)*$'
  AND CAST(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(CONCAT(',', REPLACE(u.use_dept, ' ', ''), ','), ',', n.n + 1), ',', -1)) AS UNSIGNED) > 0;

-- =============================================================================
-- 说明：
-- 1) 若历史 user.use_dept 曾存「多个中文科室名+逗号」，无法用单条 UPDATE 自动映射，请先拆行或写脚本逐条匹配。
-- 2) cst_report_usage_hist 等 JSON 快照中的 useDept 仍为旧文本时，需业务侧展示兼容或离线脚本替换。
-- =============================================================================
