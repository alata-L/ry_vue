package com.ruoyi.web.controller.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.custom.domain.CstLifeUsage;
import com.ruoyi.custom.domain.UseDeptScope;
import com.ruoyi.system.mapper.SysUserUseDeptMapper;

/**
 * 数据范围：仅当用户具备 common-equipment 或 major-equipment、且不具备 manger 时，
 * 限制为本人「所属科室」（sys_user_use_dept / use_dept）。
 * 若同时分配了 manger（管理员类角色），则不按科室过滤，与全院管理一致。
 */
@Component
public class CstCommonUseDeptScopeService {

    public static final String ROLE_COMMON_EQUIPMENT = "common-equipment";
    public static final String ROLE_MAJOR_EQUIPMENT = "major-equipment";

    /** 与角色管理中「管理员」权限字符一致（若库中改名需同步） */
    public static final String ROLE_MANGER = "manger";

    @Autowired
    private SysUserUseDeptMapper userUseDeptMapper;

    public boolean isCommonUseDeptRestricted() {
        Long uid = SecurityUtils.getUserId();
        if (SecurityUtils.isAdmin(uid)) {
            return false;
        }
        if (SecurityUtils.hasRole(ROLE_MANGER)) {
            return false;
        }
        return SecurityUtils.hasRole(ROLE_COMMON_EQUIPMENT) || SecurityUtils.hasRole(ROLE_MAJOR_EQUIPMENT);
    }

    /**
     * 当前登录用户允许访问的 cst_use_dept 词典编码（字符串，与表 use_dept 一致）。
     */
    public List<String> allowedUseDeptCodes() {
        List<Long> codes = userUseDeptMapper.selectDeptDictCodesByUserId(SecurityUtils.getUserId());
        if (codes != null && !codes.isEmpty()) {
            return codes.stream().map(String::valueOf).collect(Collectors.toList());
        }
        SysUser u = SecurityUtils.getLoginUser().getUser();
        String raw = u != null ? u.getUseDept() : null;
        if (StringUtils.isEmpty(raw)) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String p : raw.split(",")) {
            String s = p != null ? p.trim() : "";
            if (!s.isEmpty()) {
                list.add(s);
            }
        }
        return list;
    }

    public UseDeptScope currentScope() {
        if (!isCommonUseDeptRestricted()) {
            return UseDeptScope.unrestricted();
        }
        return UseDeptScope.of(allowedUseDeptCodes());
    }

    public String normalizeDeptCode(String code) {
        if (code == null) {
            return "";
        }
        return code.trim();
    }

    public boolean isUseDeptAllowed(String useDept) {
        String n = normalizeDeptCode(useDept);
        if (n.isEmpty()) {
            return false;
        }
        return allowedUseDeptCodes().contains(n);
    }

    /**
     * 列表/导出：写入 {@link CstLifeUsage#getParams()}，与 Mapper 中条件配合。
     */
    public void applyToLifeUsageQuery(CstLifeUsage row) {
        UseDeptScope scope = currentScope();
        if (scope.isUnrestricted()) {
            return;
        }
        if (scope.isDenied()) {
            row.getParams().put("commonUseDeptDenied", true);
            return;
        }
        List<String> allowed = scope.getUseDeptCodes();
        String q = row.getUseDept();
        if (StringUtils.isNotEmpty(q)) {
            String nq = normalizeDeptCode(q);
            if (!allowed.contains(nq)) {
                row.getParams().put("commonUseDeptDenied", true);
                row.setUseDept(null);
                return;
            }
        } else {
            row.getParams().put("useDeptList", allowed);
        }
    }

    /**
     * 汇总接口参数 Map（与 {@code sumUsageByRange} 一致）。
     */
    public void applyToSumParams(java.util.Map<String, Object> params) {
        UseDeptScope scope = currentScope();
        if (scope.isUnrestricted()) {
            return;
        }
        if (scope.isDenied()) {
            params.put("commonUseDeptDenied", true);
            return;
        }
        List<String> allowed = scope.getUseDeptCodes();
        String q = (String) params.get("useDept");
        if (StringUtils.isNotEmpty(q)) {
            if (!allowed.contains(normalizeDeptCode(q))) {
                params.put("commonUseDeptDenied", true);
                params.remove("useDept");
            }
        } else {
            params.put("useDeptList", allowed);
        }
    }

    public void applyToLifeEquipQuery(com.ruoyi.custom.domain.CstLifeEquip row) {
        UseDeptScope scope = currentScope();
        if (scope.isUnrestricted()) {
            return;
        }
        if (scope.isDenied()) {
            row.getParams().put("commonUseDeptDenied", true);
            return;
        }
        List<String> allowed = scope.getUseDeptCodes();
        String q = row.getUseDept();
        if (StringUtils.isNotEmpty(q)) {
            String nq = normalizeDeptCode(q);
            if (!allowed.contains(nq)) {
                row.getParams().put("commonUseDeptDenied", true);
                row.setUseDept(null);
            }
        } else {
            row.getParams().put("useDeptList", allowed);
        }
    }

    public void applyToReportUsageHistQuery(com.ruoyi.custom.domain.CstReportUsageHist row) {
        UseDeptScope scope = currentScope();
        if (scope.isUnrestricted()) {
            return;
        }
        if (scope.isDenied()) {
            row.getParams().put("commonUseDeptDenied", true);
            return;
        }
        List<String> allowed = scope.getUseDeptCodes();
        String q = row.getUseDept();
        if (StringUtils.isNotEmpty(q)) {
            String nq = normalizeDeptCode(q);
            if (!allowed.contains(nq)) {
                row.getParams().put("commonUseDeptDenied", true);
                row.setUseDept(null);
            }
        } else {
            row.getParams().put("useDeptList", allowed);
        }
    }

    /** 重点设备台账列表/导出（与通用设备台账同一套 params） */
    public void applyToKeyEquipQuery(com.ruoyi.custom.domain.CstKeyEquip row) {
        UseDeptScope scope = currentScope();
        if (scope.isUnrestricted()) {
            return;
        }
        if (scope.isDenied()) {
            row.getParams().put("commonUseDeptDenied", true);
            return;
        }
        List<String> allowed = scope.getUseDeptCodes();
        String q = row.getUseDept();
        if (StringUtils.isNotEmpty(q)) {
            String nq = normalizeDeptCode(q);
            if (!allowed.contains(nq)) {
                row.getParams().put("commonUseDeptDenied", true);
                row.setUseDept(null);
            }
        } else {
            row.getParams().put("useDeptList", allowed);
        }
    }

    /** 重点设备上报列表/导出：按台账 use_dept 过滤 */
    public void applyToKeyEquipUsageQuery(com.ruoyi.custom.domain.CstKeyEquipUsage row) {
        UseDeptScope scope = currentScope();
        if (scope.isUnrestricted()) {
            return;
        }
        if (scope.isDenied()) {
            row.getParams().put("commonUseDeptDenied", true);
            return;
        }
        List<String> allowed = scope.getUseDeptCodes();
        String q = row.getUseDept();
        if (StringUtils.isNotEmpty(q)) {
            String nq = normalizeDeptCode(q);
            if (!allowed.contains(nq)) {
                row.getParams().put("commonUseDeptDenied", true);
                row.setUseDept(null);
            }
        } else {
            row.getParams().put("useDeptList", allowed);
        }
    }
}
