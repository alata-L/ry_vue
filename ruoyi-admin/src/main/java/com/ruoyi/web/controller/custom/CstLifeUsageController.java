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
import com.ruoyi.custom.domain.CstLifeUsage;
import com.ruoyi.custom.service.ICstLifeUsageService;

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

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:list')")
    @GetMapping("/list")
    public TableDataInfo list(CstLifeUsage row) {
        startPage();
        List<CstLifeUsage> list = cstLifeUsageService.selectCstLifeUsageList(row);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('custom:lifeUsage:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(cstLifeUsageService.selectCstLifeUsageById(id));
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
}
