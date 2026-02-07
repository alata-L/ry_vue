<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="上报日期" prop="reportDate">
        <el-date-picker v-model="queryParams.reportDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width:140px"></el-date-picker>
      </el-form-item>
      <el-form-item label="使用科室" prop="useDept">
        <el-select v-model="queryParams.useDept" placeholder="请选择" clearable filterable allow-create style="width:180px">
          <el-option v-for="dict in dict.type.cst_use_dept" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备描述" prop="equipDesc">
        <el-input v-model="queryParams.equipDesc" placeholder="请输入设备描述" clearable style="width:180px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['custom:keyUsage:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['custom:keyUsage:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['custom:keyUsage:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="上报日期" align="center" prop="reportDate" width="110" />
      <el-table-column label="设备编号" align="center" prop="equipNo" min-width="100" />
      <el-table-column label="设备描述" align="center" prop="equipDesc" min-width="140" show-overflow-tooltip />
      <el-table-column label="使用科室" align="center" prop="useDept" min-width="120" show-overflow-tooltip />
      <el-table-column label="工作时间" align="center" prop="workHours" width="120" />
      <el-table-column label="每周工作天数" align="center" prop="weekWorkDays" width="120" />
      <el-table-column label="日均服务例数" align="center" prop="treatCount" width="120" />
      <el-table-column label="收费价格" align="center" prop="unitChargePrice" width="120" />
      <el-table-column label="操作" align="center" width="140" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['custom:keyUsage:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['custom:keyUsage:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="560px" custom-class="key-usage-dialog" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="使用科室" prop="useDept">
          <el-select v-model="form.useDept" placeholder="请先选择使用科室" clearable filterable style="width:100%" @change="onUseDeptChange">
            <el-option v-for="dict in dict.type.cst_use_dept" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="重点设备" prop="equipId">
          <el-select v-model="form.equipId" placeholder="请先选择使用科室后再选设备" filterable style="width:100%" :disabled="!form.useDept">
            <el-option v-for="item in keyEquipOptions" :key="item.id" :label="item.equipNo + ' ' + (item.equipDesc || '')" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="上报日期" prop="reportDate">
          <el-date-picker v-model="form.reportDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width:100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="工作时间" prop="workHours">
          <el-input-number v-model="form.workHours" :min="0" :precision="2" controls-position="right" style="width:100%" placeholder="小时" />
        </el-form-item>
        <el-form-item label="每周工作天数" prop="weekWorkDays">
          <el-input-number v-model="form.weekWorkDays" :min="0" :max="7" :precision="0" controls-position="right" style="width:100%" placeholder="天" />
        </el-form-item>
        <el-form-item label="日均服务例数" prop="treatCount">
          <el-input-number v-model="form.treatCount" :min="0" :precision="0" controls-position="right" style="width:100%" placeholder="例" />
        </el-form-item>
        <el-form-item label="收费价格" prop="unitChargePrice">
          <el-input-number v-model="form.unitChargePrice" :min="0" :precision="2" controls-position="right" style="width:100%" placeholder="元" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listKeyUsage, getKeyUsage, addKeyUsage, updateKeyUsage, delKeyUsage } from '@/api/custom/keyUsage'
import { listKeyEquip } from '@/api/custom/keyEquip'

export default {
  name: 'KeyUsage',
  dicts: ['cst_use_dept'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      list: [],
      keyEquipOptions: [],
      title: '',
      open: false,
      queryParams: { pageNum: 1, pageSize: 10, reportDate: undefined, useDept: undefined, equipDesc: undefined },
      form: {},
      rules: {
        useDept: [{ required: true, message: '请选择使用科室', trigger: 'change' }],
        equipId: [{ required: true, message: '请选择重点设备', trigger: 'change' }],
        reportDate: [{ required: true, message: '请选择上报日期', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 使用科室变更：清空设备选择并按科室加载重点设备列表 */
    onUseDeptChange() {
      this.form.equipId = undefined
      this.loadKeyEquipByDept(this.form.useDept)
    },
    /** 按使用科室加载重点设备选项（仅显示该科室拥有的设备） */
    loadKeyEquipByDept(useDept) {
      if (!useDept) {
        this.keyEquipOptions = []
        return
      }
      listKeyEquip({ useDept, pageNum: 1, pageSize: 500 }).then(res => {
        this.keyEquipOptions = res.rows || []
      })
    },
    getList() {
      this.loading = true
      listKeyUsage(this.queryParams).then(res => {
        this.list = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = { id: undefined, useDept: undefined, equipId: undefined, reportDate: undefined, workHours: undefined, weekWorkDays: undefined, treatCount: undefined, unitChargePrice: undefined, remark: undefined }
      this.keyEquipOptions = []
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      // 设置上报日期默认为当天
      const today = new Date()
      this.form.reportDate = today.getFullYear() + '-' + 
        String(today.getMonth() + 1).padStart(2, '0') + '-' + 
        String(today.getDate()).padStart(2, '0')
      this.open = true
      this.title = '新增使用统计'
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids[0]
      getKeyUsage(id).then(res => {
        this.form = res.data
        this.loadKeyEquipByDept(this.form.useDept)
        this.open = true
        this.title = '修改使用统计'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateKeyUsage(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addKeyUsage(this.form).then(() => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const ids = row.id ? [row.id] : this.ids
      this.$modal.confirm('是否确认删除选中的数据？').then(() => {
        return delKeyUsage(ids.join(','))
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss">
/* 移动端上报弹窗不超出屏幕（弹窗 append-to-body 需全局样式） */
@media (max-width: 768px) {
  .key-usage-dialog.el-dialog {
    width: 95vw !important;
    max-width: 100vw;
  }
}
</style>
