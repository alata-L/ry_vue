-- 中国大陆工作日历（国务院办公厅放假与调休；生命支持类统计仅汇总 is_workday=1 的上报行）
-- 数据见 cst_cn_work_calendar_data.sql（由 sql/gen_cn_calendar.py 生成，年度更新后重新执行生成脚本并导入）

DROP TABLE IF EXISTS cst_cn_work_calendar;
CREATE TABLE cst_cn_work_calendar (
  cal_date    date        NOT NULL COMMENT '公历日期',
  is_workday  tinyint(1)  NOT NULL DEFAULT 0 COMMENT '1=工作日 0=休息日（含周末与法定节假日）',
  PRIMARY KEY (cal_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='中国大陆工作日历';
