package com.ruoyi.web.controller.custom;

import java.util.Collections;
import java.util.List;
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

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:history:list')")
    @GetMapping("/history/list")
    public TableDataInfo historyList(CstReportUsageHist query) {
        query.setBizType(CstReportUsageHist.BIZ_KEY);
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
        CstKeyEquip query = new CstKeyEquip();
        query.setUseDept(useDept);
        List<CstKeyEquip> list;
        try {
            PageHelper.startPage(1, 500);
            list = cstKeyEquipService.selectCstKeyEquipList(query);
        } finally {
            clearPage();
        }
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:list')")
    @GetMapping("/list")
    public TableDataInfo list(CstKeyEquipUsage row) {
        startPage();
        List<CstKeyEquipUsage> list = cstKeyEquipUsageService.selectCstKeyEquipUsageList(row);
        createByNickNameHelper.fillKeyEquipUsage(list);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        CstKeyEquipUsage row = cstKeyEquipUsageService.selectCstKeyEquipUsageById(id);
        createByNickNameHelper.fillKeyEquipUsage(row);
        return success(row);
    }

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:add')")
    @Log(title = "重点设备上报", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CstKeyEquipUsage row) {
        return toAjax(cstKeyEquipUsageService.saveCstKeyEquipUsage(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:edit')")
    @Log(title = "重点设备上报", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CstKeyEquipUsage row) {
        return toAjax(cstKeyEquipUsageService.saveCstKeyEquipUsage(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:remove')")
    @Log(title = "重点设备上报", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(cstKeyEquipUsageService.deleteCstKeyEquipUsageByIds(ids));
    }
}
