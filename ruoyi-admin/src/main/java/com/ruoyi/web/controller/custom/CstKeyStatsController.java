package com.ruoyi.web.controller.custom;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.custom.service.ICstKeyStatsService;

/**
 * 重点设备统计
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/custom/keyStats")
public class CstKeyStatsController extends BaseController {

    @Autowired
    private ICstKeyStatsService cstKeyStatsService;

    /** 一级页：全院汇总 + 按科室表格 */
    @PreAuthorize("@ss.hasPermi('custom:keyEquip:list')")
    @GetMapping("/summary")
    public AjaxResult summary() {
        Map<String, Object> data = cstKeyStatsService.getSummaryAndDeptList();
        return success(data);
    }

    /** 首页：全院重点设备今年 vs 去年 按月序列（收费与诊治例数） */
    @PreAuthorize("@ss.hasPermi('custom:keyEquip:list')")
    @GetMapping("/summarySeries")
    public AjaxResult summarySeries() {
        return success(cstKeyStatsService.getSummaryMonthlySeries());
    }

    /** 首页：重点设备今年收费 TOP N */
    @PreAuthorize("@ss.hasPermi('custom:keyEquip:list')")
    @GetMapping("/topEquip")
    public AjaxResult topEquip(@RequestParam(required = false, defaultValue = "10") Integer limit) {
        return success(cstKeyStatsService.getKeyEquipTopByCharge(limit != null ? limit : 10));
    }

    /** 二级页：某科室下按设备统计 */
    @PreAuthorize("@ss.hasPermi('custom:keyEquip:list')")
    @GetMapping("/dept")
    public AjaxResult dept(@RequestParam String useDept) {
        List<Map<String, Object>> list = cstKeyStatsService.getDeptEquipList(useDept);
        return success(list);
    }

    /** 三级页：单设备按时段序列，groupBy=day|week|month|year */
    @PreAuthorize("@ss.hasPermi('custom:keyEquip:list')")
    @GetMapping("/equip/{equipId}/series")
    public AjaxResult equipSeries(
            @PathVariable Long equipId,
            @RequestParam(required = false, defaultValue = "day") String groupBy,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime) {
        List<Map<String, Object>> list = cstKeyStatsService.getEquipSeries(equipId, groupBy, beginTime, endTime);
        return success(list);
    }

    /** 首页：按价格范围筛选的TOP10设备按月序列（收费、工作时长、诊疗例数） */
    @PreAuthorize("@ss.hasPermi('custom:keyEquip:list')")
    @GetMapping("/topEquipSeriesByValue")
    public AjaxResult topEquipSeriesByValue(
            @RequestParam(required = false, defaultValue = "500000") Long minValue,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        Map<String, List<Map<String, Object>>> data = cstKeyStatsService.getTopEquipMonthlySeriesByValue(
            minValue != null ? minValue : 500000L,
            limit != null ? limit : 10
        );
        return success(data);
    }
}
