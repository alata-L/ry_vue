package com.ruoyi.web.controller.custom;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.custom.domain.CstKeyEquip;
import com.ruoyi.custom.domain.CstKeyEquipUsage;
import com.ruoyi.custom.domain.CstReportUsageHist;
import com.ruoyi.custom.service.ICstKeyEquipService;
import com.ruoyi.custom.service.ICstKeyEquipUsageService;
import com.ruoyi.custom.service.ICstReportUsageHistService;

/**
 * 重点设备上报（月度使用数据）
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/custom/keyUsage")
public class CstKeyEquipUsageController extends BaseController {

    @Autowired
    private ICstKeyEquipUsageService cstKeyEquipUsageService;

    @Autowired
    private ICstKeyEquipService cstKeyEquipService;

    @Autowired
    private CreateByNickNameHelper createByNickNameHelper;

    @Autowired
    private ICstReportUsageHistService reportUsageHistService;

    @Autowired
    private CstCommonUseDeptScopeService cstCommonUseDeptScopeService;

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:history:list')")
    @GetMapping("/history/list")
    public TableDataInfo historyList(CstReportUsageHist query) {
        query.setBizType(CstReportUsageHist.BIZ_KEY);
        cstCommonUseDeptScopeService.applyToReportUsageHistQuery(query);
        startPage();
        List<CstReportUsageHist> list = reportUsageHistService.selectCstReportUsageHistList(query);
        createByNickNameHelper.fillReportUsageHistReporterNick(list);
        createByNickNameHelper.fillReportUsageHistUseDeptDisplay(list);
        return getDataTable(list);
    }

    /**
     * 上报页按科室拉取重点设备下拉（仅需 keyUsage 权限，不依赖 keyEquip:list）
     */
    @PreAuthorize("@ss.hasAnyPermi('custom:keyUsage:list,custom:keyUsage:query,custom:keyUsage:add,custom:keyUsage:edit')")
    @GetMapping("/options/keyEquips")
    public AjaxResult listKeyEquipsForUsage(@RequestParam String useDept) {
        if (StringUtils.isEmpty(useDept)) {
            return success(Collections.emptyList());
        }
        if (cstCommonUseDeptScopeService.isCommonUseDeptRestricted()) {
            if (!cstCommonUseDeptScopeService.isUseDeptAllowed(useDept.trim())) {
                return success(Collections.emptyList());
            }
        }
        CstKeyEquip query = new CstKeyEquip();
        query.setUseDept(useDept.trim());
        List<CstKeyEquip> list;
        try {
            PageHelper.startPage(1, 500);
            list = cstKeyEquipService.selectCstKeyEquipList(query);
        } finally {
            clearPage();
        }
        return success(list);
    }

    @Log(title = "重点设备上报", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('custom:keyUsage:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CstKeyEquipUsage row) {
        cstCommonUseDeptScopeService.applyToKeyEquipUsageQuery(row);
        List<CstKeyEquipUsage> list = cstKeyEquipUsageService.selectCstKeyEquipUsageList(row);
        createByNickNameHelper.fillKeyEquipUsage(list);
        ExcelUtil<CstKeyEquipUsageExport> util = new ExcelUtil<>(CstKeyEquipUsageExport.class);
        util.exportExcel(response, CstKeyEquipUsageExport.fromFilledRows(list), "使用统计");
    }

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:list')")
    @GetMapping("/list")
    public TableDataInfo list(CstKeyEquipUsage row) {
        cstCommonUseDeptScopeService.applyToKeyEquipUsageQuery(row);
        startPage();
        List<CstKeyEquipUsage> list = cstKeyEquipUsageService.selectCstKeyEquipUsageList(row);
        createByNickNameHelper.fillKeyEquipUsage(list);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        CstKeyEquipUsage row = cstKeyEquipUsageService.selectCstKeyEquipUsageById(id);
        if (row == null) {
            return error("记录不存在");
        }
        if (cstCommonUseDeptScopeService.isCommonUseDeptRestricted()
            && !cstCommonUseDeptScopeService.isUseDeptAllowed(row.getUseDept())) {
            return error("无权限查看该上报数据");
        }
        createByNickNameHelper.fillKeyEquipUsage(row);
        return success(row);
    }

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:add')")
    @Log(title = "重点设备上报", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CstKeyEquipUsage row) {
        if (!validateKeyUsageEquipDept(row)) {
            return error("只能为本人的所属科室设备上报");
        }
        return toAjax(cstKeyEquipUsageService.saveCstKeyEquipUsage(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:edit')")
    @Log(title = "重点设备上报", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CstKeyEquipUsage row) {
        if (row.getId() == null) {
            return error("缺少主键");
        }
        CstKeyEquipUsage old = cstKeyEquipUsageService.selectCstKeyEquipUsageById(row.getId());
        if (old == null) {
            return error("记录不存在");
        }
        if (!validateKeyUsageEquipDept(old) || !validateKeyUsageEquipDept(row)) {
            return error("无权限修改该上报数据");
        }
        return toAjax(cstKeyEquipUsageService.saveCstKeyEquipUsage(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:remove')")
    @Log(title = "重点设备上报", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        if (ids == null || ids.length == 0) {
            return error("请选择要删除的数据");
        }
        for (Long id : ids) {
            CstKeyEquipUsage u = cstKeyEquipUsageService.selectCstKeyEquipUsageById(id);
            if (u == null) {
                continue;
            }
            if (cstCommonUseDeptScopeService.isCommonUseDeptRestricted()
                && !cstCommonUseDeptScopeService.isUseDeptAllowed(u.getUseDept())) {
                return error("无权限删除该上报数据");
            }
        }
        return toAjax(cstKeyEquipUsageService.deleteCstKeyEquipUsageByIds(ids));
    }

    /** 按台账 use_dept 校验 equipId 所属科室 */
    private boolean validateKeyUsageEquipDept(CstKeyEquipUsage row) {
        if (!cstCommonUseDeptScopeService.isCommonUseDeptRestricted()) {
            return true;
        }
        if (row.getEquipId() == null) {
            return false;
        }
        CstKeyEquip eq = cstKeyEquipService.selectCstKeyEquipById(row.getEquipId());
        if (eq == null) {
            return false;
        }
        return cstCommonUseDeptScopeService.isUseDeptAllowed(eq.getUseDept());
    }
}
