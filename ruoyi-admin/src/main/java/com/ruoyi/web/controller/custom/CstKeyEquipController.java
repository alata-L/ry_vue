package com.ruoyi.web.controller.custom;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.custom.domain.CstKeyEquip;
import com.ruoyi.custom.service.ICstKeyEquipService;

/**
 * 重点设备台账(单价≥50万)
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/custom/keyEquip")
public class CstKeyEquipController extends BaseController {

    @Autowired
    private ICstKeyEquipService cstKeyEquipService;

    @PreAuthorize("@ss.hasPermi('custom:keyEquip:list')")
    @GetMapping("/list")
    public TableDataInfo list(CstKeyEquip row) {
        startPage();
        List<CstKeyEquip> list = cstKeyEquipService.selectCstKeyEquipList(row);
        return getDataTable(list);
    }

    @Log(title = "重点设备", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('custom:keyEquip:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CstKeyEquip row) {
        List<CstKeyEquip> list = cstKeyEquipService.selectCstKeyEquipList(row);
        ExcelUtil<CstKeyEquip> util = new ExcelUtil<>(CstKeyEquip.class);
        util.exportExcel(response, list, "重点设备(单价≥50万)");
    }

    @PreAuthorize("@ss.hasPermi('custom:keyEquip:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(cstKeyEquipService.selectCstKeyEquipById(id));
    }

    @PreAuthorize("@ss.hasPermi('custom:keyEquip:add')")
    @Log(title = "重点设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CstKeyEquip row) {
        row.setStatus("0");
        return toAjax(cstKeyEquipService.insertCstKeyEquip(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:keyEquip:edit')")
    @Log(title = "重点设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CstKeyEquip row) {
        return toAjax(cstKeyEquipService.updateCstKeyEquip(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:keyEquip:remove')")
    @Log(title = "重点设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(cstKeyEquipService.deleteCstKeyEquipByIds(ids));
    }
}
