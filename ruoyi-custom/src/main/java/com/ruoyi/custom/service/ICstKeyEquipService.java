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

    /** 按设备编号精确匹配（导入上报用） */
    CstKeyEquip selectCstKeyEquipByEquipNo(String equipNo);

    int insertCstKeyEquip(CstKeyEquip row);

    int updateCstKeyEquip(CstKeyEquip row);

    int deleteCstKeyEquipByIds(Long[] ids);

    /** 导入重点设备数据 */
    String importKeyEquip(List<CstKeyEquip> keyEquipList, Boolean isUpdateSupport, String operName);
}
