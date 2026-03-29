package com.ruoyi.custom.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.custom.domain.CstKeyEquipUsage;
import com.ruoyi.custom.mapper.CstKeyEquipUsageMapper;
import com.ruoyi.custom.service.ICstKeyEquipUsageService;
import com.ruoyi.custom.service.ICstReportUsageHistService;

/**
 * 重点设备月度使用上报 服务层实现
 *
 * @author ruoyi
 */
@Service
public class CstKeyEquipUsageServiceImpl implements ICstKeyEquipUsageService {

    @Autowired
    private CstKeyEquipUsageMapper cstKeyEquipUsageMapper;

    @Autowired
    private ICstReportUsageHistService reportUsageHistService;

    @Override
    public List<CstKeyEquipUsage> selectCstKeyEquipUsageList(CstKeyEquipUsage row) {
        return cstKeyEquipUsageMapper.selectCstKeyEquipUsageList(row);
    }

    @Override
    public CstKeyEquipUsage selectCstKeyEquipUsageById(Long id) {
        return cstKeyEquipUsageMapper.selectCstKeyEquipUsageById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveCstKeyEquipUsage(CstKeyEquipUsage row) {
        if (row.getId() != null) {
            CstKeyEquipUsage old = cstKeyEquipUsageMapper.selectCstKeyEquipUsageById(row.getId());
            reportUsageHistService.tryRecordKeyUpdate(old, row);
            row.setUpdateBy(SecurityUtils.getUsername());
            return cstKeyEquipUsageMapper.updateCstKeyEquipUsage(row);
        }
        CstKeyEquipUsage exist = cstKeyEquipUsageMapper.selectByEquipIdAndReportDate(row.getEquipId(), row.getReportDate());
        if (exist != null) {
            reportUsageHistService.tryRecordKeyUpdate(exist, row);
            row.setId(exist.getId());
            row.setUpdateBy(SecurityUtils.getUsername());
            return cstKeyEquipUsageMapper.updateCstKeyEquipUsage(row);
        }
        row.setCreateBy(SecurityUtils.getUsername());
        return cstKeyEquipUsageMapper.insertCstKeyEquipUsage(row);
    }

    @Override
    public int deleteCstKeyEquipUsageByIds(Long[] ids) {
        if (ids == null || ids.length == 0) return 0;
        return cstKeyEquipUsageMapper.deleteCstKeyEquipUsageByIds(ids);
    }
}
