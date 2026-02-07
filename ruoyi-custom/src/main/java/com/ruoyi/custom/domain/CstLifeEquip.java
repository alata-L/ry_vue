package com.ruoyi.custom.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 通用设备台账 cst_life_equip
 *
 * @author ruoyi
 */
public class CstLifeEquip extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;
    /** 设备编号 */
    @Excel(name = "设备编号")
    private String equipNo;
    /** 子资产编号 */
    private String subAssetNo;
    /** 设备描述 */
    @Excel(name = "设备描述")
    private String equipDesc;
    /** 型号 */
    @Excel(name = "型号")
    private String model;
    /** 累计购置价值 */
    @Excel(name = "累计购置价值")
    private BigDecimal totalValue;
    /** 资本化日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "资本化日期", dateFormat = "yyyy-MM-dd")
    private Date capDate;
    /** 使用科室(词典cst_use_dept) */
    @Excel(name = "使用科室", dictType = "cst_use_dept")
    private String useDept;
    /** 设备类型 1监护仪 2输液泵 3注射泵 */
    @Excel(name = "设备类型", readConverterExp = "1=监护仪,2=输液泵,3=注射泵")
    private String equipType;
    /** 状态 0正常 1停用 */
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEquipNo() { return equipNo; }
    public void setEquipNo(String equipNo) { this.equipNo = equipNo; }
    public String getSubAssetNo() { return subAssetNo; }
    public void setSubAssetNo(String subAssetNo) { this.subAssetNo = subAssetNo; }
    public String getEquipDesc() { return equipDesc; }
    public void setEquipDesc(String equipDesc) { this.equipDesc = equipDesc; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public Date getCapDate() { return capDate; }
    public void setCapDate(Date capDate) { this.capDate = capDate; }
    public String getUseDept() { return useDept; }
    public void setUseDept(String useDept) { this.useDept = useDept; }
    public String getEquipType() { return equipType; }
    public void setEquipType(String equipType) { this.equipType = equipType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
