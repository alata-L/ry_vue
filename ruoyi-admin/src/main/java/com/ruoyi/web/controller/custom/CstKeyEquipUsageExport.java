package com.ruoyi.web.controller.custom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.custom.domain.CstKeyEquipUsage;

/**
 * 重点设备上报 Excel 导出行
 */
public class CstKeyEquipUsageExport {

    @Excel(name = "上报日期", dateFormat = "yyyy-MM-dd", sort = 1)
    private String reportDate;

    @Excel(name = "上报人", sort = 2)
    private String createByNickName;

    @Excel(name = "设备编号", sort = 3)
    private String equipNo;

    @Excel(name = "设备描述", sort = 4)
    private String equipDesc;

    @Excel(name = "使用科室", dictType = "cst_use_dept", sort = 5)
    private String useDept;

    @Excel(name = "工作时间", sort = 6)
    private BigDecimal workHours;

    @Excel(name = "每周工作天数", sort = 7)
    private Integer weekWorkDays;

    @Excel(name = "日均服务例数", sort = 8)
    private Integer treatCount;

    @Excel(name = "收费价格", sort = 9)
    private BigDecimal unitChargePrice;

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getCreateByNickName() {
        return createByNickName;
    }

    public void setCreateByNickName(String createByNickName) {
        this.createByNickName = createByNickName;
    }

    public String getEquipNo() {
        return equipNo;
    }

    public void setEquipNo(String equipNo) {
        this.equipNo = equipNo;
    }

    public String getEquipDesc() {
        return equipDesc;
    }

    public void setEquipDesc(String equipDesc) {
        this.equipDesc = equipDesc;
    }

    public String getUseDept() {
        return useDept;
    }

    public void setUseDept(String useDept) {
        this.useDept = useDept;
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

    static List<CstKeyEquipUsageExport> fromFilledRows(List<CstKeyEquipUsage> list) {
        List<CstKeyEquipUsageExport> out = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return out;
        }
        for (CstKeyEquipUsage u : list) {
            CstKeyEquipUsageExport r = new CstKeyEquipUsageExport();
            r.setReportDate(u.getReportDate());
            r.setEquipNo(u.getEquipNo());
            r.setEquipDesc(u.getEquipDesc());
            r.setUseDept(u.getUseDept());
            r.setWorkHours(u.getWorkHours());
            r.setWeekWorkDays(u.getWeekWorkDays());
            r.setTreatCount(u.getTreatCount());
            r.setUnitChargePrice(u.getUnitChargePrice());
            if (u.getParams() != null) {
                Object nick = u.getParams().get(CreateByNickNameHelper.PARAM_CREATE_BY_NICK_NAME);
                r.setCreateByNickName(nick != null ? String.valueOf(nick)
                    : (StringUtils.isNotEmpty(u.getCreateBy()) ? u.getCreateBy() : ""));
            } else {
                r.setCreateByNickName(StringUtils.isNotEmpty(u.getCreateBy()) ? u.getCreateBy() : "");
            }
            out.add(r);
        }
        return out;
    }
}
