<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="使用科室" prop="useDept">
        <el-select v-model="queryParams.useDept" placeholder="请选择使用科室" clearable filterable allow-create>
          <el-option v-for="dict in dict.type.cst_use_dept" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备类型" prop="equipType">
        <el-select v-model="queryParams.equipType" placeholder="请选择设备类型" clearable>
          <el-option v-for="dict in dict.type.cst_life_equip_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['custom:lifeEquip:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['custom:lifeEquip:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['custom:lifeEquip:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-dropdown @command="handleExportByType" v-hasPermi="['custom:lifeEquip:export']">
          <el-button type="warning" plain icon="el-icon-download" size="mini">导出<i class="el-icon-arrow-down el-icon--right"></i></el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="1">导出监护仪</el-dropdown-item>
            <el-dropdown-item command="2">导出输液泵</el-dropdown-item>
            <el-dropdown-item command="3">导出注射泵</el-dropdown-item>
            <el-dropdown-item command="all">导出全部</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="设备编号" align="center" prop="equipNo" min-width="100" />
      <el-table-column label="设备描述" align="center" prop="equipDesc" min-width="120" show-overflow-tooltip />
      <el-table-column label="型号" align="center" prop="model" min-width="100" />
      <el-table-column label="累计购置价值" align="center" prop="totalValue" width="110" />
      <el-table-column label="资本化日期" align="center" prop="capDate" width="110" />
      <el-table-column label="使用科室" align="center" prop="useDept" min-width="120" show-overflow-tooltip />
      <el-table-column label="设备类型" align="center" prop="equipType" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.cst_life_equip_type" :value="scope.row.equipType"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="140" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['custom:lifeEquip:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['custom:lifeEquip:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="640px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="使用科室" prop="useDept">
              <el-select v-model="form.useDept" placeholder="请选择或输入使用科室" filterable allow-create style="width:100%">
                <el-option v-for="dict in dict.type.cst_use_dept" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备描述" prop="equipDesc">
              <el-input v-model="form.equipDesc" placeholder="请输入设备描述" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备编号" prop="equipNo">
              <el-input v-model="form.equipNo" placeholder="请输入设备编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="设备类型" prop="equipType">
              <el-select v-model="form.equipType" placeholder="请选择设备类型" style="width:100%">
                <el-option v-for="dict in dict.type.cst_life_equip_type" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input v-model="form.model" placeholder="请输入型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="累计购置价值" prop="totalValue">
              <el-input-number v-model="form.totalValue" :min="0" :precision="2" controls-position="right" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资本化日期" prop="capDate">
              <el-date-picker v-model="form.capDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width:100%"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
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
import { listLifeEquip, getLifeEquip, addLifeEquip, updateLifeEquip, delLifeEquip, exportLifeEquipByType, exportLifeEquip } from '@/api/custom/lifeEquip'

export default {
  name: 'LifeEquip',
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
      title: '',
      open: false,
      queryParams: { pageNum: 1, pageSize: 10, useDept: undefined, equipType: undefined },
      form: {},
      rules: {
        equipNo: [{ required: true, message: '设备编号不能为空', trigger: 'blur' }],
        equipType: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
        useDept: [{ required: true, message: '请选择使用科室', trigger: 'change' }],
        equipDesc: [{ required: true, message: '请输入设备描述', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listLifeEquip(this.queryParams).then(res => {
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
      this.form = { id: undefined, equipNo: undefined, subAssetNo: undefined, equipDesc: undefined, model: undefined, totalValue: undefined, capDate: undefined, useDept: undefined, equipType: undefined, remark: undefined }
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
      this.open = true
      this.title = '新增通用设备'
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids[0]
      getLifeEquip(id).then(res => {
        this.form = res.data
        this.open = true
        this.title = '修改通用设备'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateLifeEquip(this.form).then(() => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addLifeEquip(this.form).then(() => {
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
      this.$modal.confirm('是否确认删除选中的设备？').then(() => {
        return delLifeEquip(ids.join(','))
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExportByType(command) {
      if (command === 'all') {
        this.download('custom/lifeEquip/export', { ...this.queryParams }, `通用设备_${new Date().getTime()}.xlsx`)
      } else {
        this.download('custom/lifeEquip/exportByType', { equipType: command }, `通用设备_${command}_${new Date().getTime()}.xlsx`)
      }
    }
  }
}
</script>
