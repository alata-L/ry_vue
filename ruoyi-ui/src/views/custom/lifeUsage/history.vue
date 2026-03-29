<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="上报日期">
        <el-date-picker v-model="reportDateRange" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期"
          value-format="yyyy-MM-dd" style="width:240px" />
      </el-form-item>
      <el-form-item label="设备类型" prop="equipType">
        <el-select v-model="queryParams.equipType" placeholder="请选择" clearable style="width:140px">
          <el-option v-for="dict in dict.type.cst_life_equip_type" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="使用科室" prop="useDept">
        <el-select v-model="queryParams.useDept" placeholder="请选择" clearable filterable allow-create style="width:180px">
          <el-option v-for="dict in dict.type.cst_use_dept" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="修改人" prop="changeBy">
        <el-input v-model="queryParams.changeBy" placeholder="登录名" clearable style="width:120px" />
      </el-form-item>
      <el-form-item label="修改时间">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="-" start-placeholder="开始" end-placeholder="结束"
          value-format="yyyy-MM-dd" style="width:240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list">
      <el-table-column label="上报日期" align="center" width="110">
        <template slot-scope="scope">
          <span>{{ reportDateFromRow(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="上报人" align="center" width="120" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ reporterNickHist(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="设备类型" align="center" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.cst_life_equip_type" :value="snapLife(scope.row).equipType" />
        </template>
      </el-table-column>
      <el-table-column label="使用科室" align="center" min-width="110" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ useDeptHist(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="当日使用台数" align="center" width="110">
        <template slot-scope="scope">
          <span>{{ snapLife(scope.row).usedCount != null && snapLife(scope.row).usedCount !== '' ? snapLife(scope.row).usedCount : '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="修改时间" align="center" prop="changeTime" width="160" />
      <el-table-column label="修改人" align="center" prop="changeByNick" min-width="100" show-overflow-tooltip />
      <el-table-column label="类型" align="center" prop="operType" width="90" />
      <el-table-column label="操作" align="center" width="100">
        <template slot-scope="scope">
          <el-button type="text" size="mini" icon="el-icon-view" @click="showDetail(scope.row)">明细</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="变更明细" :visible.sync="detailOpen" width="820px" append-to-body custom-class="hist-detail-dialog" @closed="detailRow = null">
      <div v-if="detailRow" class="hist-detail-wrap">
        <p class="hist-detail-hint">以下为修改前后对比，<span class="hist-pink-mark">粉红色</span>行为有变更的字段。</p>
        <el-table :data="lifeDetailCompareRows" border size="small" class="hist-compare-table" :row-class-name="lifeDetailRowClass">
          <el-table-column prop="label" label="项目" width="130" align="center" />
          <el-table-column label="修改前" min-width="160" align="center">
            <template slot-scope="scope">
              <div class="hist-cell-inner">
                <dict-tag v-if="scope.row.key === 'equipType'" :options="dict.type.cst_life_equip_type" :value="scope.row.before" />
                <span v-else>{{ formatLifeValue(scope.row.key, scope.row.before) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="修改后" min-width="160" align="center">
            <template slot-scope="scope">
              <div class="hist-cell-inner">
                <dict-tag v-if="scope.row.key === 'equipType'" :options="dict.type.cst_life_equip_type" :value="scope.row.after" />
                <span v-else>{{ formatLifeValue(scope.row.key, scope.row.after) }}</span>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-collapse v-if="lifeDetailRawFallback" class="hist-raw-collapse">
          <el-collapse-item title="原始数据（解析失败时）" name="raw">
            <pre class="hist-json">{{ formatJson(detailRow.beforeJson) }}</pre>
            <pre class="hist-json">{{ formatJson(detailRow.afterJson) }}</pre>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listLifeUsageHistory } from '@/api/custom/lifeUsage'

export default {
  name: 'LifeUsageHistory',
  dicts: ['cst_use_dept', 'cst_life_equip_type'],
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      list: [],
      dateRange: [],
      reportDateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        changeBy: undefined,
        usageId: undefined,
        equipType: undefined,
        useDept: undefined
      },
      detailOpen: false,
      detailRow: null,
      /** 与系统其它列表一致：created 已拉数；keep-alive 下再次进入走 activated，避免重复首屏请求 */
      skipActivatedRefresh: true
    }
  },
  computed: {
    lifeDetailCompareRows() {
      if (!this.detailRow) {
        return []
      }
      return this.buildLifeCompareRows(this.detailRow.beforeJson, this.detailRow.afterJson)
    },
    lifeDetailRawFallback() {
      return this.detailRow && this.lifeDetailCompareRows.length === 0
    }
  },
  watch: {
    /** 侧栏进入全量列表时 URL 无 usageId，须与路由同步，否则会沿用上次「单条上报修改记录」跳转时的 usageId 过滤 */
    '$route'(to) {
      if (!to.path.includes('lifeUsageHistory')) {
        return
      }
      this.applyUsageIdFromRoute(to)
      this.getList()
    }
  },
  created() {
    this.applyUsageIdFromRoute()
    this.getList()
  },
  activated() {
    if (this.skipActivatedRefresh) {
      this.skipActivatedRefresh = false
      return
    }
    this.applyUsageIdFromRoute()
    this.getList()
  },
  methods: {
    applyUsageIdFromRoute(route) {
      const r = route || this.$route
      const q = r.query && r.query.usageId
      this.queryParams.usageId = q != null && q !== '' ? q : undefined
    },
    getList() {
      this.loading = true
      const p = { ...this.queryParams }
      if (this.reportDateRange && this.reportDateRange.length === 2) {
        p.reportBegin = this.reportDateRange[0]
        p.reportEnd = this.reportDateRange[1]
      } else {
        p.reportBegin = undefined
        p.reportEnd = undefined
      }
      listLifeUsageHistory(this.addDateRange(p, this.dateRange))
        .then(res => {
          this.list = res.rows
          this.total = res.total
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    /** 后端写入 params.reporterNick（与上报列表 params.createByNickName 同理） */
    reporterNickHist(row) {
      const p = row.params || {}
      return p.reporterNick != null && p.reporterNick !== '' ? p.reporterNick : '—'
    },
    /** 快照无 useDept 时后端 params.useDeptDisplay 回填主表科室 */
    useDeptHist(row) {
      const j = this.snapLife(row).useDept
      if (j != null && j !== '') return j
      const p = row.params || {}
      return p.useDeptDisplay != null && p.useDeptDisplay !== '' ? p.useDeptDisplay : '—'
    },
    /** 与通用设备上报列表「上报日期」一致：取快照 JSON 中 statDate */
    reportDateFromRow(row) {
      const o = this.snapLife(row)
      return o.statDate || '—'
    },
    /** 修改后优先，否则修改前（与业务快照字段一致） */
    snapLife(row) {
      const s = row.afterJson || row.beforeJson
      if (!s) return {}
      try {
        return JSON.parse(s)
      } catch (e) {
        return {}
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.reportDateRange = []
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        changeBy: undefined,
        usageId: undefined,
        equipType: undefined,
        useDept: undefined
      }
      this.resetForm('queryForm')
      if (this.$route.query && this.$route.query.usageId != null && this.$route.query.usageId !== '') {
        this.$router.replace({ path: this.$route.path, query: {} })
      } else {
        this.handleQuery()
      }
    },
    showDetail(row) {
      this.detailRow = row
      this.detailOpen = true
    },
    lifeDetailRowClass({ row }) {
      return row.diff ? 'hist-diff-row' : ''
    },
    buildLifeCompareRows(beforeJson, afterJson) {
      const before = this.parseSnapObj(beforeJson)
      const after = this.parseSnapObj(afterJson)
      if (!before && !after) {
        return []
      }
      const a = before || {}
      const b = after || {}
      const keys = new Set([...Object.keys(a), ...Object.keys(b)])
      const order = ['statDate', 'equipType', 'useDept', 'usedCount', 'remark', 'reportBy']
      const labelMap = {
        statDate: '上报日期',
        equipType: '设备类型',
        useDept: '使用科室',
        usedCount: '当日使用台数',
        remark: '备注',
        reportBy: '上报人（登录名）'
      }
      const sorted = [...keys].filter(k => k !== '__proto__' && k !== 'id').sort((x, y) => {
        const ix = order.indexOf(x)
        const iy = order.indexOf(y)
        if (ix === -1 && iy === -1) return x.localeCompare(y)
        if (ix === -1) return 1
        if (iy === -1) return -1
        return ix - iy
      })
      return sorted.map(key => {
        const va = a[key]
        const vb = b[key]
        const diff = this.snapValuesDiffer(va, vb)
        return { key, label: labelMap[key] || key, before: va, after: vb, diff }
      })
    },
    parseSnapObj(s) {
      if (!s || typeof s !== 'string') {
        return null
      }
      try {
        return JSON.parse(s)
      } catch (e) {
        return null
      }
    },
    snapValuesDiffer(va, vb) {
      return JSON.stringify(va) !== JSON.stringify(vb)
    },
    formatLifeValue(key, val) {
      if (val === null || val === undefined || val === '') {
        return '—'
      }
      if (key === 'usedCount' || key === 'id') {
        return String(val)
      }
      return String(val)
    },
    formatJson(s) {
      if (!s) return '—'
      try {
        return JSON.stringify(JSON.parse(s), null, 2)
      } catch (e) {
        return s
      }
    }
  }
}
</script>

<style scoped>
.hist-section-title {
  font-weight: 600;
  margin: 8px 0 4px;
}
.hist-json {
  max-height: 240px;
  overflow: auto;
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.45;
}
.hist-detail-hint {
  font-size: 13px;
  color: #606266;
  margin: 0 0 12px;
}
.hist-pink-mark {
  color: #e91e8c;
  font-weight: 600;
}
.hist-compare-table {
  width: 100%;
}
.hist-cell-inner {
  padding: 4px 0;
  min-height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
}
.hist-raw-collapse {
  margin-top: 12px;
}
</style>

<style>
/* el-table 行类名在子组件内，需非 scoped 才能命中 */
.hist-compare-table .el-table__body tr.hist-diff-row > td {
  background-color: #ffe4ec !important;
}
.hist-compare-table .el-table__body tr.hist-diff-row:hover > td {
  background-color: #ffd6e5 !important;
}
</style>
