package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户与「使用科室」词典关联（sys_dict_data.dict_code，dict_type=cst_use_dept）
 *
 * @author ruoyi
 */
public class SysUserUseDept
{
    private Long userId;

    /** 对应 sys_dict_data.dict_code */
    private Long deptDictCode;

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getDeptDictCode()
    {
        return deptDictCode;
    }

    public void setDeptDictCode(Long deptDictCode)
    {
        this.deptDictCode = deptDictCode;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("deptDictCode", getDeptDictCode())
            .toString();
    }
}
