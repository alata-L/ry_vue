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
import org.springframework.web.multipart.MultipartFile;
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

    @Autowired
    private CstCommonUseDeptScopeService cstCommonUseDeptScopeService;

    @PreAuthorize("@ss.hasPermi('custom:keyEquip:list')")
    @GetMapping("/list")
    public TableDataInfo list(CstKeyEquip row) {
        cstCommonUseDeptScopeService.applyToKeyEquipQuery(row);
        startPage();
        List<CstKeyEquip> list = cstKeyEquipService.selectCstKeyEquipList(row);
        return getDataTable(list);
    }

    @Log(title = "重点设备", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('custom:keyEquip:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CstKeyEquip row) {
        cstCommonUseDeptScopeService.applyToKeyEquipQuery(row);
        List<CstKeyEquip> list = cstKeyEquipService.selectCstKeyEquipList(row);
        ExcelUtil<CstKeyEquip> util = new ExcelUtil<>(CstKeyEquip.class);
        util.exportExcel(response, list, "重点设备(单价≥50万)");
    }

    @PreAuthorize("@ss.hasPermi('custom:keyEquip:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        CstKeyEquip row = cstKeyEquipService.selectCstKeyEquipById(id);
        if (row == null) {
            return error("记录不存在");
        }
        if (cstCommonUseDeptScopeService.isCommonUseDeptRestricted()
            && !cstCommonUseDeptScopeService.isUseDeptAllowed(row.getUseDept())) {
            return error("无权限查看该设备数据");
        }
        return success(row);
    }

    @PreAuthorize("@ss.hasPermi('custom:keyEquip:add')")
    @Log(title = "重点设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CstKeyEquip row) {
        row.setStatus("0");
        if (cstCommonUseDeptScopeService.isCommonUseDeptRestricted()
            && !cstCommonUseDeptScopeService.isUseDeptAllowed(row.getUseDept())) {
            return error("只能为本人的所属科室维护台账");
        }
        return toAjax(cstKeyEquipService.insertCstKeyEquip(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:keyEquip:edit')")
    @Log(title = "重点设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CstKeyEquip row) {
        if (row.getId() == null) {
            return error("缺少主键");
        }
        CstKeyEquip old = cstKeyEquipService.selectCstKeyEquipById(row.getId());
        if (old == null) {
            return error("记录不存在");
        }
        if (cstCommonUseDeptScopeService.isCommonUseDeptRestricted()) {
            if (!cstCommonUseDeptScopeService.isUseDeptAllowed(old.getUseDept())) {
                return error("无权限修改该设备数据");
            }
            if (!cstCommonUseDeptScopeService.isUseDeptAllowed(row.getUseDept())) {
                return error("只能为本人的所属科室维护台账");
            }
        }
        return toAjax(cstKeyEquipService.updateCstKeyEquip(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:keyEquip:remove')")
    @Log(title = "重点设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        if (ids == null || ids.length == 0) {
            return error("请选择要删除的数据");
        }
        for (Long id : ids) {
            CstKeyEquip u = cstKeyEquipService.selectCstKeyEquipById(id);
            if (u == null) {
                continue;
            }
            if (cstCommonUseDeptScopeService.isCommonUseDeptRestricted()
                && !cstCommonUseDeptScopeService.isUseDeptAllowed(u.getUseDept())) {
                return error("无权限删除该设备数据");
            }
        }
        return toAjax(cstKeyEquipService.deleteCstKeyEquipByIds(ids));
    }

    @Log(title = "重点设备", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('custom:keyEquip:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        if (file == null || file.isEmpty()) {
            return error("上传文件不能为空");
        }
        ExcelUtil<CstKeyEquip> util = new ExcelUtil<>(CstKeyEquip.class);
        List<CstKeyEquip> keyEquipList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = cstKeyEquipService.importKeyEquip(keyEquipList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<CstKeyEquip> util = new ExcelUtil<>(CstKeyEquip.class);
        util.importTemplateExcel(response, "重点设备数据");
    }
}
