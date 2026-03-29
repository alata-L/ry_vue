package com.ruoyi.custom.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备上报变更历史 cst_report_usage_hist（biz_type: LIFE / KEY）
 */
public class CstReportUsageHist extends BaseEntity {

    public static final String BIZ_LIFE = "LIFE";
    public static final String BIZ_KEY = "KEY";

    public static final String OPER_UPDATE = "UPDATE";

    private static final long serialVersionUID = 1L;

    private Long id;
    private String bizType;
    private Long usageId;
    private String operType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date changeTime;
    private String changeBy;
    /** 列表关联 sys_user 展示用 */
    private String changeByNick;
    private String beforeJson;
    private String afterJson;

    /** 查询条件：上报日期起止（对应快照 JSON 中 statDate / reportDate，非表字段） */
    private String reportBegin;
    private String reportEnd;

    /** 查询：LIFE 设备类型、使用科室（与快照 JSON 字段一致） */
    private String equipType;
    private String useDept;

    /** 查询：KEY 设备描述模糊（与重点上报检索一致） */
    private String equipDesc;

    /** 查询：变更时间起止（放 BaseEntity.params） */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public Long getUsageId() { return usageId; }
    public void setUsageId(Long usageId) { this.usageId = usageId; }
    public String getOperType() { return operType; }
    public void setOperType(String operType) { this.operType = operType; }
    public Date getChangeTime() { return changeTime; }
    public void setChangeTime(Date changeTime) { this.changeTime = changeTime; }
    public String getChangeBy() { return changeBy; }
    public void setChangeBy(String changeBy) { this.changeBy = changeBy; }
    public String getChangeByNick() { return changeByNick; }
    public void setChangeByNick(String changeByNick) { this.changeByNick = changeByNick; }
    public String getBeforeJson() { return beforeJson; }
    public void setBeforeJson(String beforeJson) { this.beforeJson = beforeJson; }
    public String getAfterJson() { return afterJson; }
    public void setAfterJson(String afterJson) { this.afterJson = afterJson; }
    public String getReportBegin() { return reportBegin; }
    public void setReportBegin(String reportBegin) { this.reportBegin = reportBegin; }
    public String getReportEnd() { return reportEnd; }
    public void setReportEnd(String reportEnd) { this.reportEnd = reportEnd; }
    public String getEquipType() { return equipType; }
    public void setEquipType(String equipType) { this.equipType = equipType; }
    public String getUseDept() { return useDept; }
    public void setUseDept(String useDept) { this.useDept = useDept; }
    public String getEquipDesc() { return equipDesc; }
    public void setEquipDesc(String equipDesc) { this.equipDesc = equipDesc; }
}
