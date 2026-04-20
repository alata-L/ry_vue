package com.ruoyi.web.controller.custom;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;

/**
 * 重点设备上报 Excel 导入行（模板列，与导出主数据列一致，不含上报人）
 */
public class CstKeyEquipUsageImportRow {

    @Excel(name = "上报日期", dateFormat = "yyyy-MM-dd", sort = 1)
    private String reportDate;

    @Excel(name = "设备编号", sort = 2)
    private String equipNo;

    @Excel(name = "工作时间", sort = 3)
    private BigDecimal workHours;

    @Excel(name = "每周工作天数", sort = 4)
    private Integer weekWorkDays;

    @Excel(name = "日均服务例数", sort = 5)
    private Integer treatCount;

    @Excel(name = "收费价格", sort = 6)
    private BigDecimal unitChargePrice;

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getEquipNo() {
        return equipNo;
    }

    public void setEquipNo(String equipNo) {
        this.equipNo = equipNo;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    public Integer getWeekWorkDays() {
        return weekWorkDays;
    }

    public void setWeekWorkDays(Integer weekWorkDays) {
        this.weekWorkDays = weekWorkDays;
    }

    public Integer getTreatCount() {
        return treatCount;
    }

    public void setTreatCount(Integer treatCount) {
        this.treatCount = treatCount;
    }

    public BigDecimal getUnitChargePrice() {
        return unitChargePrice;
    }

    public void setUnitChargePrice(BigDecimal unitChargePrice) {
        this.unitChargePrice = unitChargePrice;
    }
}
