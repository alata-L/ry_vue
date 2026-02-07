package com.ruoyi.custom.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.custom.mapper.CstLifeUsageMapper;
import com.ruoyi.custom.mapper.CstLifeEquipMapper;
import com.ruoyi.custom.service.ICstLifeStatsService;

/**
 * 生命支持类设备统计 服务层实现（首页概览）
 *
 * @author ruoyi
 */
@Service
public class CstLifeStatsServiceImpl implements ICstLifeStatsService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private CstLifeUsageMapper cstLifeUsageMapper;
    @Autowired
    private CstLifeEquipMapper cstLifeEquipMapper;

    @Override
    public List<Map<String, Object>> getUsageTrend() {
        LocalDate now = LocalDate.now();
        String start = now.withDayOfYear(1).format(FMT);
        String end = now.format(FMT);
        List<Map<String, Object>> list = cstLifeUsageMapper.sumUsageByMonthAndType(start, end);
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getUsageByDept() {
        LocalDate now = LocalDate.now();
        String start = now.withDayOfYear(1).format(FMT);
        String end = now.format(FMT);
        List<Map<String, Object>> list = cstLifeUsageMapper.sumUsageByDeptInRange(start, end);
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public Map<String, Long> getEquipCountByType() {
        List<Map<String, Object>> list = cstLifeEquipMapper.countByDeptAndType();
        Map<String, Long> result = new java.util.HashMap<>();
        result.put("1", 0L); // 监护仪
        result.put("2", 0L); // 输液泵
        result.put("3", 0L); // 注射泵
        if (list != null) {
            for (Map<String, Object> row : list) {
                Object equipType = row.get("equipType");
                Object cnt = row.get("cnt");
                if (equipType != null && cnt != null) {
                    String typeStr = equipType.toString();
                    long count = Long.parseLong(cnt.toString());
                    result.put(typeStr, result.getOrDefault(typeStr, 0L) + count);
                }
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getUsageTrendByType(String equipType, String groupBy) {
        if (equipType == null || equipType.isEmpty()) {
            return new ArrayList<>();
        }
        if (groupBy == null || groupBy.isEmpty()) {
            groupBy = "day";
        }
        LocalDate now = LocalDate.now();
        String start = now.withDayOfYear(1).format(FMT);
        String end = now.format(FMT);
        List<Map<String, Object>> list = cstLifeUsageMapper.sumUsageTrendByType(start, end, equipType, groupBy);
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getUsageDeptRankByType(String equipType, String groupBy) {
        if (equipType == null || equipType.isEmpty()) {
            return new ArrayList<>();
        }
        if (groupBy == null || groupBy.isEmpty()) {
            groupBy = "day";
        }
        LocalDate now = LocalDate.now();
        String start = now.withDayOfYear(1).format(FMT);
        String end = now.format(FMT);
        List<Map<String, Object>> list = cstLifeUsageMapper.sumUsageDeptRankByType(start, end, equipType, groupBy);
        // 按statPeriod分组，每个period取TOP5
        return filterTop5ByPeriod(list);
    }

    private List<Map<String, Object>> filterTop5ByPeriod(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, List<Map<String, Object>>> byPeriod = new java.util.HashMap<>();
        for (Map<String, Object> row : list) {
            Object period = row.get("statPeriod");
            if (period != null) {
                String periodStr = period.toString();
                byPeriod.computeIfAbsent(periodStr, k -> new java.util.ArrayList<>()).add(row);
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (List<Map<String, Object>> periodList : byPeriod.values()) {
            periodList.sort((a, b) -> {
                Object avgA = a.get("avgDailyCount");
                Object avgB = b.get("avgDailyCount");
                double valA = avgA != null ? Double.parseDouble(avgA.toString()) : 0;
                double valB = avgB != null ? Double.parseDouble(avgB.toString()) : 0;
                return Double.compare(valB, valA); // 降序
            });
            result.addAll(periodList.subList(0, Math.min(5, periodList.size())));
        }
        return result;
    }
}
