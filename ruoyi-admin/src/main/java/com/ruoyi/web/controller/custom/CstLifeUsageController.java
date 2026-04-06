package com.ruoyi.web.controller.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.custom.domain.CstLifeUsage;
import com.ruoyi.custom.domain.CstReportUsageHist;
import com.ruoyi.custom.service.ICstLifeUsageService;
import com.ruoyi.custom.service.ICstReportUsageHistService;

/**
 * 通用设备日使用上报
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/custom/lifeUsage")
public class CstLifeUsageController extends BaseController {

    @Autowired
    private ICstLifeUsageService cstLifeUsageService;

    @Autowired
    private CreateByNickNameHelper createByNickNameHelper;

    @Autowired
    private ICstReportUsageHistService reportUsageHistService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CstCommonUseDeptScopeService cstCommonUseDeptScopeService;

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:history:list')")
    @GetMapping("/history/list")
    public TableDataInfo historyList(CstReportUsageHist query) {
        query.setBizType(CstReportUsageHist.BIZ_LIFE);
        cstCommonUseDeptScopeService.applyToReportUsageHistQuery(query);
        startPage();
        List<CstReportUsageHist> list = reportUsageHistService.selectCstReportUsageHistList(query);
        createByNickNameHelper.fillReportUsageHistReporterNick(list);
        createByNickNameHelper.fillReportUsageHistUseDeptDisplay(list);
        return getDataTable(list);
    }

    @Log(title = "通用设备使用上报", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CstLifeUsage row) {
        cstCommonUseDeptScopeService.applyToLifeUsageQuery(row);
        List<CstLifeUsage> list = cstLifeUsageService.selectCstLifeUsageList(row);
        fillEquipInstallCount(list);
        createByNickNameHelper.fillLifeUsage(list);
        ExcelUtil<CstLifeUsageExport> util = new ExcelUtil<>(CstLifeUsageExport.class);
        util.exportExcel(response, CstLifeUsageExport.fromFilledRows(list), "使用数据");
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:list')")
    @GetMapping("/list")
    public TableDataInfo list(CstLifeUsage row) {
        cstCommonUseDeptScopeService.applyToLifeUsageQuery(row);
        startPage();
        List<CstLifeUsage> list = cstLifeUsageService.selectCstLifeUsageList(row);
        fillEquipInstallCount(list);
        createByNickNameHelper.fillLifeUsage(list);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        CstLifeUsage row = cstLifeUsageService.selectCstLifeUsageById(id);
        if (row == null) {
            return error("记录不存在");
        }
        if (cstCommonUseDeptScopeService.isCommonUseDeptRestricted()
            && !cstCommonUseDeptScopeService.isUseDeptAllowed(row.getUseDept())) {
            return error("无权限查看该上报数据");
        }
        fillEquipInstallCount(row);
        createByNickNameHelper.fillLifeUsage(row);
        return success(row);
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:add')")
    @Log(title = "生命支持使用上报", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CstLifeUsage row) {
        if (!validateCommonLifeUsageWrite(row)) {
            return error("只能为本人的所属科室上报");
        }
        return toAjax(cstLifeUsageService.saveCstLifeUsage(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:edit')")
    @Log(title = "生命支持使用上报", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CstLifeUsage row) {
        if (row.getId() == null) {
            return error("缺少主键");
        }
        CstLifeUsage old = cstLifeUsageService.selectCstLifeUsageById(row.getId());
        if (old == null) {
            return error("记录不存在");
        }
        if (!validateCommonLifeUsageEdit(old, row)) {
            return error("无权限修改该上报数据");
        }
        return toAjax(cstLifeUsageService.saveCstLifeUsage(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:remove')")
    @Log(title = "生命支持使用上报", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        if (ids == null || ids.length == 0) {
            return error("请选择要删除的数据");
        }
        for (Long id : ids) {
            CstLifeUsage u = cstLifeUsageService.selectCstLifeUsageById(id);
            if (u == null) {
                continue;
            }
            if (cstCommonUseDeptScopeService.isCommonUseDeptRestricted()
                && !cstCommonUseDeptScopeService.isUseDeptAllowed(u.getUseDept())) {
                return error("无权限删除该上报数据");
            }
        }
        return toAjax(cstLifeUsageService.deleteCstLifeUsageByIds(ids));
    }

    /** 按时间范围汇总使用数据，groupBy: day|month|year */
    @PreAuthorize("@ss.hasPermi('custom:lifeStats:list')")
    @GetMapping("/sum")
    public AjaxResult sumUsage(String useDept, String equipType, String beginTime, String endTime, String groupBy) {
        Map<String, Object> params = new HashMap<>();
        params.put("useDept", useDept);
        params.put("equipType", equipType);
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        params.put("groupBy", groupBy != null ? groupBy : "day");
        cstCommonUseDeptScopeService.applyToSumParams(params);
        return success(cstLifeUsageService.sumUsageByRange(params));
    }

    private boolean validateCommonLifeUsageWrite(CstLifeUsage row) {
        if (!cstCommonUseDeptScopeService.isCommonUseDeptRestricted()) {
            return true;
        }
        return cstCommonUseDeptScopeService.isUseDeptAllowed(row.getUseDept());
    }

    private boolean validateCommonLifeUsageEdit(CstLifeUsage old, CstLifeUsage row) {
        if (!cstCommonUseDeptScopeService.isCommonUseDeptRestricted()) {
            return true;
        }
        if (!cstCommonUseDeptScopeService.isUseDeptAllowed(old.getUseDept())) {
            return false;
        }
        return cstCommonUseDeptScopeService.isUseDeptAllowed(row.getUseDept());
    }

    /**
     * 装配台数（cst_life_equip 同科室+同 equip_type、status=0 条数）。放在 admin 层用 JdbcTemplate 查询，
     * 避免仅编译 ruoyi-admin 时仍使用本地仓库中旧的 ruoyi-custom.jar 导致不生效。
     */
    private void fillEquipInstallCount(List<CstLifeUsage> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (CstLifeUsage u : list) {
            fillEquipInstallCount(u);
        }
    }

    private void fillEquipInstallCount(CstLifeUsage u) {
        if (u == null) {
            return;
        }
        int n = 0;
        String ud = u.getUseDept() != null ? u.getUseDept().trim() : "";
        String et = u.getEquipType() != null ? u.getEquipType().trim() : "";
        if (!ud.isEmpty() && !et.isEmpty()) {
            Integer c = jdbcTemplate.queryForObject(
                "select count(*) from cst_life_equip where status = '0' and use_dept = ? and equip_type = ?",
                Integer.class, ud, et);
            n = c != null ? c : 0;
        }
        // 仅写入 params：本地 .m2 中 ruoyi-custom 若为旧版可能无 equipInstallCount 字段，避免编译失败；前端已读 params.equipInstallCount
        u.getParams().put("equipInstallCount", n);
    }
}
