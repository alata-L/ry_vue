package com.ruoyi.custom.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 通用设备日使用上报 cst_life_usage
 *
 * @author ruoyi
 */
public class CstLifeUsage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;
    /** 上报日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上报日期", dateFormat = "yyyy-MM-dd")
    private Date statDate;
    /** 设备类型 1监护仪 2输液泵 3注射泵 */
    @Excel(name = "设备类型", readConverterExp = "1=监护仪,2=输液泵,3=注射泵")
    private String equipType;
    /** 使用科室 */
    @Excel(name = "使用科室", dictType = "cst_use_dept")
    private String useDept;
    /** 当日使用台数 */
    @Excel(name = "当日使用台数")
    private Integer usedCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Date getStatDate() { return statDate; }
    public void setStatDate(Date statDate) { this.statDate = statDate; }
    public String getEquipType() { return equipType; }
    public void setEquipType(String equipType) { this.equipType = equipType; }
    public String getUseDept() { return useDept; }
    public void setUseDept(String useDept) { this.useDept = useDept; }
    public Integer getUsedCount() { return usedCount; }
    public void setUsedCount(Integer usedCount) { this.usedCount = usedCount; }
}
