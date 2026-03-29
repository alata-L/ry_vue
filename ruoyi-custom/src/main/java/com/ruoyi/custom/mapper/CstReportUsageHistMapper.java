package com.ruoyi.custom.mapper;

import java.util.List;
import com.ruoyi.custom.domain.CstReportUsageHist;

/**
 * 设备上报变更历史
 */
public interface CstReportUsageHistMapper {

    int insertCstReportUsageHist(CstReportUsageHist row);

    List<CstReportUsageHist> selectCstReportUsageHistList(CstReportUsageHist query);
}
