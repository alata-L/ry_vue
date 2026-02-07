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

    /**
     * 首页：获取设备台数统计（按类型），返回 equipType/count
     */
    Map<String, Long> getEquipCountByType();

    /**
     * 首页：全院使用趋势（按日、月、年，按设备类型，日均数据），返回 statPeriod/avgDailyCount
     */
    List<Map<String, Object>> getUsageTrendByType(String equipType, String groupBy);

    /**
     * 首页：科室排名TOP5（按日、月、年，按设备类型，日均数据），返回 statPeriod/useDept/avgDailyCount
     */
    List<Map<String, Object>> getUsageDeptRankByType(String equipType, String groupBy);
}
