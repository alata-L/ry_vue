package com.ruoyi.web.controller.custom;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.custom.service.ICstLifeStatsService;

/**
 * 生命支持类设备统计（首页概览）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/custom/lifeStats")
public class CstLifeStatsController extends BaseController {

    @Autowired
    private ICstLifeStatsService cstLifeStatsService;

    /** 首页：按设备类型使用趋势（今年按月） */
    @PreAuthorize("@ss.hasPermi('custom:lifeStats:list')")
    @GetMapping("/usageTrend")
    public AjaxResult usageTrend() {
        List<Map<String, Object>> list = cstLifeStatsService.getUsageTrend();
        return success(list);
    }

    /** 首页：科室使用台数排名（今年） */
    @PreAuthorize("@ss.hasPermi('custom:lifeStats:list')")
    @GetMapping("/usageByDept")
    public AjaxResult usageByDept() {
        List<Map<String, Object>> list = cstLifeStatsService.getUsageByDept();
        return success(list);
    }
}
