package com.ruoyi.custom.service;

import java.util.List;
import java.util.Map;

/**
 * 重点设备统计 服务层
 *
 * @author ruoyi
 */
public interface ICstKeyStatsService {

    /**
     * 一级页：全院汇总（6 卡片）+ 按科室表格数据
     * summary: equipCount, totalValue, chargeThisMonth, chargeLastMonth, chargeThisYear, chargeLastYear
     * deptList: useDept, equipCount, totalValue, chargeThisMonth, chargeLastMonth, chargeThisYear, chargeLastYear, treatThisMonth, treatLastMonth, treatThisYear, treatLastYear
     */
    Map<String, Object> getSummaryAndDeptList();

    /**
     * 二级页：某科室下按设备统计
     * 每行: equipId, equipNo, equipDesc, totalValue, chargeThisMonth, chargeLastMonth, chargeThisYear, chargeLastYear, treatThisMonth, treatLastMonth, treatThisYear, treatLastYear
     */
    List<Map<String, Object>> getDeptEquipList(String useDept);

    /**
     * 三级页：单设备按时段序列，groupBy=day|week|month|year
     * 每行: period, workHours, treatCount, totalCharge, avgChargePerCase
     */
    List<Map<String, Object>> getEquipSeries(Long equipId, String groupBy, String beginTime, String endTime);

    /**
     * 首页：全院重点设备今年 vs 去年 按月序列（收费与诊治例数）
     * 返回 key: thisYear / lastYear，value: List<Map> 每项 period(yyyy-MM), totalCharge, totalTreat
     */
    Map<String, List<Map<String, Object>>> getSummaryMonthlySeries();

    /**
     * 首页：重点设备今年收费 TOP N，返回 equipId/equipNo/equipDesc/useDept/totalCharge/totalTreat
     */
    List<Map<String, Object>> getKeyEquipTopByCharge(int limit);
}
