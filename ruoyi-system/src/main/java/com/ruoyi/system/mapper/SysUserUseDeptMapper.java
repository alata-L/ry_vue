package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.SysUserUseDept;

/**
 * 用户与使用科室关联
 *
 * @author ruoyi
 */
public interface SysUserUseDeptMapper
{
    /**
     * 按用户删除关联
     */
    int deleteUserUseDeptByUserId(Long userId);

    /**
     * 批量删除用户关联
     */
    int deleteUserUseDept(Long[] userIds);

    /**
     * 批量新增
     */
    int batchUserUseDept(@Param("list") List<SysUserUseDept> list);

    /**
     * 查询用户已选科室词典编码（有序）
     */
    List<Long> selectDeptDictCodesByUserId(Long userId);
}
