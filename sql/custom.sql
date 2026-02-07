-- ============================================================
-- 定制模块 SQL（设备管理：生命支持类设备、重点设备）
-- 表前缀 cst_，执行前请先执行主库 ry_xxxx.sql
-- ============================================================

-- ----------------------------
-- 清理（需求变动时先执行此段再重新导入）
-- ----------------------------
DELETE FROM sys_role_menu WHERE menu_id >= 2000 AND menu_id <= 2999;
DELETE FROM sys_menu WHERE menu_id >= 2000 AND menu_id <= 2999;
DELETE FROM sys_dict_data WHERE dict_type IN ('cst_use_dept', 'cst_life_equip_type');
DELETE FROM sys_dict_type WHERE dict_type IN ('cst_use_dept', 'cst_life_equip_type');

DROP TABLE IF EXISTS cst_key_equip_usage;
DROP TABLE IF EXISTS cst_key_equip;
DROP TABLE IF EXISTS cst_life_usage;
DROP TABLE IF EXISTS cst_life_equip;

-- ----------------------------
-- 1、通用设备台账表 cst_life_equip
-- ----------------------------
CREATE TABLE cst_life_equip (
  id              BIGINT(20)    NOT NULL AUTO_INCREMENT    COMMENT '主键',
  equip_no        VARCHAR(64)   DEFAULT ''                 COMMENT '设备编号',
  sub_asset_no    VARCHAR(64)   DEFAULT ''                 COMMENT '子资产编号',
  equip_desc      VARCHAR(200)  DEFAULT ''                 COMMENT '设备描述',
  model           VARCHAR(100)  DEFAULT ''                 COMMENT '型号',
  total_value     DECIMAL(18,2) DEFAULT 0                  COMMENT '累计购置价值',
  cap_date        DATE          DEFAULT NULL               COMMENT '资本化日期',
  use_dept        VARCHAR(100)  DEFAULT ''                 COMMENT '使用科室(词典cst_use_dept)',
  equip_type      CHAR(1)       DEFAULT '1'                COMMENT '设备类型 1监护仪 2输液泵 3注射泵',
  status          CHAR(1)       DEFAULT '0'                COMMENT '状态 0正常 1停用',
  create_by       VARCHAR(64)   DEFAULT ''                COMMENT '创建者',
  create_time     DATETIME                                 COMMENT '创建时间',
  update_by       VARCHAR(64)   DEFAULT ''                COMMENT '更新者',
  update_time     DATETIME                                 COMMENT '更新时间',
  remark          VARCHAR(500)  DEFAULT NULL              COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_life_equip_type (equip_type),
  KEY idx_life_equip_dept (use_dept),
  KEY idx_life_equip_cap (cap_date)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='通用设备台账';

-- ----------------------------
-- 2、通用设备日使用上报表 cst_life_usage
-- ----------------------------
CREATE TABLE cst_life_usage (
  id              BIGINT(20)    NOT NULL AUTO_INCREMENT    COMMENT '主键',
  stat_date       DATE          NOT NULL                  COMMENT '统计日期',
  equip_type      CHAR(1)       NOT NULL                  COMMENT '设备类型 1监护仪 2输液泵 3注射泵',
  use_dept        VARCHAR(100)  DEFAULT ''                COMMENT '使用科室',
  used_count      INT(11)       DEFAULT 0                 COMMENT '当日使用台数',
  create_by       VARCHAR(64)   DEFAULT ''                COMMENT '创建者',
  create_time     DATETIME                                 COMMENT '创建时间',
  update_by       VARCHAR(64)   DEFAULT ''                COMMENT '更新者',
  update_time     DATETIME                                 COMMENT '更新时间',
  remark          VARCHAR(500)  DEFAULT NULL              COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_life_usage (stat_date, equip_type, use_dept),
  KEY idx_life_usage_date (stat_date),
  KEY idx_life_usage_type (equip_type),
  KEY idx_life_usage_dept (use_dept)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='通用设备日使用上报';

-- ----------------------------
-- 3、重点设备台账表 cst_key_equip（单价≥50万）
-- ----------------------------
CREATE TABLE cst_key_equip (
  id              BIGINT(20)    NOT NULL AUTO_INCREMENT    COMMENT '主键',
  equip_no        VARCHAR(64)   DEFAULT ''                 COMMENT '设备编号',
  sub_asset_no    VARCHAR(64)   DEFAULT ''                 COMMENT '子资产编号',
  equip_desc      VARCHAR(200)  DEFAULT ''                 COMMENT '设备描述',
  model           VARCHAR(100)  DEFAULT ''                 COMMENT '型号',
  total_value     DECIMAL(18,2) DEFAULT 0                  COMMENT '累计购置价值(原值)',
  cap_date        DATE          DEFAULT NULL               COMMENT '资本化日期',
  use_dept        VARCHAR(100)  DEFAULT ''                 COMMENT '使用科室(词典cst_use_dept)',
  status          CHAR(1)       DEFAULT '0'                COMMENT '状态 0正常 1停用',
  create_by       VARCHAR(64)   DEFAULT ''                COMMENT '创建者',
  create_time     DATETIME                                 COMMENT '创建时间',
  update_by       VARCHAR(64)   DEFAULT ''                COMMENT '更新者',
  update_time     DATETIME                                 COMMENT '更新时间',
  remark          VARCHAR(500)  DEFAULT NULL              COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_key_equip_dept (use_dept),
  KEY idx_key_equip_cap (cap_date)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='重点设备台账(单价≥50万)';

-- ----------------------------
-- 4、重点设备月度使用上报表 cst_key_equip_usage
-- ----------------------------
CREATE TABLE cst_key_equip_usage (
  id               BIGINT(20)    NOT NULL AUTO_INCREMENT    COMMENT '主键',
  equip_id         BIGINT(20)    NOT NULL                  COMMENT '重点设备ID',
  report_date      DATE          NOT NULL                  COMMENT '上报日期',
  treat_count      INT(11)       DEFAULT 0                 COMMENT '诊治例数',
  work_hours       DECIMAL(10,2) DEFAULT NULL              COMMENT '确认设备工作时间(小时)',
  unit_charge_price DECIMAL(10,2) DEFAULT NULL              COMMENT '单次/例收费价格',
  create_by        VARCHAR(64)   DEFAULT ''                COMMENT '创建者',
  create_time      DATETIME                                 COMMENT '创建时间',
  update_by        VARCHAR(64)   DEFAULT ''                COMMENT '更新者',
  update_time      DATETIME                                 COMMENT '更新时间',
  remark           VARCHAR(500)  DEFAULT NULL              COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_key_usage (equip_id, report_date),
  KEY idx_key_usage_date (report_date)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='重点设备月度使用上报';

-- ----------------------------
-- 5、词典类型：使用科室
-- ----------------------------
INSERT INTO sys_dict_type VALUES(100, '使用科室', 'cst_use_dept', '0', 'admin', sysdate(), '', NULL, '设备使用科室，可选可输入');

-- ----------------------------
-- 6、词典数据：使用科室列表
-- ----------------------------
INSERT INTO sys_dict_data VALUES(100, 1,  '一病区', '一病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(101, 2,  '四病区（心内科）', '四病区（心内科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(102, 3,  '五病区', '五病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(103, 4,  '七病区（神经外科）', '七病区（神经外科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(104, 5,  '八病区（普外科）', '八病区（普外科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(105, 6,  '九病区（肿瘤科）', '九病区（肿瘤科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(106, 7,  '十病区（肝病科）', '十病区（肝病科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(107, 8,  '十病区（消化科）', '十病区（消化科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(108, 9,  '十一病区（风湿科）', '十一病区（风湿科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(109, 10, '十一病区（肾脏内科）', '十一病区（肾脏内科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(110, 11, '十二病区', '十二病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(111, 12, '十二病区（内分泌科）', '十二病区（内分泌科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(112, 13, '十五病区', '十五病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(113, 14, '十六病区', '十六病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(114, 15, '十七病区', '十七病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(115, 16, '十八病区（免疫科）', '十八病区（免疫科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(116, 17, '十九病区（感染科）', '十九病区（感染科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(117, 18, '二十病区（传染科）', '二十病区（传染科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(118, 19, '二十四病区', '二十四病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(119, 20, '二十五病区', '二十五病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(120, 21, '二十六病区', '二十六病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(121, 22, '二十七病区', '二十七病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(122, 23, '二十九病区', '二十九病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(123, 24, '三十病区', '三十病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(124, 25, '三十二病区', '三十二病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(125, 26, '三十三病区', '三十三病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(126, 27, '三十四病区', '三十四病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(127, 28, '三十五病区', '三十五病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(128, 29, '三十六病区', '三十六病区', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(129, 30, 'PICU/23W', 'PICU/23W', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(130, 31, '研究型病房/28W', '研究型病房/28W', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(131, 32, '眼科/66W', '眼科/66W', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(132, 33, '七十病区（呼吸科）', '七十病区（呼吸科）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(133, 34, 'B超室', 'B超室', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(134, 35, '放射科', '放射科', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(135, 36, '肺功能室', '肺功能室', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(136, 37, '核医学科', '核医学科', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(137, 38, '麻醉科', '麻醉科', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(138, 39, '脑电图室', '脑电图室', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(139, 40, '胃镜室', '胃镜室', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(140, 41, '心超室', '心超室', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(141, 42, '心导管室', '心导管室', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(142, 43, '心电与心功能室', '心电与心功能室', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(143, 44, '心胸外科手术室（设备）', '心胸外科手术室（设备）', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(144, 45, '支气管镜室', '支气管镜室', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(145, 46, '耳鼻咽喉头颈外科门诊', '耳鼻咽喉头颈外科门诊', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(146, 47, '特需门诊', '特需门诊', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(147, 48, '急诊', '急诊', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(148, 49, '急诊培训中心', '急诊培训中心', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(149, 50, '教育培训部', '教育培训部', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(150, 51, '康复科', '康复科', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(151, 52, '呼吸综合科', '呼吸综合科', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(152, 53, '神经心理康复病房', '神经心理康复病房', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(153, 54, '镇静中心', '镇静中心', 'cst_use_dept', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');

-- ----------------------------
-- 7、词典类型：通用设备类型
-- ----------------------------
INSERT INTO sys_dict_type VALUES(101, '通用设备类型', 'cst_life_equip_type', '0', 'admin', sysdate(), '', NULL, '监护仪/输液泵/注射泵');
INSERT INTO sys_dict_data VALUES(154, 1, '监护仪', '1', 'cst_life_equip_type', '', '', 'Y', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(155, 2, '输液泵', '2', 'cst_life_equip_type', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_dict_data VALUES(156, 3, '注射泵', '3', 'cst_life_equip_type', '', '', 'N', '0', 'admin', sysdate(), '', NULL, '');

-- ----------------------------
-- 8、菜单：设备管理（一级）
-- ----------------------------
INSERT INTO sys_menu VALUES(2000, '设备管理', 0, 3, 'custom', NULL, '', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', sysdate(), '', NULL, '设备管理目录');

-- 通用设备
INSERT INTO sys_menu VALUES(2001, '通用设备', 2000, 1, 'lifeEquip', 'custom/lifeEquip/index', '', '', 1, 0, 'C', '0', '0', 'custom:lifeEquip:list', 'build', 'admin', sysdate(), '', NULL, '通用设备台账');
INSERT INTO sys_menu VALUES(2002, '设备台账查询', 2001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeEquip:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2003, '设备台账新增', 2001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeEquip:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2004, '设备台账修改', 2001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeEquip:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2005, '设备台账删除', 2001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeEquip:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2006, '设备台账导出', 2001, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeEquip:export', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu VALUES(2010, '通用设备上报', 2000, 2, 'lifeUsage', 'custom/lifeUsage/index', '', '', 1, 0, 'C', '0', '0', 'custom:lifeUsage:list', 'edit', 'admin', sysdate(), '', NULL, '通用设备日使用上报');
INSERT INTO sys_menu VALUES(2011, '使用数据查询', 2010, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeUsage:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2012, '使用数据新增', 2010, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeUsage:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2013, '使用数据修改', 2010, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeUsage:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2014, '使用数据删除', 2010, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeUsage:remove', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu VALUES(2020, '通用设备统计', 2000, 3, 'lifeStats', 'custom/lifeStats/index', '', '', 1, 0, 'C', '0', '0', 'custom:lifeStats:list', 'chart', 'admin', sysdate(), '', NULL, '科室/全院维度统计');

-- 重点设备
INSERT INTO sys_menu VALUES(2030, '重点设备', 2000, 4, 'keyEquip', 'custom/keyEquip/index', '', '', 1, 0, 'C', '0', '0', 'custom:keyEquip:list', 'build', 'admin', sysdate(), '', NULL, '重点设备台账(单价≥50万)');
INSERT INTO sys_menu VALUES(2031, '重点设备查询', 2030, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyEquip:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2032, '重点设备新增', 2030, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyEquip:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2033, '重点设备修改', 2030, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyEquip:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2034, '重点设备删除', 2030, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyEquip:remove', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2035, '重点设备导出', 2030, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyEquip:export', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu VALUES(2040, '重点设备上报', 2000, 5, 'keyUsage', 'custom/keyUsage/index', '', '', 1, 0, 'C', '0', '0', 'custom:keyUsage:list', 'edit', 'admin', sysdate(), '', NULL, '月度使用数据');
INSERT INTO sys_menu VALUES(2041, '使用统计查询', 2040, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyUsage:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2042, '使用统计新增', 2040, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyUsage:add', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2043, '使用统计修改', 2040, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyUsage:edit', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES(2044, '使用统计删除', 2040, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyUsage:remove', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu VALUES(2045, '重点设备统计', 2000, 6, 'keyStats', 'custom/keyStats/index', '', '', 1, 0, 'C', '0', '0', 'custom:keyEquip:list', 'chart', 'admin', sysdate(), '', NULL, '科室/设备/时序统计');

-- 为角色2（普通角色）分配设备管理菜单：仅查询与统计，不含新增/修改/删除；不可见系统管理、系统监控（参考 ry_20250522.sql 中角色2原分配，此处先收回主库菜单）
DELETE FROM sys_role_menu WHERE role_id = 2 AND menu_id < 2000;
-- 设备管理目录 + 通用设备（仅查询）+ 通用设备上报（仅查询）+ 通用/重点设备统计 + 重点设备（仅查询）+ 重点设备上报（仅查询）
INSERT INTO sys_role_menu VALUES (2, 2000);
INSERT INTO sys_role_menu VALUES (2, 2001); INSERT INTO sys_role_menu VALUES (2, 2002);
INSERT INTO sys_role_menu VALUES (2, 2010); INSERT INTO sys_role_menu VALUES (2, 2011);
INSERT INTO sys_role_menu VALUES (2, 2020);
INSERT INTO sys_role_menu VALUES (2, 2030); INSERT INTO sys_role_menu VALUES (2, 2031);
INSERT INTO sys_role_menu VALUES (2, 2040); INSERT INTO sys_role_menu VALUES (2, 2041);
INSERT INTO sys_role_menu VALUES (2, 2045);

-- ----------------------------
-- 9、追加用户：财务部门 / 普通角色 / 密码 123456
-- 依赖主库：sys_dept(106 财务部门)、sys_role(2 普通角色)、sys_post(2)
-- ----------------------------
INSERT INTO sys_user VALUES(3, 106, 'caiwu', '财务用户', '00', '', '', '0', '', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '0', '0', '', NULL, NULL, 'admin', sysdate(), '', NULL, '财务部门-普通角色');
INSERT INTO sys_user_role VALUES (3, 2);
INSERT INTO sys_user_post VALUES (3, 2);
