package com.ruoyi.custom.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
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

    @Override
    public String importKeyEquip(List<CstKeyEquip> keyEquipList, Boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(keyEquipList) || keyEquipList.size() == 0) {
            throw new ServiceException("导入设备数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (CstKeyEquip equip : keyEquipList) {
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
                if (StringUtils.isEmpty(equip.getUseDept())) {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、设备编号 " + equip.getEquipNo() + " 的使用科室不能为空");
                    continue;
                }

                // 验证是否存在这个设备（根据设备编号）
                CstKeyEquip existEquip = cstKeyEquipMapper.selectCstKeyEquipByEquipNo(equip.getEquipNo());
                if (StringUtils.isNull(existEquip)) {
                    // 新增
                    equip.setStatus("0");
                    equip.setCreateBy(operName);
                    cstKeyEquipMapper.insertCstKeyEquip(equip);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、设备编号 " + equip.getEquipNo() + " 导入成功");
                } else if (isUpdateSupport) {
                    // 更新
                    equip.setId(existEquip.getId());
                    equip.setUpdateBy(operName);
                    cstKeyEquipMapper.updateCstKeyEquip(equip);
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
