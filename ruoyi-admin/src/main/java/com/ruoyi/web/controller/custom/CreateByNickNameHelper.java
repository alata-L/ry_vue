package com.ruoyi.web.controller.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.custom.domain.CstKeyEquipUsage;
import com.ruoyi.custom.domain.CstLifeUsage;
import com.ruoyi.system.service.ISysUserService;

/**
 * 将 createBy（登录名）解析为展示用昵称，写入 {@link BaseEntity#getParams()}，避免依赖 custom 实体是否含 setCreateByNickName（防止运行包中 custom 未同步编译）。
 */
@Component
public class CreateByNickNameHelper {

    /** 与前端约定：列表行 params.createByNickName */
    public static final String PARAM_CREATE_BY_NICK_NAME = "createByNickName";

    @Autowired
    private ISysUserService userService;

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
