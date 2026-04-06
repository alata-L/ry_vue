package com.ruoyi.web.controller.custom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.custom.domain.CstLifeUsage;

/**
 * 通用设备使用上报 Excel 导出行（独立于 ruoyi-custom 实体上的 @Excel，避免本地依赖 jar 版本不一致）
 */
public class CstLifeUsageExport {

    @Excel(name = "上报日期", dateFormat = "yyyy-MM-dd", sort = 1)
    private Date statDate;

    @Excel(name = "上报人", sort = 2)
    private String createByNickName;

    @Excel(name = "设备类型", readConverterExp = "1=监护仪,2=输液泵,3=注射泵", sort = 3)
    private String equipType;

    @Excel(name = "使用科室", dictType = "cst_use_dept", sort = 4)
    private String useDept;

    @Excel(name = "装配台数", sort = 5)
    private Integer equipInstallCount;

    @Excel(name = "当日使用台数", sort = 6)
    private Integer usedCount;

    public Date getStatDate() {
        return statDate;
    }

    public void setStatDate(Date statDate) {
        this.statDate = statDate;
    }

    public String getCreateByNickName() {
        return createByNickName;
    }

    public void setCreateByNickName(String createByNickName) {
        this.createByNickName = createByNickName;
    }

    public String getEquipType() {
        return equipType;
    }

    public void setEquipType(String equipType) {
        this.equipType = equipType;
    }

    public String getUseDept() {
        return useDept;
    }

    public void setUseDept(String useDept) {
        this.useDept = useDept;
    }

    public Integer getEquipInstallCount() {
        return equipInstallCount;
    }

    public void setEquipInstallCount(Integer equipInstallCount) {
        this.equipInstallCount = equipInstallCount;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }

    static List<CstLifeUsageExport> fromFilledRows(List<CstLifeUsage> list) {
        List<CstLifeUsageExport> out = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return out;
        }
        for (CstLifeUsage u : list) {
            CstLifeUsageExport r = new CstLifeUsageExport();
            r.setStatDate(u.getStatDate());
            r.setEquipType(u.getEquipType());
            r.setUseDept(u.getUseDept());
            r.setUsedCount(u.getUsedCount());
            if (u.getParams() != null) {
                Object nick = u.getParams().get(CreateByNickNameHelper.PARAM_CREATE_BY_NICK_NAME);
                r.setCreateByNickName(nick != null ? String.valueOf(nick)
                    : (StringUtils.isNotEmpty(u.getCreateBy()) ? u.getCreateBy() : ""));
                Object ec = u.getParams().get("equipInstallCount");
                if (ec instanceof Integer) {
                    r.setEquipInstallCount((Integer) ec);
                } else if (ec != null) {
                    try {
                        r.setEquipInstallCount(Integer.parseInt(String.valueOf(ec)));
                    } catch (NumberFormatException e) {
                        r.setEquipInstallCount(0);
                    }
                } else {
                    r.setEquipInstallCount(0);
                }
            } else {
                r.setCreateByNickName(StringUtils.isNotEmpty(u.getCreateBy()) ? u.getCreateBy() : "");
                r.setEquipInstallCount(0);
            }
            out.add(r);
        }
        return out;
    }
}
