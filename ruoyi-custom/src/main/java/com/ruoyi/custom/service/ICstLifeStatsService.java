package com.ruoyi.custom.service;

import java.util.List;
import java.util.Map;

/**
 * 生命支持类设备统计 服务层（首页概览）
 *
 * @author ruoyi
 */
public interface ICstLifeStatsService {

    /**
     * 首页：按设备类型使用趋势（今年按月），返回 statPeriod(yyyy-MM)/equipType/totalCount
     */
    List<Map<String, Object>> getUsageTrend();

    /**
     * 首页：科室使用台数排名（今年），返回 useDept/totalCount
     */
    List<Map<String, Object>> getUsageByDept();
}
