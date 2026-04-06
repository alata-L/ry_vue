package com.ruoyi.custom.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.custom.domain.UseDeptScope;
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

    /** common 角色：无科室时 Mapper 用 1=0；否则仅统计这些 use_dept */
    private static Boolean deniedFlag(UseDeptScope scope) {
        if (scope.isUnrestricted()) {
            return null;
        }
        return scope.isDenied() ? Boolean.TRUE : null;
    }

    private static List<String> useDeptListParam(UseDeptScope scope) {
        if (scope.isUnrestricted() || scope.isDenied()) {
            return null;
        }
        return scope.getUseDeptCodes();
    }

    @Override
    public List<Map<String, Object>> getUsageTrend(UseDeptScope scope) {
        if (scope.isDenied()) {
            return new ArrayList<>();
        }
        LocalDate now = LocalDate.now();
        String start = now.withDayOfYear(1).format(FMT);
        String end = now.format(FMT);
        List<Map<String, Object>> list = cstLifeUsageMapper.sumUsageByMonthAndType(start, end, deniedFlag(scope), useDeptListParam(scope));
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getUsageByDept(UseDeptScope scope) {
        if (scope.isDenied()) {
            return new ArrayList<>();
        }
        LocalDate now = LocalDate.now();
        String start = now.withDayOfYear(1).format(FMT);
        String end = now.format(FMT);
        List<Map<String, Object>> list = cstLifeUsageMapper.sumUsageByDeptInRange(start, end, deniedFlag(scope), useDeptListParam(scope));
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public Map<String, Long> getEquipCountByType(UseDeptScope scope) {
        List<Map<String, Object>> list = cstLifeEquipMapper.countByDeptAndType(deniedFlag(scope), useDeptListParam(scope));
        Map<String, Long> result = new java.util.HashMap<>();
        result.put("1", 0L);
        result.put("2", 0L);
        result.put("3", 0L);
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
    public List<Map<String, Object>> getUsageTrendByType(String equipType, String groupBy, UseDeptScope scope) {
        if (equipType == null || equipType.isEmpty()) {
            return new ArrayList<>();
        }
        if (scope.isDenied()) {
            return new ArrayList<>();
        }
        if (groupBy == null || groupBy.isEmpty()) {
            groupBy = "day";
        }
        LocalDate now = LocalDate.now();
        String start = now.withDayOfYear(1).format(FMT);
        String end = now.format(FMT);
        List<Map<String, Object>> list = cstLifeUsageMapper.sumUsageTrendByType(start, end, equipType, groupBy, deniedFlag(scope), useDeptListParam(scope));
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public List<Map<String, Object>> getUsageDeptRankByType(String equipType, String groupBy, UseDeptScope scope) {
        if (equipType == null || equipType.isEmpty()) {
            return new ArrayList<>();
        }
        if (scope.isDenied()) {
            return new ArrayList<>();
        }
        if (groupBy == null || groupBy.isEmpty()) {
            groupBy = "day";
        }
        LocalDate now = LocalDate.now();
        String start = now.withDayOfYear(1).format(FMT);
        String end = now.format(FMT);
        List<Map<String, Object>> list = cstLifeUsageMapper.sumUsageDeptRankByType(start, end, equipType, groupBy, deniedFlag(scope), useDeptListParam(scope));
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
                return Double.compare(valB, valA);
            });
            result.addAll(periodList.subList(0, Math.min(5, periodList.size())));
        }
        return result;
    }
}
