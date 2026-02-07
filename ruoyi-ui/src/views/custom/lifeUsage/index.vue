<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="上报日期">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd" style="width:240px"></el-date-picker>
      </el-form-item>
      <el-form-item label="设备类型" prop="equipType">
        <el-select v-model="queryParams.equipType" placeholder="请选择" clearable>
          <el-option v-for="dict in dict.type.cst_life_equip_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="使用科室" prop="useDept">
        <el-select v-model="queryParams.useDept" placeholder="请选择" clearable filterable allow-create>
          <el-option v-for="dict in dict.type.cst_use_dept" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['custom:lifeUsage:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['custom:lifeUsage:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['custom:lifeUsage:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="上报日期" align="center" prop="statDate" width="110" />
      <el-table-column label="设备类型" align="center" prop="equipType" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.cst_life_equip_type" :value="scope.row.equipType"/>
        </template>
      </el-table-column>
      <el-table-column label="使用科室" align="center" prop="useDept" min-width="120" show-overflow-tooltip />
      <el-table-column label="当日使用台数" align="center" prop="usedCount" width="110" />
      <el-table-column label="操作" align="center" width="140" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['custom:lifeUsage:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['custom:lifeUsage:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="520px" custom-class="life-usage-dialog" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="上报日期" prop="statDate">
          <el-date-picker v-model="form.statDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width:100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="设备类型" prop="equipType">
          <el-select v-model="form.equipType" placeholder="请选择设备类型" style="width:100%">
            <el-option v-for="dict in dict.type.cst_life_equip_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="使用科室" prop="useDept">
          <el-select v-model="form.useDept" placeholder="请选择或输入使用科室" filterable allow-create style="width:100%">
            <el-option v-for="dict in dict.type.cst_use_dept" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="当日使用台数" prop="usedCount">
          <el-input-number v-model="form.usedCount" :min="0" controls-position="right" style="width:100%" />
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
import { listLifeUsage, getLifeUsage, addLifeUsage, updateLifeUsage, delLifeUsage } from '@/api/custom/lifeUsage'

export default {
  name: 'LifeUsage',
  dicts: ['cst_use_dept', 'cst_life_equip_type'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      list: [],
      dateRange: [],
      title: '',
      open: false,
      queryParams: { pageNum: 1, pageSize: 10, equipType: undefined, useDept: undefined },
      form: {},
      rules: {
        statDate: [{ required: true, message: '上报日期不能为空', trigger: 'change' }],
        equipType: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
        useDept: [{ required: true, message: '请选择使用科室', trigger: 'change' }],
        usedCount: [{ required: true, message: '当日使用台数不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listLifeUsage(this.addDateRange(this.queryParams, this.dateRange)).then(res => {
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
      this.form = { id: undefined, statDate: undefined, equipType: undefined, useDept: undefined, usedCount: 0, remark: undefined }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
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
      // 设置默认日期为今天
      const today = new Date()
      const year = today.getFullYear()
      const month = String(today.getMonth() + 1).padStart(2, '0')
      const day = String(today.getDate()).padStart(2, '0')
      this.form.statDate = `${year}-${month}-${day}`
      this.open = true
      this.title = '新增使用数据'
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids[0]
      getLifeUsage(id).then(res => {
        this.form = res.data
        this.open = true
        this.title = '修改使用数据'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateLifeUsage(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addLifeUsage(this.form).then(() => {
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
        return delLifeUsage(ids.join(','))
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
  .life-usage-dialog.el-dialog {
    width: 95vw !important;
    max-width: 100vw;
  }
}
</style>
