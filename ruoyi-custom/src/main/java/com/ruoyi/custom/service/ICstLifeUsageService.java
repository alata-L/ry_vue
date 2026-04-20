package com.ruoyi.custom.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.custom.domain.CstLifeUsage;

/**
 * 通用设备日使用上报 服务层
 *
 * @author ruoyi
 */
public interface ICstLifeUsageService {

    List<CstLifeUsage> selectCstLifeUsageList(CstLifeUsage row);

    CstLifeUsage selectCstLifeUsageById(Long id);

    /** 按 上报日期+设备类型+使用科室 唯一键查询（导入/校验用） */
    CstLifeUsage selectCstLifeUsageByUnique(String statDate, String equipType, String useDept);

    int saveCstLifeUsage(CstLifeUsage row);

    int deleteCstLifeUsageByIds(Long[] ids);

    /** 按时间范围汇总使用数据，groupBy: day|month|year */
    List<Map<String, Object>> sumUsageByRange(String useDept, String equipType, String beginTime, String endTime, String groupBy);

    /** 与 Mapper 一致：params 含 useDept、equipType、beginTime、endTime、groupBy，及 common 科室范围键 */
    List<Map<String, Object>> sumUsageByRange(Map<String, Object> params);
}
