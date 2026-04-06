package com.ruoyi.custom.service.impl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.custom.domain.CstLifeUsage;
import com.ruoyi.custom.mapper.CstLifeUsageMapper;
import com.ruoyi.custom.service.ICstLifeUsageService;
import com.ruoyi.custom.service.ICstReportUsageHistService;

/**
 * 通用设备日使用上报 服务层实现
 *
 * @author ruoyi
 */
@Service
public class CstLifeUsageServiceImpl implements ICstLifeUsageService {

    private final CstLifeUsageMapper cstLifeUsageMapper;
    private final ICstReportUsageHistService reportUsageHistService;

    @Autowired
    public CstLifeUsageServiceImpl(CstLifeUsageMapper cstLifeUsageMapper,
        ICstReportUsageHistService reportUsageHistService) {
        this.cstLifeUsageMapper = cstLifeUsageMapper;
        this.reportUsageHistService = reportUsageHistService;
    }

    @Override
    public List<CstLifeUsage> selectCstLifeUsageList(CstLifeUsage row) {
        return cstLifeUsageMapper.selectCstLifeUsageList(row);
    }

    @Override
    public CstLifeUsage selectCstLifeUsageById(Long id) {
        return cstLifeUsageMapper.selectCstLifeUsageById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveCstLifeUsage(CstLifeUsage row) {
        if (row.getId() != null) {
            CstLifeUsage old = cstLifeUsageMapper.selectCstLifeUsageById(row.getId());
            reportUsageHistService.tryRecordLifeUpdate(old, row);
            row.setUpdateBy(SecurityUtils.getUsername());
            return cstLifeUsageMapper.updateCstLifeUsage(row);
        }
        String statDateStr = row.getStatDate() != null
            ? new SimpleDateFormat("yyyy-MM-dd").format(row.getStatDate()) : "";
        CstLifeUsage exist = cstLifeUsageMapper.selectCstLifeUsageByUnique(statDateStr,
            row.getEquipType(), row.getUseDept() != null ? row.getUseDept() : "");
        if (exist != null) {
            reportUsageHistService.tryRecordLifeUpdate(exist, row);
            row.setId(exist.getId());
            row.setUpdateBy(SecurityUtils.getUsername());
            return cstLifeUsageMapper.updateCstLifeUsage(row);
        }
        row.setCreateBy(SecurityUtils.getUsername());
        return cstLifeUsageMapper.insertCstLifeUsage(row);
    }

    @Override
    public int deleteCstLifeUsageByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        return cstLifeUsageMapper.deleteCstLifeUsageByIds(ids);
    }

    @Override
    public List<Map<String, Object>> sumUsageByRange(String useDept, String equipType, String beginTime,
        String endTime, String groupBy) {
        Map<String, Object> params = new HashMap<>();
        params.put("useDept", useDept);
        params.put("equipType", equipType);
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        params.put("groupBy", groupBy != null ? groupBy : "day");
        return sumUsageByRange(params);
    }

    @Override
    public List<Map<String, Object>> sumUsageByRange(Map<String, Object> params) {
        if (params.get("groupBy") == null || "".equals(params.get("groupBy"))) {
            params.put("groupBy", "day");
        }
        return cstLifeUsageMapper.sumUsageByRange(params);
    }
}
