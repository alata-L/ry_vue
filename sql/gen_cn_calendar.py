# -*- coding: utf-8 -*-
"""Generate cst_cn_work_calendar INSERTs: 2024-2030, 2024-2026 per State Council; 2027-2030 weekend-only fallback."""
from datetime import date, timedelta


def dr(s, e):
    a, b = date.fromisoformat(s), date.fromisoformat(e)
    d = a
    while d <= b:
        yield d
        d += timedelta(days=1)


def add_range(rest_set, s, e):
    for d in dr(s, e):
        rest_set.add(d)


REST = set()
WORK = set()

# 2024
add_range(REST, "2024-01-01", "2024-01-01")
add_range(REST, "2024-02-10", "2024-02-17")
add_range(REST, "2024-04-04", "2024-04-06")
add_range(REST, "2024-05-01", "2024-05-05")
add_range(REST, "2024-06-08", "2024-06-10")
add_range(REST, "2024-09-15", "2024-09-17")
add_range(REST, "2024-10-01", "2024-10-07")
for s in [
    "2024-02-04",
    "2024-02-18",
    "2024-04-07",
    "2024-04-28",
    "2024-05-11",
    "2024-09-14",
    "2024-09-29",
    "2024-10-12",
]:
    WORK.add(date.fromisoformat(s))

# 2025
add_range(REST, "2025-01-01", "2025-01-01")
add_range(REST, "2025-01-28", "2025-02-04")
add_range(REST, "2025-04-04", "2025-04-06")
add_range(REST, "2025-05-01", "2025-05-05")
add_range(REST, "2025-05-31", "2025-06-02")
add_range(REST, "2025-10-01", "2025-10-08")
for s in ["2025-01-26", "2025-02-08", "2025-04-27", "2025-09-28", "2025-10-11"]:
    WORK.add(date.fromisoformat(s))

# 2026
add_range(REST, "2026-01-01", "2026-01-03")
add_range(REST, "2026-02-15", "2026-02-23")
add_range(REST, "2026-04-04", "2026-04-06")
add_range(REST, "2026-05-01", "2026-05-05")
add_range(REST, "2026-06-19", "2026-06-21")
add_range(REST, "2026-09-25", "2026-09-27")
add_range(REST, "2026-10-01", "2026-10-07")
for s in ["2026-01-04", "2026-02-14", "2026-02-28", "2026-05-09", "2026-09-20", "2026-10-10"]:
    WORK.add(date.fromisoformat(s))

lines = []
d0, d1 = date(2010, 1, 1), date(2030, 12, 31)
d = d0
while d <= d1:
    if d < date(2024, 1, 1):
        # 2010-2023：无国务院日历数据时按周一至周五为工作日（节假日未剔除）
        iw = 0 if d.weekday() >= 5 else 1
    elif d in WORK:
        iw = 1
    elif d in REST:
        iw = 0
    else:
        iw = 0 if d.weekday() >= 5 else 1
    lines.append((d.isoformat(), iw))
    d += timedelta(days=1)

chunks = []
step = 400
for i in range(0, len(lines), step):
    part = lines[i : i + step]
    vals = ",".join("('{}',{})".format(a, b) for a, b in part)
    chunks.append("INSERT INTO cst_cn_work_calendar (cal_date, is_workday) VALUES " + vals + ";")

out_path = r"d:\eclipse-workspace\ry_vue\sql\cst_cn_work_calendar_data.sql"
with open(out_path, "w", encoding="utf-8") as f:
    f.write(
        "-- 中国大陆工作日历 2010-2030：2010-2023 仅周一至五为工作日（未含法定节假日）；"
        "2024-2026 按国务院办公厅放假与调休；2027-2030 暂为周一至五/周末（待国务院公布后运行 sql/gen_cn_calendar.py 更新）\n"
    )
    f.write("\n".join(chunks))
print(len(lines), "rows ->", out_path)
