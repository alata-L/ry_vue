package com.ruoyi.custom.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.custom.domain.CstLifeUsage;

/**
 * 通用设备日使用上报 数据层
 *
 * @author ruoyi
 */
public interface CstLifeUsageMapper {

    List<CstLifeUsage> selectCstLifeUsageList(CstLifeUsage row);

    CstLifeUsage selectCstLifeUsageById(Long id);

    CstLifeUsage selectCstLifeUsageByUnique(@Param("statDate") String statDate, @Param("equipType") String equipType, @Param("useDept") String useDept);

    int insertCstLifeUsage(CstLifeUsage row);

    int updateCstLifeUsage(CstLifeUsage row);

    int deleteCstLifeUsageById(Long id);

    int deleteCstLifeUsageByIds(Long[] ids);

    /** 按日/月/年汇总使用数据：params: useDept(可选), equipType(可选), beginTime, endTime, groupBy(day|month|year) */
    List<Map<String, Object>> sumUsageByRange(Map<String, Object> params);

    /** 指定日期范围内按月份+设备类型汇总使用台数，返回 statPeriod(yyyy-MM)/equipType/totalCount */
    List<Map<String, Object>> sumUsageByMonthAndType(@Param("start") String start, @Param("end") String end);

    /** 指定日期范围内按科室汇总使用台数，返回 useDept/totalCount */
    List<Map<String, Object>> sumUsageByDeptInRange(@Param("start") String start, @Param("end") String end);

    /** 全院使用趋势（按日、月、年，按设备类型，日均数据），返回 statPeriod/equipType/avgDailyCount */
    List<Map<String, Object>> sumUsageTrendByType(@Param("start") String start, @Param("end") String end, @Param("equipType") String equipType, @Param("groupBy") String groupBy);

    /** 科室排名TOP5（按日、月、年，按设备类型，日均数据），返回 statPeriod/useDept/avgDailyCount */
    List<Map<String, Object>> sumUsageDeptRankByType(@Param("start") String start, @Param("end") String end, @Param("equipType") String equipType, @Param("groupBy") String groupBy);
}
