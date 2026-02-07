package com.ruoyi.custom.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
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

    @Override
    public String importLifeEquip(List<CstLifeEquip> lifeEquipList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(lifeEquipList) || lifeEquipList.size() == 0) {
            throw new ServiceException("导入设备数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (CstLifeEquip equip : lifeEquipList) {
            try {
                // 验证必填字段
                if (StringUtils.isEmpty(equip.getEquipNo())) {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、设备编号不能为空");
                    continue;
                }
                if (StringUtils.isEmpty(equip.getEquipDesc())) {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、设备编号 " + equip.getEquipNo() + " 的设备描述不能为空");
                    continue;
                }
                if (StringUtils.isEmpty(equip.getEquipType())) {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、设备编号 " + equip.getEquipNo() + " 的设备类型不能为空");
                    continue;
                }
                if (StringUtils.isEmpty(equip.getUseDept())) {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、设备编号 " + equip.getEquipNo() + " 的使用科室不能为空");
                    continue;
                }
                
                // 验证是否存在这个设备（根据设备编号）
                CstLifeEquip existEquip = cstLifeEquipMapper.selectCstLifeEquipByEquipNo(equip.getEquipNo());
                if (StringUtils.isNull(existEquip)) {
                    // 新增
                    equip.setStatus("0");
                    equip.setCreateBy(operName);
                    cstLifeEquipMapper.insertCstLifeEquip(equip);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、设备编号 " + equip.getEquipNo() + " 导入成功");
                } else if (isUpdateSupport) {
                    // 更新
                    equip.setId(existEquip.getId());
                    equip.setUpdateBy(operName);
                    cstLifeEquipMapper.updateCstLifeEquip(equip);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、设备编号 " + equip.getEquipNo() + " 更新成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、设备编号 " + equip.getEquipNo() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、设备编号 " + (equip.getEquipNo() != null ? equip.getEquipNo() : "未知") + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                e.printStackTrace();
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }
}
