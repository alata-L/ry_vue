package com.ruoyi.custom.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.custom.domain.CstKeyEquip;
import com.ruoyi.custom.mapper.CstKeyEquipMapper;
import com.ruoyi.custom.mapper.CstKeyEquipUsageMapper;
import com.ruoyi.custom.service.ICstKeyEquipService;
import com.ruoyi.custom.service.ICstKeyStatsService;

/**
 * 重点设备统计 服务层实现
 *
 * @author ruoyi
 */
@Service
public class CstKeyStatsServiceImpl implements ICstKeyStatsService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private CstKeyEquipMapper cstKeyEquipMapper;
    @Autowired
    private CstKeyEquipUsageMapper cstKeyEquipUsageMapper;
    @Autowired
    private ICstKeyEquipService cstKeyEquipService;

    @Override
    public Map<String, Object> getSummaryAndDeptList() {
        LocalDate now = LocalDate.now();
        String thisMonthStart = now.withDayOfMonth(1).format(FMT);
        String thisMonthEnd = now.format(FMT);
        String lastMonthStart = now.minusMonths(1).withDayOfMonth(1).format(FMT);
        String lastMonthEnd = now.withDayOfMonth(1).minusDays(1).format(FMT);
        String thisYearStart = now.withDayOfYear(1).format(FMT);
        String thisYearEnd = now.format(FMT);
        String lastYearStart = now.minusYears(1).withDayOfYear(1).format(FMT);
        String lastYearEnd = now.minusYears(1).withMonth(12).withDayOfMonth(31).format(FMT);

        Map<String, Object> summary = new HashMap<>();
        Map<String, Object> equipSummary = cstKeyEquipMapper.selectSummary();
        if (equipSummary != null) {
            summary.put("equipCount", toLong(equipSummary.get("equipCount")));
            summary.put("totalValue", equipSummary.get("totalValue"));
        } else {
            summary.put("equipCount", 0L);
            summary.put("totalValue", BigDecimal.ZERO);
        }
        
        // 按价格范围统计（≥50万）
        Map<String, Object> equipSummary50 = cstKeyEquipMapper.selectSummaryByValue50();
        if (equipSummary50 != null) {
            summary.put("equipCount50", toLong(equipSummary50.get("equipCount")));
            summary.put("totalValue50", equipSummary50.get("totalValue"));
        } else {
            summary.put("equipCount50", 0L);
            summary.put("totalValue50", BigDecimal.ZERO);
        }
        
        // 按价格范围统计（≥100万）
        Map<String, Object> equipSummary100 = cstKeyEquipMapper.selectSummaryByValue100();
        if (equipSummary100 != null) {
            summary.put("equipCount100", toLong(equipSummary100.get("equipCount")));
            summary.put("totalValue100", equipSummary100.get("totalValue"));
        } else {
            summary.put("equipCount100", 0L);
            summary.put("totalValue100", BigDecimal.ZERO);
        }
        
        putChargeAndTreat(summary, "ThisMonth", sumInRange(thisMonthStart, thisMonthEnd));
        putChargeAndTreat(summary, "LastMonth", sumInRange(lastMonthStart, lastMonthEnd));
        putChargeAndTreat(summary, "ThisYear", sumInRange(thisYearStart, thisYearEnd));
        putChargeAndTreat(summary, "LastYear", sumInRange(lastYearStart, lastYearEnd));

        List<Map<String, Object>> deptEquipStats = cstKeyEquipMapper.selectDeptEquipStats();
        List<Map<String, Object>> deptThisMonth = cstKeyEquipUsageMapper.sumChargeAndTreatByDeptInRange(thisMonthStart, thisMonthEnd);
        List<Map<String, Object>> deptLastMonth = cstKeyEquipUsageMapper.sumChargeAndTreatByDeptInRange(lastMonthStart, lastMonthEnd);
        List<Map<String, Object>> deptThisYear = cstKeyEquipUsageMapper.sumChargeAndTreatByDeptInRange(thisYearStart, thisYearEnd);
        List<Map<String, Object>> deptLastYear = cstKeyEquipUsageMapper.sumChargeAndTreatByDeptInRange(lastYearStart, lastYearEnd);

        Map<String, Map<String, Object>> byDept = new HashMap<>();
        for (Map<String, Object> row : deptEquipStats) {
            String useDept = (String) row.get("useDept");
            Map<String, Object> m = new HashMap<>();
            m.put("useDept", useDept);
            m.put("equipCount", toLong(row.get("equipCount")));
            m.put("totalValue", row.get("totalValue"));
            m.put("chargeThisMonth", BigDecimal.ZERO);
            m.put("chargeLastMonth", BigDecimal.ZERO);
            m.put("chargeThisYear", BigDecimal.ZERO);
            m.put("chargeLastYear", BigDecimal.ZERO);
            m.put("treatThisMonth", 0L);
            m.put("treatLastMonth", 0L);
            m.put("treatThisYear", 0L);
            m.put("treatLastYear", 0L);
            byDept.put(useDept, m);
        }
        mergeDeptUsage(byDept, deptThisMonth, "ThisMonth");
        mergeDeptUsage(byDept, deptLastMonth, "LastMonth");
        mergeDeptUsage(byDept, deptThisYear, "ThisYear");
        mergeDeptUsage(byDept, deptLastYear, "LastYear");

        Map<String, Object> result = new HashMap<>();
        result.put("summary", summary);
        result.put("deptList", new ArrayList<>(byDept.values()));
        return result;
    }

    @Override
    public List<Map<String, Object>> getDeptEquipList(String useDept) {
        if (useDept == null || useDept.isEmpty()) {
            return new ArrayList<>();
        }
        CstKeyEquip query = new CstKeyEquip();
        query.setUseDept(useDept);
        query.setStatus("0");
        List<CstKeyEquip> equips = cstKeyEquipService.selectCstKeyEquipList(query);
        if (equips == null || equips.isEmpty()) {
            return new ArrayList<>();
        }

        LocalDate now = LocalDate.now();
        String thisMonthStart = now.withDayOfMonth(1).format(FMT);
        String thisMonthEnd = now.format(FMT);
        String lastMonthStart = now.minusMonths(1).withDayOfMonth(1).format(FMT);
        String lastMonthEnd = now.withDayOfMonth(1).minusDays(1).format(FMT);
        String thisYearStart = now.withDayOfYear(1).format(FMT);
        String thisYearEnd = now.format(FMT);
        String lastYearStart = now.minusYears(1).withDayOfYear(1).format(FMT);
        String lastYearEnd = now.minusYears(1).withMonth(12).withDayOfMonth(31).format(FMT);

        List<Map<String, Object>> m1 = cstKeyEquipUsageMapper.sumChargeAndTreatByEquipInRangeAndDept(useDept, thisMonthStart, thisMonthEnd);
        List<Map<String, Object>> m2 = cstKeyEquipUsageMapper.sumChargeAndTreatByEquipInRangeAndDept(useDept, lastMonthStart, lastMonthEnd);
        List<Map<String, Object>> m3 = cstKeyEquipUsageMapper.sumChargeAndTreatByEquipInRangeAndDept(useDept, thisYearStart, thisYearEnd);
        List<Map<String, Object>> m4 = cstKeyEquipUsageMapper.sumChargeAndTreatByEquipInRangeAndDept(useDept, lastYearStart, lastYearEnd);

        Map<Long, Map<String, Object>> usageByEquip = new HashMap<>();
        for (CstKeyEquip e : equips) {
            Map<String, Object> row = new HashMap<>();
            row.put("equipId", e.getId());
            row.put("equipNo", e.getEquipNo());
            row.put("equipDesc", e.getEquipDesc());
            row.put("totalValue", e.getTotalValue());
            row.put("chargeThisMonth", BigDecimal.ZERO);
            row.put("chargeLastMonth", BigDecimal.ZERO);
            row.put("chargeThisYear", BigDecimal.ZERO);
            row.put("chargeLastYear", BigDecimal.ZERO);
            row.put("treatThisMonth", 0L);
            row.put("treatLastMonth", 0L);
            row.put("treatThisYear", 0L);
            row.put("treatLastYear", 0L);
            usageByEquip.put(e.getId(), row);
        }
        mergeEquipUsage(usageByEquip, m1, "ThisMonth");
        mergeEquipUsage(usageByEquip, m2, "LastMonth");
        mergeEquipUsage(usageByEquip, m3, "ThisYear");
        mergeEquipUsage(usageByEquip, m4, "LastYear");
        return new ArrayList<>(usageByEquip.values());
    }

    @Override
    public List<Map<String, Object>> getEquipSeries(Long equipId, String groupBy, String beginTime, String endTime) {
        if (equipId == null) {
            return new ArrayList<>();
        }
        if (groupBy == null || groupBy.isEmpty()) {
            groupBy = "day";
        }
        LocalDate now = LocalDate.now();
        if (beginTime == null || beginTime.isEmpty() || endTime == null || endTime.isEmpty()) {
            switch (groupBy) {
                case "week":
                    beginTime = now.minusYears(1).format(FMT);
                    endTime = now.format(FMT);
                    break;
                case "month":
                    beginTime = now.minusYears(3).format(FMT);
                    endTime = now.format(FMT);
                    break;
                case "year":
                    beginTime = now.minusYears(6).format(FMT);
                    endTime = now.format(FMT);
                    break;
                default:
                    beginTime = now.minusMonths(3).format(FMT);
                    endTime = now.format(FMT);
                    break;
            }
        }
        List<Map<String, Object>> list = cstKeyEquipUsageMapper.selectSeriesByEquip(equipId, beginTime, endTime, groupBy);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : list) {
            Map<String, Object> out = new HashMap<>(row);
            // 保留reportDate和weekWorkDays字段（如果存在）
            Object reportDate = row.get("reportDate");
            if (reportDate != null) {
                out.put("reportDate", reportDate);
            }
            Object weekWorkDays = row.get("weekWorkDays");
            if (weekWorkDays != null) {
                out.put("weekWorkDays", weekWorkDays);
            }
            Object tc = row.get("totalCharge");
            Object treat = row.get("treatCount");
            BigDecimal totalCharge = tc != null ? toBigDecimal(tc) : BigDecimal.ZERO;
            long treatCount = toLong(treat);
            out.put("totalCharge", totalCharge);
            if (treatCount > 0) {
                out.put("avgChargePerCase", totalCharge.divide(BigDecimal.valueOf(treatCount), 2, RoundingMode.HALF_UP));
            } else {
                out.put("avgChargePerCase", null);
            }
            result.add(out);
        }
        return result;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getSummaryMonthlySeries() {
        LocalDate now = LocalDate.now();
        int thisYear = now.getYear();
        int lastYear = thisYear - 1;
        String thisYearStart = LocalDate.of(thisYear, 1, 1).format(FMT);
        String thisYearEnd = now.format(FMT);
        String lastYearStart = LocalDate.of(lastYear, 1, 1).format(FMT);
        String lastYearEnd = LocalDate.of(lastYear, 12, 31).format(FMT);

        List<Map<String, Object>> thisYearRaw = cstKeyEquipUsageMapper.sumChargeAndTreatByMonthInRange(thisYearStart, thisYearEnd);
        List<Map<String, Object>> lastYearRaw = cstKeyEquipUsageMapper.sumChargeAndTreatByMonthInRange(lastYearStart, lastYearEnd);

        Map<String, Map<String, Object>> thisYearByPeriod = toMapByPeriod(thisYearRaw);
        Map<String, Map<String, Object>> lastYearByPeriod = toMapByPeriod(lastYearRaw);

        List<Map<String, Object>> thisYearList = new ArrayList<>();
        for (int m = 1; m <= now.getMonthValue(); m++) {
            String period = String.format("%d-%02d", thisYear, m);
            thisYearList.add(monthRow(period, thisYearByPeriod.get(period)));
        }
        List<Map<String, Object>> lastYearList = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            String period = String.format("%d-%02d", lastYear, m);
            lastYearList.add(monthRow(period, lastYearByPeriod.get(period)));
        }

        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("thisYear", thisYearList);
        result.put("lastYear", lastYearList);
        return result;
    }

    @Override
    public List<Map<String, Object>> getKeyEquipTopByCharge(int limit) {
        if (limit <= 0) limit = 10;
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        String start = LocalDate.of(year, 1, 1).format(FMT);
        String end = now.format(FMT);
        List<Map<String, Object>> list = cstKeyEquipUsageMapper.topEquipByChargeInRange(start, end, limit);
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public Map<String, List<Map<String, Object>>> getTopEquipMonthlySeriesByValue(Long minValue, int limit) {
        if (minValue == null) minValue = 500000L;
        if (limit <= 0) limit = 10;
        
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        String thisYearStart = LocalDate.of(year, 1, 1).format(FMT);
        String thisYearEnd = now.format(FMT);
        String lastYearStart = LocalDate.of(year - 1, 1, 1).format(FMT);
        String lastYearEnd = LocalDate.of(year - 1, 12, 31).format(FMT);

        // 获取今年TOP10设备ID列表
        List<Map<String, Object>> topEquipList = cstKeyEquipUsageMapper.topEquipByChargeInRangeAndValue(thisYearStart, thisYearEnd, minValue, limit);
        List<Long> equipIds = new ArrayList<>();
        if (topEquipList != null && !topEquipList.isEmpty()) {
            for (Map<String, Object> row : topEquipList) {
                Object equipId = row.get("equipId");
                if (equipId != null) {
                    equipIds.add(toLong(equipId));
                }
            }
        }

        // 获取今年和去年的按月序列数据
        List<Map<String, Object>> thisYearList = new ArrayList<>();
        List<Map<String, Object>> lastYearList = new ArrayList<>();
        
        if (!equipIds.isEmpty()) {
            thisYearList = cstKeyEquipUsageMapper.topEquipMonthlySeries(thisYearStart, thisYearEnd, minValue, equipIds);
            lastYearList = cstKeyEquipUsageMapper.topEquipMonthlySeries(lastYearStart, lastYearEnd, minValue, equipIds);
        }

        // 构建完整的12个月数据
        List<Map<String, Object>> thisYearFull = buildFullYearSeries(thisYearList, year);
        List<Map<String, Object>> lastYearFull = buildFullYearSeries(lastYearList, year - 1);

        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("thisYear", thisYearFull);
        result.put("lastYear", lastYearFull);
        return result;
    }

    private List<Map<String, Object>> buildFullYearSeries(List<Map<String, Object>> list, int year) {
        Map<String, Map<String, Object>> byPeriod = new HashMap<>();
        if (list != null) {
            for (Map<String, Object> row : list) {
                Object period = row.get("period");
                if (period != null) {
                    byPeriod.put(period.toString(), row);
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            String period = String.format("%04d-%02d", year, month);
            Map<String, Object> row = byPeriod.get(period);
            if (row == null) {
                row = new HashMap<>();
                row.put("period", period);
                row.put("totalCharge", BigDecimal.ZERO);
                row.put("totalWorkHours", 0L);
                row.put("totalTreat", 0L);
            }
            result.add(row);
        }
        return result;
    }

    private static Map<String, Map<String, Object>> toMapByPeriod(List<Map<String, Object>> list) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        if (list != null) {
            for (Map<String, Object> row : list) {
                Object p = row.get("period");
                if (p != null) map.put(p.toString(), row);
            }
        }
        return map;
    }

    private static Map<String, Object> monthRow(String period, Map<String, Object> data) {
        Map<String, Object> row = new HashMap<>();
        row.put("period", period);
        row.put("totalCharge", data != null && data.get("totalCharge") != null ? data.get("totalCharge") : BigDecimal.ZERO);
        row.put("totalTreat", data != null && data.get("totalTreat") != null ? data.get("totalTreat") : 0L);
        return row;
    }

    private Map<String, Object> sumInRange(String start, String end) {
        Map<String, Object> m = cstKeyEquipUsageMapper.sumChargeAndTreatInRange(start, end);
        return m != null ? m : new HashMap<>();
    }

    private void putChargeAndTreat(Map<String, Object> summary, String suffix, Map<String, Object> data) {
        summary.put("charge" + suffix, data.get("totalCharge") != null ? data.get("totalCharge") : BigDecimal.ZERO);
        summary.put("treat" + suffix, toLong(data.get("totalTreat")));
    }

    private void mergeDeptUsage(Map<String, Map<String, Object>> byDept, List<Map<String, Object>> list, String suffix) {
        if (list == null) return;
        for (Map<String, Object> row : list) {
            String useDept = (String) row.get("useDept");
            Map<String, Object> m = byDept.get(useDept);
            if (m != null) {
                m.put("charge" + suffix, row.get("totalCharge") != null ? row.get("totalCharge") : BigDecimal.ZERO);
                m.put("treat" + suffix, toLong(row.get("totalTreat")));
            }
        }
    }

    private void mergeEquipUsage(Map<Long, Map<String, Object>> byEquip, List<Map<String, Object>> list, String suffix) {
        if (list == null) return;
        for (Map<String, Object> row : list) {
            Long equipId = toLongObj(row.get("equipId"));
            if (equipId == null) continue;
            Map<String, Object> m = byEquip.get(equipId);
            if (m != null) {
                m.put("charge" + suffix, row.get("totalCharge") != null ? row.get("totalCharge") : BigDecimal.ZERO);
                m.put("treat" + suffix, toLong(row.get("totalTreat")));
            }
        }
    }

    private static long toLong(Object o) {
        if (o == null) return 0L;
        if (o instanceof Number) return ((Number) o).longValue();
        try {
            return Long.parseLong(o.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private static Long toLongObj(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).longValue();
        try {
            return Long.parseLong(o.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static BigDecimal toBigDecimal(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal) return (BigDecimal) o;
        if (o instanceof Number) return BigDecimal.valueOf(((Number) o).doubleValue());
        try {
            return new BigDecimal(o.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
