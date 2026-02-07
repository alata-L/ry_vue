package com.ruoyi.custom.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.custom.mapper.CstLifeUsageMapper;
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
}
