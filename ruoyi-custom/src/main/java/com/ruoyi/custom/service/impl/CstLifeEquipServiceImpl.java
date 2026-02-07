package com.ruoyi.custom.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.custom.domain.CstLifeEquip;
import com.ruoyi.custom.mapper.CstLifeEquipMapper;
import com.ruoyi.custom.service.ICstLifeEquipService;

/**
 * 通用设备台账 服务层实现
 *
 * @author ruoyi
 */
@Service
public class CstLifeEquipServiceImpl implements ICstLifeEquipService {

    @Autowired
    private CstLifeEquipMapper cstLifeEquipMapper;

    @Override
    public List<CstLifeEquip> selectCstLifeEquipList(CstLifeEquip row) {
        return cstLifeEquipMapper.selectCstLifeEquipList(row);
    }

    @Override
    public CstLifeEquip selectCstLifeEquipById(Long id) {
        return cstLifeEquipMapper.selectCstLifeEquipById(id);
    }

    @Override
    public int insertCstLifeEquip(CstLifeEquip row) {
        row.setCreateBy(SecurityUtils.getUsername());
        return cstLifeEquipMapper.insertCstLifeEquip(row);
    }

    @Override
    public int updateCstLifeEquip(CstLifeEquip row) {
        row.setUpdateBy(SecurityUtils.getUsername());
        return cstLifeEquipMapper.updateCstLifeEquip(row);
    }

    @Override
    public int deleteCstLifeEquipByIds(Long[] ids) {
        if (ids == null || ids.length == 0) return 0;
        return cstLifeEquipMapper.deleteCstLifeEquipByIds(ids);
    }

    @Override
    public List<Map<String, Object>> countByDeptAndType() {
        return cstLifeEquipMapper.countByDeptAndType();
    }

    @Override
    public List<Map<String, Object>> countByYearsAndDept(int minYears) {
        return cstLifeEquipMapper.countByYearsAndDept(minYears);
    }
}
