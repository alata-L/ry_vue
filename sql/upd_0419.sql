
INSERT IGNORE INTO sys_menu VALUES(2007, '设备台账导入', 2001, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeEquip:import', '#', 'admin', sysdate(), '', NULL, '通用设备台账 Excel 导入');
INSERT IGNORE INTO sys_menu VALUES(2036, '重点设备导入', 2030, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyEquip:import', '#', 'admin', sysdate(), '', NULL, '重点设备台账 Excel 导入');
-- 通用设备上报 / 重点设备上报：导入按钮（数据写入上报表）
INSERT IGNORE INTO sys_menu VALUES(2016, '通用设备上报导入', 2010, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:lifeUsage:import', '#', 'admin', sysdate(), '', NULL, '生命支持类日使用上报 Excel 导入');
INSERT IGNORE INTO sys_menu VALUES(2071, '重点设备上报导入', 2040, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'custom:keyUsage:import', '#', 'admin', sysdate(), '', NULL, '重点设备月度使用上报 Excel 导入');
