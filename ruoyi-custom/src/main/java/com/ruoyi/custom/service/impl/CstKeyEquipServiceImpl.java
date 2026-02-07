package com.ruoyi.custom.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.custom.domain.CstKeyEquip;
import com.ruoyi.custom.mapper.CstKeyEquipMapper;
import com.ruoyi.custom.service.ICstKeyEquipService;

/**
 * 重点设备台账 服务层实现
 *
 * @author ruoyi
 */
@Service
public class CstKeyEquipServiceImpl implements ICstKeyEquipService {

    @Autowired
    private CstKeyEquipMapper cstKeyEquipMapper;

    @Override
    public List<CstKeyEquip> selectCstKeyEquipList(CstKeyEquip row) {
        return cstKeyEquipMapper.selectCstKeyEquipList(row);
    }

    @Override
    public CstKeyEquip selectCstKeyEquipById(Long id) {
        return cstKeyEquipMapper.selectCstKeyEquipById(id);
    }

    @Override
    public int insertCstKeyEquip(CstKeyEquip row) {
        row.setCreateBy(SecurityUtils.getUsername());
        return cstKeyEquipMapper.insertCstKeyEquip(row);
    }

    @Override
    public int updateCstKeyEquip(CstKeyEquip row) {
        row.setUpdateBy(SecurityUtils.getUsername());
        return cstKeyEquipMapper.updateCstKeyEquip(row);
    }

    @Override
    public int deleteCstKeyEquipByIds(Long[] ids) {
        if (ids == null || ids.length == 0) return 0;
        return cstKeyEquipMapper.deleteCstKeyEquipByIds(ids);
    }
}
