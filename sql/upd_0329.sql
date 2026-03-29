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
