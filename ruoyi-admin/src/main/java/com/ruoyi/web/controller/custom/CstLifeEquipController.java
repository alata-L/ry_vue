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
import com.ruoyi.custom.domain.CstLifeEquip;
import com.ruoyi.custom.service.ICstLifeEquipService;

/**
 * 通用设备台账
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/custom/lifeEquip")
public class CstLifeEquipController extends BaseController {

    @Autowired
    private ICstLifeEquipService cstLifeEquipService;

    @Autowired
    private CstCommonUseDeptScopeService cstCommonUseDeptScopeService;

    @PreAuthorize("@ss.hasPermi('custom:lifeEquip:list')")
    @GetMapping("/list")
    public TableDataInfo list(CstLifeEquip row) {
        cstCommonUseDeptScopeService.applyToLifeEquipQuery(row);
        startPage();
        List<CstLifeEquip> list = cstLifeEquipService.selectCstLifeEquipList(row);
        return getDataTable(list);
    }

    @Log(title = "通用设备", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('custom:lifeEquip:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, CstLifeEquip row) {
        cstCommonUseDeptScopeService.applyToLifeEquipQuery(row);
        List<CstLifeEquip> list = cstLifeEquipService.selectCstLifeEquipList(row);
        ExcelUtil<CstLifeEquip> util = new ExcelUtil<>(CstLifeEquip.class);
        util.hideColumn("subAssetNo");
        util.exportExcel(response, list, "通用设备数据");
    }

    /** 按设备类型导出：1监护仪 2输液泵 3注射泵 */
    @Log(title = "通用设备导出", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('custom:lifeEquip:export')")
    @PostMapping("/exportByType")
    public void exportByType(HttpServletResponse response, String equipType) {
        CstLifeEquip row = new CstLifeEquip();
        row.setEquipType(equipType);
        row.setStatus("0");
        cstCommonUseDeptScopeService.applyToLifeEquipQuery(row);
        List<CstLifeEquip> list = cstLifeEquipService.selectCstLifeEquipList(row);
        String title = "1".equals(equipType) ? "监护仪" : "2".equals(equipType) ? "输液泵" : "3".equals(equipType) ? "注射泵" : "通用设备";
        ExcelUtil<CstLifeEquip> util = new ExcelUtil<>(CstLifeEquip.class);
        util.hideColumn("subAssetNo");
        util.exportExcel(response, list, title);
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeEquip:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        CstLifeEquip row = cstLifeEquipService.selectCstLifeEquipById(id);
        if (row == null) {
            return error("记录不存在");
        }
        if (cstCommonUseDeptScopeService.isCommonUseDeptRestricted()
            && !cstCommonUseDeptScopeService.isUseDeptAllowed(row.getUseDept())) {
            return error("无权限查看该设备数据");
        }
        return success(row);
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeEquip:add')")
    @Log(title = "通用设备", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CstLifeEquip row) {
        row.setStatus("0");
        if (cstCommonUseDeptScopeService.isCommonUseDeptRestricted()
            && !cstCommonUseDeptScopeService.isUseDeptAllowed(row.getUseDept())) {
            return error("只能为本人的所属科室维护台账");
        }
        return toAjax(cstLifeEquipService.insertCstLifeEquip(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeEquip:edit')")
    @Log(title = "通用设备", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CstLifeEquip row) {
        if (row.getId() == null) {
            return error("缺少主键");
        }
        CstLifeEquip old = cstLifeEquipService.selectCstLifeEquipById(row.getId());
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
        return toAjax(cstLifeEquipService.updateCstLifeEquip(row));
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeEquip:remove')")
    @Log(title = "通用设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        if (ids == null || ids.length == 0) {
            return error("请选择要删除的数据");
        }
        for (Long id : ids) {
            CstLifeEquip u = cstLifeEquipService.selectCstLifeEquipById(id);
            if (u == null) {
                continue;
            }
            if (cstCommonUseDeptScopeService.isCommonUseDeptRestricted()
                && !cstCommonUseDeptScopeService.isUseDeptAllowed(u.getUseDept())) {
                return error("无权限删除该设备数据");
            }
        }
        return toAjax(cstLifeEquipService.deleteCstLifeEquipByIds(ids));
    }

    /** 按科室统计各类型设备台数（首页/统计用） */
    @PreAuthorize("@ss.hasPermi('custom:lifeEquip:list')")
    @GetMapping("/stats/deptType")
    public AjaxResult statsDeptType() {
        return success(cstLifeEquipService.countByDeptAndType(cstCommonUseDeptScopeService.currentScope()));
    }

    /** 按使用年限统计：minYears=6/10/15 */
    @PreAuthorize("@ss.hasPermi('custom:lifeEquip:list')")
    @GetMapping("/stats/years")
    public AjaxResult statsYears(Integer minYears) {
        if (minYears == null) minYears = 6;
        return success(cstLifeEquipService.countByYearsAndDept(minYears, cstCommonUseDeptScopeService.currentScope()));
    }

    /** 按使用年限和设备类型统计：minYears=6/10/15 */
    @PreAuthorize("@ss.hasPermi('custom:lifeEquip:list')")
    @GetMapping("/stats/yearsByType")
    public AjaxResult statsYearsByType(Integer minYears) {
        if (minYears == null) minYears = 6;
        return success(cstLifeEquipService.countByYearsAndDeptAndType(minYears, cstCommonUseDeptScopeService.currentScope()));
    }

    @Log(title = "通用设备", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('custom:lifeEquip:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        if (file == null || file.isEmpty()) {
            return error("上传文件不能为空");
        }
        ExcelUtil<CstLifeEquip> util = new ExcelUtil<>(CstLifeEquip.class);
        util.hideColumn("subAssetNo");
        List<CstLifeEquip> lifeEquipList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = cstLifeEquipService.importLifeEquip(lifeEquipList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil<CstLifeEquip> util = new ExcelUtil<>(CstLifeEquip.class);
        util.hideColumn("subAssetNo");
        util.importTemplateExcel(response, "通用设备数据");
    }
}
