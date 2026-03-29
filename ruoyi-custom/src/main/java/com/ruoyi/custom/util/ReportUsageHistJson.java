package com.ruoyi.custom.util;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.custom.domain.CstKeyEquipUsage;
import com.ruoyi.custom.domain.CstLifeUsage;

/**
 * 上报数据快照 JSON（用于变更对比与落库）
 */
public final class ReportUsageHistJson {

    private static final ObjectMapper M = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private ReportUsageHistJson() {
    }

    public static String lifeSnapshot(CstLifeUsage u) throws Exception {
        if (u == null) {
            return null;
        }
        SnapLife s = new SnapLife();
        s.id = u.getId();
        s.statDate = u.getStatDate() != null ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(u.getStatDate()) : null;
        s.equipType = u.getEquipType();
        s.useDept = u.getUseDept();
        s.usedCount = u.getUsedCount();
        s.remark = u.getRemark();
        s.reportBy = u.getCreateBy();
        return M.writeValueAsString(s);
    }

    public static String keySnapshot(CstKeyEquipUsage u) throws Exception {
        if (u == null) {
            return null;
        }
        SnapKey s = new SnapKey();
        s.id = u.getId();
        s.equipId = u.getEquipId();
        s.reportDate = u.getReportDate();
        s.equipNo = u.getEquipNo();
        s.equipDesc = u.getEquipDesc();
        s.useDept = u.getUseDept();
        s.treatCount = u.getTreatCount();
        s.workHours = u.getWorkHours();
        s.weekWorkDays = u.getWeekWorkDays();
        s.unitChargePrice = u.getUnitChargePrice();
        s.remark = u.getRemark();
        s.reportBy = u.getCreateBy();
        return M.writeValueAsString(s);
    }

    /** 合并 update 请求体到库中旧实体，得到即将落库的业务形态（与 update SQL 一致） */
    public static CstLifeUsage mergedLifeForUpdate(CstLifeUsage old, CstLifeUsage row) {
        CstLifeUsage a = new CstLifeUsage();
        a.setId(old.getId());
        a.setStatDate(old.getStatDate());
        a.setEquipType(old.getEquipType());
        a.setUseDept(old.getUseDept());
        a.setUsedCount(row.getUsedCount() != null ? row.getUsedCount() : old.getUsedCount());
        a.setRemark(row.getRemark() != null ? row.getRemark() : old.getRemark());
        a.setCreateBy(old.getCreateBy());
        return a;
    }

    public static CstKeyEquipUsage mergedKeyForUpdate(CstKeyEquipUsage old, CstKeyEquipUsage row) {
        CstKeyEquipUsage a = new CstKeyEquipUsage();
        a.setId(old.getId());
        a.setEquipId(old.getEquipId());
        a.setReportDate(old.getReportDate());
        a.setEquipNo(old.getEquipNo());
        a.setEquipDesc(old.getEquipDesc());
        a.setUseDept(old.getUseDept());
        a.setTreatCount(row.getTreatCount() != null ? row.getTreatCount() : old.getTreatCount());
        a.setWorkHours(row.getWorkHours() != null ? row.getWorkHours() : old.getWorkHours());
        a.setWeekWorkDays(row.getWeekWorkDays() != null ? row.getWeekWorkDays() : old.getWeekWorkDays());
        a.setUnitChargePrice(row.getUnitChargePrice() != null ? row.getUnitChargePrice() : old.getUnitChargePrice());
        a.setRemark(row.getRemark() != null ? row.getRemark() : old.getRemark());
        a.setCreateBy(old.getCreateBy());
        return a;
    }

    public static boolean lifeJsonEquals(String a, String b) {
        return Objects.equals(a, b);
    }

    private static class SnapLife {
        public Long id;
        public String statDate;
        public String equipType;
        public String useDept;
        public Integer usedCount;
        public String remark;
        /** 上报人登录名（与上报列表「上报人」对应） */
        public String reportBy;
    }

    private static class SnapKey {
        public Long id;
        public Long equipId;
        public String reportDate;
        public String equipNo;
        public String equipDesc;
        public String useDept;
        public Integer treatCount;
        public java.math.BigDecimal workHours;
        public Integer weekWorkDays;
        public java.math.BigDecimal unitChargePrice;
        public String remark;
        public String reportBy;
    }
}
