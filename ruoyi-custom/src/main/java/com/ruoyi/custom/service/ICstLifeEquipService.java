package com.ruoyi.custom.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.custom.domain.CstLifeEquip;
import com.ruoyi.custom.domain.UseDeptScope;

/**
 * 通用设备台账 服务层
 *
 * @author ruoyi
 */
public interface ICstLifeEquipService {

    List<CstLifeEquip> selectCstLifeEquipList(CstLifeEquip row);

    CstLifeEquip selectCstLifeEquipById(Long id);

    int insertCstLifeEquip(CstLifeEquip row);

    int updateCstLifeEquip(CstLifeEquip row);

    int deleteCstLifeEquipByIds(Long[] ids);

    /** 按科室统计各类型设备台数 */
    List<Map<String, Object>> countByDeptAndType(UseDeptScope scope);

    /** 按使用年限统计（minYears=6/10/15） */
    List<Map<String, Object>> countByYearsAndDept(int minYears, UseDeptScope scope);

    /** 按使用年限和设备类型统计（minYears=6/10/15） */
    List<Map<String, Object>> countByYearsAndDeptAndType(int minYears, UseDeptScope scope);

    /** 导入通用设备数据 */
    String importLifeEquip(List<CstLifeEquip> lifeEquipList, Boolean isUpdateSupport, String operName);
}
