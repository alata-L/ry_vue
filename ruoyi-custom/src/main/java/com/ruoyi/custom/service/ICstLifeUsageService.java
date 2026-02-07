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

    int saveCstLifeUsage(CstLifeUsage row);

    int deleteCstLifeUsageByIds(Long[] ids);

    /** 按时间范围汇总使用数据，groupBy: day|month|year */
    List<Map<String, Object>> sumUsageByRange(String useDept, String equipType, String beginTime, String endTime, String groupBy);
}
