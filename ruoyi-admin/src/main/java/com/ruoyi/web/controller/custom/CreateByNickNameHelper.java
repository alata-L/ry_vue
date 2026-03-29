package com.ruoyi.web.controller.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.custom.domain.CstKeyEquipUsage;
import com.ruoyi.custom.domain.CstLifeUsage;
import com.ruoyi.custom.domain.CstReportUsageHist;
import com.ruoyi.custom.service.ICstKeyEquipUsageService;
import com.ruoyi.custom.service.ICstLifeUsageService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 将 createBy（登录名）解析为展示用昵称，写入 {@link BaseEntity#getParams()}，避免依赖 custom 实体是否含 setCreateByNickName（防止运行包中 custom 未同步编译）。
 */
@Component
public class CreateByNickNameHelper {

    /** 与前端约定：列表行 params.createByNickName */
    public static final String PARAM_CREATE_BY_NICK_NAME = "createByNickName";

    /** 修改记录列表：快照上报人昵称，列表行 params.reporterNick（与实体字段解耦，避免 custom 未 install 时编译失败） */
    public static final String PARAM_REPORTER_NICK = "reporterNick";

    /** 旧快照无 useDept 时，从上报主表回填，列表行 params.useDeptDisplay */
    public static final String PARAM_USE_DEPT_DISPLAY = "useDeptDisplay";

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ICstLifeUsageService cstLifeUsageService;

    @Autowired
    private ICstKeyEquipUsageService cstKeyEquipUsageService;

    public void fillLifeUsage(List<CstLifeUsage> rows) {
        fillParams(rows);
    }

    public void fillLifeUsage(CstLifeUsage row) {
        fillParams(row);
    }

    public void fillKeyEquipUsage(List<CstKeyEquipUsage> rows) {
        fillParams(rows);
    }

    public void fillKeyEquipUsage(CstKeyEquipUsage row) {
        fillParams(row);
    }

    /**
     * 修改记录列表：从快照 JSON 的 reportBy（登录名）解析为昵称，写入 {@link BaseEntity#getParams()} 的 {@link #PARAM_REPORTER_NICK}。
     */
    public void fillReportUsageHistReporterNick(List<CstReportUsageHist> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        Map<String, String> cache = new HashMap<>();
        for (CstReportUsageHist h : rows) {
            String login = extractReportByFromHistJson(h.getAfterJson(), h.getBeforeJson());
            if (StringUtils.isEmpty(login)) {
                login = fallbackReporterLoginFromUsage(h);
            }
            if (StringUtils.isEmpty(login)) {
                h.getParams().put(PARAM_REPORTER_NICK, "—");
                continue;
            }
            String display = cache.computeIfAbsent(login, this::resolveDisplayName);
            h.getParams().put(PARAM_REPORTER_NICK, display);
        }
    }

    /**
     * 修改记录列表：快照 JSON 中无 useDept（旧数据或非空过滤）时，从上报主表按 usage_id 回填到 {@link #PARAM_USE_DEPT_DISPLAY}。
     */
    public void fillReportUsageHistUseDeptDisplay(List<CstReportUsageHist> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        for (CstReportUsageHist h : rows) {
            String dept = extractUseDeptFromHistJson(h.getAfterJson(), h.getBeforeJson());
            if (StringUtils.isEmpty(dept)) {
                dept = fallbackUseDeptFromUsage(h);
            }
            if (StringUtils.isNotEmpty(dept)) {
                h.getParams().put(PARAM_USE_DEPT_DISPLAY, dept);
            }
        }
    }

    private String extractUseDeptFromHistJson(String afterJson, String beforeJson) {
        String raw = StringUtils.isNotEmpty(afterJson) ? afterJson : beforeJson;
        if (StringUtils.isEmpty(raw)) {
            return null;
        }
        try {
            JSONObject o = JSON.parseObject(raw);
            String v = o.getString("useDept");
            return StringUtils.isEmpty(v) ? null : v.trim();
        } catch (Exception e) {
            return null;
        }
    }

    private String fallbackUseDeptFromUsage(CstReportUsageHist h) {
        if (h.getUsageId() == null) {
            return null;
        }
        try {
            if (CstReportUsageHist.BIZ_LIFE.equals(h.getBizType())) {
                CstLifeUsage u = cstLifeUsageService.selectCstLifeUsageById(h.getUsageId());
                return u != null && StringUtils.isNotEmpty(u.getUseDept()) ? u.getUseDept().trim() : null;
            }
            if (CstReportUsageHist.BIZ_KEY.equals(h.getBizType())) {
                CstKeyEquipUsage u = cstKeyEquipUsageService.selectCstKeyEquipUsageById(h.getUsageId());
                return u != null && StringUtils.isNotEmpty(u.getUseDept()) ? u.getUseDept().trim() : null;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /** 旧快照无 reportBy 时，从上报主表按 usage_id 取 createBy（上报人登录名） */
    private String fallbackReporterLoginFromUsage(CstReportUsageHist h) {
        if (h.getUsageId() == null) {
            return null;
        }
        try {
            if (CstReportUsageHist.BIZ_LIFE.equals(h.getBizType())) {
                CstLifeUsage u = cstLifeUsageService.selectCstLifeUsageById(h.getUsageId());
                return u != null && StringUtils.isNotEmpty(u.getCreateBy()) ? u.getCreateBy().trim() : null;
            }
            if (CstReportUsageHist.BIZ_KEY.equals(h.getBizType())) {
                CstKeyEquipUsage u = cstKeyEquipUsageService.selectCstKeyEquipUsageById(h.getUsageId());
                return u != null && StringUtils.isNotEmpty(u.getCreateBy()) ? u.getCreateBy().trim() : null;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private String extractReportByFromHistJson(String afterJson, String beforeJson) {
        String raw = StringUtils.isNotEmpty(afterJson) ? afterJson : beforeJson;
        if (StringUtils.isEmpty(raw)) {
            return null;
        }
        try {
            JSONObject o = JSON.parseObject(raw);
            String v = o.getString("reportBy");
            return StringUtils.isEmpty(v) ? null : v.trim();
        } catch (Exception e) {
            return null;
        }
    }

    private void fillParams(List<? extends BaseEntity> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        Map<String, String> cache = new HashMap<>();
        for (BaseEntity row : rows) {
            fillParams(row, cache);
        }
    }

    private void fillParams(BaseEntity row, Map<String, String> cache) {
        if (row == null) {
            return;
        }
        String createBy = row.getCreateBy();
        if (StringUtils.isEmpty(createBy)) {
            return;
        }
        String key = createBy.trim();
        String display = cache.computeIfAbsent(key, this::resolveDisplayName);
        row.getParams().put(PARAM_CREATE_BY_NICK_NAME, display);
    }

    private void fillParams(BaseEntity row) {
        if (row == null) {
            return;
        }
        fillParams(row, new HashMap<>());
    }

    private String resolveDisplayName(String userName) {
        SysUser u = userService.selectUserByUserName(userName);
        if (u == null) {
            return userName;
        }
        return StringUtils.isNotEmpty(u.getNickName()) ? u.getNickName() : u.getUserName();
    }
}
