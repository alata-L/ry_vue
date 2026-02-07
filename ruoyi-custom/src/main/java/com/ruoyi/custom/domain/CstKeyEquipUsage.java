package com.ruoyi.custom.domain;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 重点设备月度使用上报 cst_key_equip_usage
 *
 * @author ruoyi
 */
public class CstKeyEquipUsage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;
    /** 重点设备ID */
    private Long equipId;
    /** 上报日期 yyyy-MM-dd */
    @Excel(name = "上报日期", dateFormat = "yyyy-MM-dd")
    private String reportDate;
    /** 日均服务例数 */
    @Excel(name = "日均服务例数")
    private Integer treatCount;
    /** 工作时间 */
    @Excel(name = "工作时间")
    private BigDecimal workHours;
    /** 每周工作天数 */
    @Excel(name = "每周工作天数")
    private Integer weekWorkDays;
    /** 收费价格 */
    @Excel(name = "收费价格")
    private BigDecimal unitChargePrice;

    /** 关联：设备编号等（查询用） */
    private String equipNo;
    /** 关联：设备描述（展示/检索用） */
    private String equipDesc;
    private String useDept;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEquipId() { return equipId; }
    public void setEquipId(Long equipId) { this.equipId = equipId; }
    public String getReportDate() { return reportDate; }
    public void setReportDate(String reportDate) { this.reportDate = reportDate; }
    public Integer getTreatCount() { return treatCount; }
    public void setTreatCount(Integer treatCount) { this.treatCount = treatCount; }
    public BigDecimal getWorkHours() { return workHours; }
    public void setWorkHours(BigDecimal workHours) { this.workHours = workHours; }
    public Integer getWeekWorkDays() { return weekWorkDays; }
    public void setWeekWorkDays(Integer weekWorkDays) { this.weekWorkDays = weekWorkDays; }
    public BigDecimal getUnitChargePrice() { return unitChargePrice; }
    public void setUnitChargePrice(BigDecimal unitChargePrice) { this.unitChargePrice = unitChargePrice; }
    public String getEquipNo() { return equipNo; }
    public void setEquipNo(String equipNo) { this.equipNo = equipNo; }
    public String getEquipDesc() { return equipDesc; }
    public void setEquipDesc(String equipDesc) { this.equipDesc = equipDesc; }
    public String getUseDept() { return useDept; }
    public void setUseDept(String useDept) { this.useDept = useDept; }
}
