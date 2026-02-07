package com.ruoyi.custom.service;

import java.util.List;
import com.ruoyi.custom.domain.CstKeyEquipUsage;

/**
 * 重点设备月度使用上报 服务层
 *
 * @author ruoyi
 */
public interface ICstKeyEquipUsageService {

    List<CstKeyEquipUsage> selectCstKeyEquipUsageList(CstKeyEquipUsage row);

    CstKeyEquipUsage selectCstKeyEquipUsageById(Long id);

    int saveCstKeyEquipUsage(CstKeyEquipUsage row);

    int deleteCstKeyEquipUsageByIds(Long[] ids);
}
