package com.ruoyi.custom.service;

import java.util.List;
import com.ruoyi.custom.domain.CstKeyEquip;

/**
 * 重点设备台账 服务层
 *
 * @author ruoyi
 */
public interface ICstKeyEquipService {

    List<CstKeyEquip> selectCstKeyEquipList(CstKeyEquip row);

    CstKeyEquip selectCstKeyEquipById(Long id);

    int insertCstKeyEquip(CstKeyEquip row);

    int updateCstKeyEquip(CstKeyEquip row);

    int deleteCstKeyEquipByIds(Long[] ids);
}
