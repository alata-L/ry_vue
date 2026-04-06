-- ============================================
-- 数据库更新脚本 - 2026-03-29
-- 说明：
-- 1. 新增统一表 cst_report_usage_hist：通用设备上报、重点设备上报共用，通过 biz_type 区分
-- 2. 在「设备管理」下增加两个菜单：通用设备修改记录、重点设备修改记录
-- 执行前请确认父菜单 menu_id=2000（设备管理）已存在（见 custom.sql）
-- ============================================

-- ----------------------------
-- 1、上报数据变更历史表（通用 + 重点 共用）
-- 已存在同名表时不会重建；若需重建请先自行备份后 DROP TABLE cst_report_usage_hist;
-- ----------------------------
CREATE TABLE IF NOT EXISTS cst_report_usage_hist (
  id               BIGINT(20)    NOT NULL AUTO_INCREMENT    COMMENT '主键',
  biz_type         VARCHAR(10)   NOT NULL                   COMMENT '业务类型：LIFE=通用设备上报(cst_life_usage) KEY=重点设备上报(cst_key_equip_usage)',
  usage_id         BIGINT(20)    NOT NULL                   COMMENT '业务主表主键：对应 cst_life_usage.id 或 cst_key_equip_usage.id',
  oper_type        VARCHAR(16)   NOT NULL DEFAULT 'UPDATE' COMMENT '操作类型：INSERT 首次上报 UPDATE 修改 DELETE 删除',
  change_time      DATETIME      NOT NULL                   COMMENT '变更时间',
  change_by        VARCHAR(64)   NOT NULL DEFAULT ''        COMMENT '操作人登录名',
  change_by_nick   VARCHAR(64)   DEFAULT ''                 COMMENT '操作人昵称快照',
  before_json      LONGTEXT                                   COMMENT '变更前数据JSON快照',
  after_json       LONGTEXT                                   COMMENT '变更后数据JSON快照',
  remark           VARCHAR(500)  DEFAULT NULL               COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_report_hist_biz_usage (biz_type, usage_id),
  KEY idx_report_hist_time (change_time),
  KEY idx_report_hist_change_by (change_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备上报数据变更历史（通用/重点共用）';

-- ----------------------------
-- 2、菜单：设备管理下增加「修改记录」页面
-- parent_id=2000 下展示顺序：通用(台账→上报→统计→通用修改记录)→重点(台账→上报→统计→重点修改记录)
-- order_num: 2001=1 2010=2 2020=3 2050=4 | 2030=5 2040=6 2045=7 2060=8
-- 重复执行本段前可先删除旧菜单及按钮（子菜单先删）：
--   DELETE FROM sys_role_menu WHERE menu_id IN (2051,2061,2050,2060);
--   DELETE FROM sys_menu WHERE menu_id IN (2051,2061,2050,2060);
-- ----------------------------
INSERT INTO sys_menu VALUES(2050, '通用设备修改记录', 2000, 4, 'lifeUsageHistory', 'custom/lifeUsage/history', '', '', 1, 0, 'C', '0', '0', 'custom:lifeUsage:history:list', 'documentation', 'admin', sysdate(), '', NULL, '通用设备日使用上报的修改痕迹查询');
INSERT INTO sys_menu VALUES(2051, '修改记录查询', 2050, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeUsage:history:query', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu VALUES(2060, '重点设备修改记录', 2000, 8, 'keyUsageHistory', 'custom/keyUsage/history', '', '', 1, 0, 'C', '0', '0', 'custom:keyUsage:history:list', 'documentation', 'admin', sysdate(), '', NULL, '重点设备月度上报的修改痕迹查询');
INSERT INTO sys_menu VALUES(2061, '修改记录查询', 2060, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyUsage:history:query', '#', 'admin', sysdate(), '', NULL, '');

-- 设备管理(2000)直属子菜单排序修正（已存在库：若此前 2050 插在末尾，执行后通用修改记录会排到通用统计后）
UPDATE sys_menu SET order_num = 1 WHERE menu_id = 2001 AND parent_id = 2000;
UPDATE sys_menu SET order_num = 2 WHERE menu_id = 2010 AND parent_id = 2000;
UPDATE sys_menu SET order_num = 3 WHERE menu_id = 2020 AND parent_id = 2000;
UPDATE sys_menu SET order_num = 4 WHERE menu_id = 2050 AND parent_id = 2000;
UPDATE sys_menu SET order_num = 5 WHERE menu_id = 2030 AND parent_id = 2000;
UPDATE sys_menu SET order_num = 6 WHERE menu_id = 2040 AND parent_id = 2000;
UPDATE sys_menu SET order_num = 7 WHERE menu_id = 2045 AND parent_id = 2000;
UPDATE sys_menu SET order_num = 8 WHERE menu_id = 2060 AND parent_id = 2000;

-- ----------------------------
-- 3、为超级管理员角色(role_id=1)授权新菜单（可选；若已全量勾选设备管理可跳过）
-- ----------------------------
INSERT IGNORE INTO sys_role_menu VALUES (1, 2050);
INSERT IGNORE INTO sys_role_menu VALUES (1, 2051);
INSERT IGNORE INTO sys_role_menu VALUES (1, 2060);
INSERT IGNORE INTO sys_role_menu VALUES (1, 2061);


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


INSERT INTO sys_menu VALUES(2015, '使用数据导出', 2010, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeUsage:export', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_role_menu VALUES (1, 2015);
INSERT INTO sys_menu VALUES(2070, '使用统计导出', 2040, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyUsage:export', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_role_menu VALUES (1, 2070);
