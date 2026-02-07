package com.ruoyi.custom.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.custom.domain.CstKeyEquipUsage;

/**
 * 重点设备月度使用上报 数据层
 *
 * @author ruoyi
 */
public interface CstKeyEquipUsageMapper {

    List<CstKeyEquipUsage> selectCstKeyEquipUsageList(CstKeyEquipUsage row);

    CstKeyEquipUsage selectCstKeyEquipUsageById(Long id);

    CstKeyEquipUsage selectByEquipIdAndReportDate(@Param("equipId") Long equipId, @Param("reportDate") String reportDate);

    int insertCstKeyEquipUsage(CstKeyEquipUsage row);

    int updateCstKeyEquipUsage(CstKeyEquipUsage row);

    int deleteCstKeyEquipUsageById(Long id);

    int deleteCstKeyEquipUsageByIds(Long[] ids);

    /** 指定日期范围内全院收费与诊治例数汇总（收费=sum(treat_count*unit_charge_price)） */
    Map<String, Object> sumChargeAndTreatInRange(@Param("start") String start, @Param("end") String end);

    /** 指定日期范围内按科室汇总收费与诊治例数 */
    List<Map<String, Object>> sumChargeAndTreatByDeptInRange(@Param("start") String start, @Param("end") String end);

    /** 指定日期范围内按设备汇总收费与诊治例数 */
    List<Map<String, Object>> sumChargeAndTreatByEquipInRange(@Param("start") String start, @Param("end") String end);

    /** 指定日期范围内、指定科室下按设备汇总收费与诊治例数 */
    List<Map<String, Object>> sumChargeAndTreatByEquipInRangeAndDept(@Param("useDept") String useDept, @Param("start") String start, @Param("end") String end);

    /** 单设备按时段聚合：groupBy=day|week|month|year，返回 period/workHours/treatCount/totalCharge */
    List<Map<String, Object>> selectSeriesByEquip(@Param("equipId") Long equipId, @Param("start") String start, @Param("end") String end, @Param("groupBy") String groupBy);

    /** 指定日期范围内全院按月汇总收费与诊治例数，返回 period(yyyy-MM)/totalCharge/totalTreat */
    List<Map<String, Object>> sumChargeAndTreatByMonthInRange(@Param("start") String start, @Param("end") String end);

    /** 指定日期范围内按设备汇总收费，按 totalCharge 降序，取前 limit 条，返回 equipId/equipNo/equipDesc/useDept/totalCharge/totalTreat */
    List<Map<String, Object>> topEquipByChargeInRange(@Param("start") String start, @Param("end") String end, @Param("limit") int limit);

    /** 按价格范围筛选的TOP10设备（≥50万或≥100万），按 totalCharge 降序，返回 equipId/equipNo/equipDesc/useDept/totalCharge/totalTreat/totalWorkHours */
    List<Map<String, Object>> topEquipByChargeInRangeAndValue(@Param("start") String start, @Param("end") String end, @Param("minValue") Long minValue, @Param("limit") int limit);

    /** TOP10设备按月序列（收费、工作时长、诊疗例数），返回 period/totalCharge/totalWorkHours/totalTreat */
    List<Map<String, Object>> topEquipMonthlySeries(@Param("start") String start, @Param("end") String end, @Param("minValue") Long minValue, @Param("equipIds") List<Long> equipIds);
}
