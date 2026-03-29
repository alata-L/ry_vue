package com.ruoyi.custom.service;

import java.util.List;
import com.ruoyi.custom.domain.CstKeyEquipUsage;
import com.ruoyi.custom.domain.CstLifeUsage;
import com.ruoyi.custom.domain.CstReportUsageHist;

/**
 * 设备上报变更历史
 */
public interface ICstReportUsageHistService {

    /**
     * 通用设备：若有业务字段变化则写入历史（与 update 同事务调用）
     */
    void tryRecordLifeUpdate(CstLifeUsage oldRow, CstLifeUsage newFromRequest);

    /**
     * 重点设备：若有业务字段变化则写入历史
     */
    void tryRecordKeyUpdate(CstKeyEquipUsage oldRow, CstKeyEquipUsage newFromRequest);

    List<CstReportUsageHist> selectCstReportUsageHistList(CstReportUsageHist query);
}
