-- ============================================================
-- 上报数据测试（请先执行 custom.sql 建表，再执行 custom_excel_data.sql 导入设备台账，最后执行本文件）
-- 包含：cst_life_usage（通用设备日使用）、cst_key_equip_usage（重点设备月度使用）
-- ============================================================

-- ----------------------------
-- 1、通用设备日使用上报 cst_life_usage（近期约 30 天 + 去年同月若干天）
-- 数据延续到 2026-02-02，便于看统计趋势；equip_type 1监护仪 2输液泵 3注射泵
-- ----------------------------
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-01', '1', '一病区', 3, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-01', '1', '麻醉科', 5, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-01', '1', '十六病区', 8, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-01', '2', '一病区', 2, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-01', '2', '十六病区', 4, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-02', '1', '一病区', 4, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-02', '1', '麻醉科', 6, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-02', '1', '十六病区', 7, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-03', '1', '十病区（消化科）', 2, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-05', '1', '十七病区', 5, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-06', '1', '一病区', 3, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-06', '1', '麻醉科', 5, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-10', '1', '十六病区', 6, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-10', '3', '麻醉科', 2, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-25', '1', '一病区', 5, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-28', '1', '麻醉科', 6, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-01-30', '1', '十六病区', 9, now(), now());
-- 2026-02 若干天，最新日 2026-02-02
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-02-01', '1', '一病区', 4, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-02-01', '1', '麻醉科', 6, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-02-02', '1', '一病区', 6, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-02-02', '1', '麻醉科', 8, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-02-02', '1', '十六病区', 10, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-02-02', '2', '一病区', 4, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2026-02-02', '3', '麻醉科', 3, now(), now());
-- 去年同月对比用
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2025-12-01', '1', '一病区', 3, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2025-12-01', '1', '麻醉科', 4, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2025-12-02', '1', '十六病区', 7, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2025-12-15', '1', '一病区', 4, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2025-12-15', '2', '十六病区', 3, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2025-11-01', '1', '一病区', 2, now(), now());
INSERT INTO cst_life_usage (stat_date, equip_type, use_dept, used_count, create_time, update_time) VALUES ('2025-11-01', '1', '麻醉科', 5, now(), now());

-- ----------------------------
-- 2、重点设备月度使用上报 cst_key_equip_usage
-- equip_id 对应 cst_key_equip 的主键；覆盖到 2026-02-02 便于看统计趋势
-- ----------------------------
-- equip_id=1 若干月（含 2026-01～2026-02）
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (1, '2025-01-15', 12, 48.50, 120.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (1, '2025-02-15', 15, 52.00, 120.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (1, '2025-06-15', 18, 60.00, 125.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (1, '2025-11-15', 20, 72.00, 130.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (1, '2025-12-15', 22, 80.00, 130.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (1, '2026-01-15', 14, 56.00, 135.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (1, '2026-02-02', 16, 60.00, 135.00, now(), now());
-- equip_id=2
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (2, '2025-01-10', 8, 32.00, 200.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (2, '2025-07-10', 10, 40.00, 200.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (2, '2025-12-10', 12, 45.00, 220.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (2, '2026-01-10', 9, 38.00, 220.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (2, '2026-02-02', 10, 42.00, 225.00, now(), now());
-- equip_id=3
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (3, '2025-03-01', 25, 100.00, 150.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (3, '2025-09-01', 28, 112.00, 155.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (3, '2025-12-01', 30, 120.00, 160.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (3, '2026-01-01', 16, 64.00, 160.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (3, '2026-02-02', 18, 72.00, 162.00, now(), now());
-- equip_id=4,5,6 今年到 2026-02-02
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (4, '2025-05-20', 6, 24.00, 380.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (4, '2025-12-20', 7, 28.00, 400.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (4, '2026-01-20', 4, 16.00, 400.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (4, '2026-02-02', 5, 20.00, 405.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (5, '2025-04-05', 14, 56.00, 180.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (5, '2025-11-05', 16, 65.00, 190.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (5, '2025-12-05', 17, 70.00, 190.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (5, '2026-01-05', 10, 42.00, 195.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (5, '2026-02-02', 12, 48.00, 198.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (6, '2025-08-01', 9, 36.00, 260.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (6, '2025-12-01', 11, 44.00, 270.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (6, '2026-01-01', 6, 24.00, 270.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (6, '2026-02-02', 8, 32.00, 272.00, now(), now());
-- equip_id=7～12 到 2026-02-02
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (7, '2025-12-12', 5, 20.00, 300.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (7, '2026-01-12', 3, 12.00, 300.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (7, '2026-02-02', 4, 16.00, 302.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (8, '2025-11-08', 19, 76.00, 140.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (8, '2025-12-08', 21, 84.00, 140.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (8, '2026-01-08', 11, 44.00, 145.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (8, '2026-02-02', 13, 52.00, 145.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (9, '2025-06-18', 13, 52.00, 210.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (9, '2025-12-18', 15, 60.00, 220.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (9, '2026-01-18', 8, 32.00, 220.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (9, '2026-02-02', 10, 40.00, 222.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (10, '2025-02-28', 7, 28.00, 350.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (10, '2025-12-28', 8, 32.00, 360.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (10, '2026-01-28', 5, 20.00, 360.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (10, '2026-02-02', 6, 24.00, 362.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (11, '2025-10-01', 22, 88.00, 110.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (11, '2025-12-01', 24, 96.00, 115.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (11, '2026-01-01', 13, 52.00, 115.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (11, '2026-02-02', 15, 60.00, 118.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (12, '2025-07-15', 4, 16.00, 450.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (12, '2025-12-15', 5, 20.00, 480.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (12, '2026-01-15', 2, 8.00, 480.00, now(), now());
INSERT INTO cst_key_equip_usage (equip_id, report_date, treat_count, work_hours, unit_charge_price, create_time, update_time) VALUES (12, '2026-02-02', 3, 12.00, 482.00, now(), now());
