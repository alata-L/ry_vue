package com.ruoyi.custom.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.custom.domain.CstKeyEquip;

/**
 * 重点设备台账 数据层
 *
 * @author ruoyi
 */
public interface CstKeyEquipMapper {

    List<CstKeyEquip> selectCstKeyEquipList(CstKeyEquip row);

    CstKeyEquip selectCstKeyEquipById(Long id);

    int insertCstKeyEquip(CstKeyEquip row);

    int updateCstKeyEquip(CstKeyEquip row);

    int deleteCstKeyEquipById(Long id);

    int deleteCstKeyEquipByIds(Long[] ids);

    /** 全院汇总：设备数量、设备价值（status=0） */
    Map<String, Object> selectSummary();

    /** 按科室汇总：科室、设备数量、设备价值 */
    List<Map<String, Object>> selectDeptEquipStats();
}
