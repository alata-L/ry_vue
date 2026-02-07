package com.ruoyi.custom.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.custom.domain.CstLifeEquip;

/**
 * 通用设备台账 数据层
 *
 * @author ruoyi
 */
public interface CstLifeEquipMapper {

    List<CstLifeEquip> selectCstLifeEquipList(CstLifeEquip row);

    CstLifeEquip selectCstLifeEquipById(Long id);

    /** 按科室统计各类型设备台数 */
    List<Map<String, Object>> countByDeptAndType();

    /** 按使用年限统计设备数（全院/科室） */
    List<Map<String, Object>> countByYearsAndDept(int minYears);

    int insertCstLifeEquip(CstLifeEquip row);

    int updateCstLifeEquip(CstLifeEquip row);

    int deleteCstLifeEquipById(Long id);

    int deleteCstLifeEquipByIds(Long[] ids);
}
