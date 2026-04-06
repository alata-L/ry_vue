package com.ruoyi.custom.domain;

import java.util.Collections;
import java.util.List;

/**
 * 使用科室（cst_use_dept）数据范围：用于 common 角色仅本人所属科室。
 */
public final class UseDeptScope {

    /** 不限制（管理员等） */
    private final boolean unrestricted;
    /** 无可用科室（禁止访问任何数据） */
    private final boolean denied;
    private final List<String> useDeptCodes;

    private UseDeptScope(boolean unrestricted, boolean denied, List<String> useDeptCodes) {
        this.unrestricted = unrestricted;
        this.denied = denied;
        this.useDeptCodes = useDeptCodes != null ? useDeptCodes : Collections.emptyList();
    }

    public static UseDeptScope unrestricted() {
        return new UseDeptScope(true, false, null);
    }

    public static UseDeptScope denied() {
        return new UseDeptScope(false, true, Collections.emptyList());
    }

    public static UseDeptScope of(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return denied();
        }
        return new UseDeptScope(false, false, codes);
    }

    public boolean isUnrestricted() {
        return unrestricted;
    }

    public boolean isDenied() {
        return denied;
    }

    public List<String> getUseDeptCodes() {
        return useDeptCodes;
    }
}
