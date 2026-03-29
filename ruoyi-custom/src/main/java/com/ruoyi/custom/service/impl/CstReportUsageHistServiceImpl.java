package com.ruoyi.custom.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.custom.domain.CstKeyEquipUsage;
import com.ruoyi.custom.domain.CstLifeUsage;
import com.ruoyi.custom.domain.CstReportUsageHist;
import com.ruoyi.custom.mapper.CstReportUsageHistMapper;
import com.ruoyi.custom.service.ICstReportUsageHistService;
import com.ruoyi.custom.util.ReportUsageHistJson;

/**
 * 设备上报变更历史
 */
@Service
public class CstReportUsageHistServiceImpl implements ICstReportUsageHistService {

    @Autowired
    private CstReportUsageHistMapper cstReportUsageHistMapper;

    @Override
    public void tryRecordLifeUpdate(CstLifeUsage oldRow, CstLifeUsage newFromRequest) {
        if (oldRow == null || oldRow.getId() == null) {
            return;
        }
        try {
            CstLifeUsage after = ReportUsageHistJson.mergedLifeForUpdate(oldRow, newFromRequest);
            String before = ReportUsageHistJson.lifeSnapshot(oldRow);
            String afterJ = ReportUsageHistJson.lifeSnapshot(after);
            if (ReportUsageHistJson.lifeJsonEquals(before, afterJ)) {
                return;
            }
            insertHist(CstReportUsageHist.BIZ_LIFE, oldRow.getId(), CstReportUsageHist.OPER_UPDATE, before, afterJ);
        } catch (Exception e) {
            throw new ServiceException("记录通用设备变更历史失败：" + e.getMessage());
        }
    }

    @Override
    public void tryRecordKeyUpdate(CstKeyEquipUsage oldRow, CstKeyEquipUsage newFromRequest) {
        if (oldRow == null || oldRow.getId() == null) {
            return;
        }
        try {
            CstKeyEquipUsage after = ReportUsageHistJson.mergedKeyForUpdate(oldRow, newFromRequest);
            String before = ReportUsageHistJson.keySnapshot(oldRow);
            String afterJ = ReportUsageHistJson.keySnapshot(after);
            if (before != null && before.equals(afterJ)) {
                return;
            }
            insertHist(CstReportUsageHist.BIZ_KEY, oldRow.getId(), CstReportUsageHist.OPER_UPDATE, before, afterJ);
        } catch (Exception e) {
            throw new ServiceException("记录重点设备变更历史失败：" + e.getMessage());
        }
    }

    private void insertHist(String bizType, Long usageId, String operType, String beforeJson, String afterJson) {
        CstReportUsageHist h = new CstReportUsageHist();
        h.setBizType(bizType);
        h.setUsageId(usageId);
        h.setOperType(operType);
        h.setChangeBy(SecurityUtils.getUsername());
        h.setChangeByNick("");
        h.setBeforeJson(beforeJson);
        h.setAfterJson(afterJson);
        cstReportUsageHistMapper.insertCstReportUsageHist(h);
    }

    @Override
    public List<CstReportUsageHist> selectCstReportUsageHistList(CstReportUsageHist query) {
        return cstReportUsageHistMapper.selectCstReportUsageHistList(query);
    }
}
