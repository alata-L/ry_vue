package com.ruoyi.web.controller.custom;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ruoyi.custom.domain.CstKeyEquipUsage;
import com.ruoyi.custom.service.ICstKeyEquipUsageService;

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

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:list')")
    @GetMapping("/list")
    public TableDataInfo list(CstKeyEquipUsage row) {
        startPage();
        List<CstKeyEquipUsage> list = cstKeyEquipUsageService.selectCstKeyEquipUsageList(row);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('custom:keyUsage:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(cstKeyEquipUsageService.selectCstKeyEquipUsageById(id));
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
