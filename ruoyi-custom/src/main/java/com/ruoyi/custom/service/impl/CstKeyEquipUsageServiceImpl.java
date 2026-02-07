package com.ruoyi.custom.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.custom.domain.CstKeyEquipUsage;
import com.ruoyi.custom.mapper.CstKeyEquipUsageMapper;
import com.ruoyi.custom.service.ICstKeyEquipUsageService;

/**
 * 重点设备月度使用上报 服务层实现
 *
 * @author ruoyi
 */
@Service
public class CstKeyEquipUsageServiceImpl implements ICstKeyEquipUsageService {

    @Autowired
    private CstKeyEquipUsageMapper cstKeyEquipUsageMapper;

    @Override
    public List<CstKeyEquipUsage> selectCstKeyEquipUsageList(CstKeyEquipUsage row) {
        return cstKeyEquipUsageMapper.selectCstKeyEquipUsageList(row);
    }

    @Override
    public CstKeyEquipUsage selectCstKeyEquipUsageById(Long id) {
        return cstKeyEquipUsageMapper.selectCstKeyEquipUsageById(id);
    }

    @Override
    public int saveCstKeyEquipUsage(CstKeyEquipUsage row) {
        if (row.getId() != null) {
            row.setUpdateBy(SecurityUtils.getUsername());
            return cstKeyEquipUsageMapper.updateCstKeyEquipUsage(row);
        }
        CstKeyEquipUsage exist = cstKeyEquipUsageMapper.selectByEquipIdAndReportDate(row.getEquipId(), row.getReportDate());
        if (exist != null) {
            row.setId(exist.getId());
            row.setUpdateBy(SecurityUtils.getUsername());
            return cstKeyEquipUsageMapper.updateCstKeyEquipUsage(row);
        }
        row.setCreateBy(SecurityUtils.getUsername());
        if (row.getTreatCount() == null) {
            row.setTreatCount(1);
        }
        return cstKeyEquipUsageMapper.insertCstKeyEquipUsage(row);
    }

    @Override
    public int deleteCstKeyEquipUsageByIds(Long[] ids) {
        if (ids == null || ids.length == 0) return 0;
        return cstKeyEquipUsageMapper.deleteCstKeyEquipUsageByIds(ids);
    }
}
