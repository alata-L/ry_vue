package com.ruoyi.web.controller.custom;

import java.util.List;
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

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:history:list')")
    @GetMapping("/history/list")
    public TableDataInfo historyList(CstReportUsageHist query) {
        query.setBizType(CstReportUsageHist.BIZ_LIFE);
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
        List<CstLifeUsage> list = cstLifeUsageService.selectCstLifeUsageList(row);
        fillEquipInstallCount(list);
        createByNickNameHelper.fillLifeUsage(list);
        ExcelUtil<CstLifeUsageExport> util = new ExcelUtil<>(CstLifeUsageExport.class);
        util.exportExcel(response, CstLifeUsageExport.fromFilledRows(list), "使用数据");
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:list')")
    @GetMapping("/list")
    public TableDataInfo list(CstLifeUsage row) {
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
        fillEquipInstallCount(row);
        createByNickNameHelper.fillLifeUsage(row);
        return success(row);
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:add')")
    @Log(title = "生命支持使用上报", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CstLifeUsage row) {
        return toAjax(cstLifeUsageService.saveCstLifeUsage(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:edit')")
    @Log(title = "生命支持使用上报", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CstLifeUsage row) {
        return toAjax(cstLifeUsageService.saveCstLifeUsage(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:remove')")
    @Log(title = "生命支持使用上报", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(cstLifeUsageService.deleteCstLifeUsageByIds(ids));
    }

    /** 按时间范围汇总使用数据，groupBy: day|month|year */
    @PreAuthorize("@ss.hasPermi('custom:lifeStats:list')")
    @GetMapping("/sum")
    public AjaxResult sumUsage(String useDept, String equipType, String beginTime, String endTime, String groupBy) {
        return success(cstLifeUsageService.sumUsageByRange(useDept, equipType, beginTime, endTime, groupBy));
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
