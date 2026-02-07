<template>
  <div class="app-container">
    <el-page-header @back="goBack" :content="pageTitle" class="page-header" />
    <div v-if="!useDept" class="empty-tip">请从统计页选择科室进入</div>
    <template v-else>
      <!-- 三类设备总台数汇总 -->
      <div class="stats-section">
        <h3 class="section-title">设备总台数</h3>
        <el-row :gutter="16" class="summary-cards">
          <el-col :span="6">
            <el-card shadow="hover" class="summary-card">
              <div class="card-label">监护仪</div>
              <div class="card-value">{{ summary.monitor || 0 }}</div>
              <div class="card-unit">台</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="summary-card">
              <div class="card-label">输液泵</div>
              <div class="card-value">{{ summary.infusionPump || 0 }}</div>
              <div class="card-unit">台</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="summary-card">
              <div class="card-label">注射泵</div>
              <div class="card-value">{{ summary.syringePump || 0 }}</div>
              <div class="card-unit">台</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="summary-card total">
              <div class="card-label">合计</div>
              <div class="card-value">{{ summary.total || 0 }}</div>
              <div class="card-unit">台</div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <!-- 设备使用数据：三个表格 -->
      <div class="stats-section usage-section">
        <h3 class="section-title">设备使用数据</h3>

        <h4 class="table-title">最近一月的每日使用情况</h4>
        <el-table :data="usageDaily" border size="small" class="usage-table">
          <el-table-column label="日期" prop="statPeriod" min-width="120" />
          <el-table-column label="设备类型" prop="equipType" width="100">
            <template slot-scope="scope">
              {{ equipTypeLabel(scope.row.equipType) }}
            </template>
          </el-table-column>
          <el-table-column label="使用台数" prop="totalCount" width="120" align="right" />
        </el-table>

        <h4 class="table-title">最近一年的每月使用情况</h4>
        <el-table :data="usageMonthly" border size="small" class="usage-table">
          <el-table-column label="月份" prop="statPeriod" min-width="120" />
          <el-table-column label="设备类型" prop="equipType" width="100">
            <template slot-scope="scope">
              {{ equipTypeLabel(scope.row.equipType) }}
            </template>
          </el-table-column>
          <el-table-column label="使用台数" prop="totalCount" width="120" align="right" />
        </el-table>
      </div>
    </template>
  </div>
</template>

<script>
import { statsDeptType } from '@/api/custom/lifeEquip'
import { sumLifeUsage } from '@/api/custom/lifeUsage'

const EQUIP_TYPE_MAP = { '1': '监护仪', '2': '输液泵', '3': '注射泵' }

function dateStr(d) {
  return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0')
}

export default {
  name: 'LifeStatsDeptDetail',
  data() {
    return {
      useDept: '',
      summary: { monitor: 0, infusionPump: 0, syringePump: 0, total: 0 },
      usageDaily: [],
      usageMonthly: []
    }
  },
  computed: {
    pageTitle() {
      return this.useDept ? `${this.useDept} - 通用设备统计` : '科室设备统计'
    }
  },
  watch: {
    '$route.query.useDept': {
      immediate: true,
      handler(val) {
        this.useDept = val || ''
        if (this.useDept) {
          this.loadDeptSummary()
          this.loadUsageAll()
        }
      }
    }
  },
  methods: {
    goBack() {
      this.$router.push({ path: '/custom/lifeStats' })
    },
    equipTypeLabel(type) {
      return EQUIP_TYPE_MAP[String(type)] || type || '-'
    },
    loadDeptSummary() {
      statsDeptType().then(res => {
        const list = (res.data || []).filter(r => r.useDept === this.useDept)
        const monitor = list.filter(r => r.equipType === '1').reduce((s, r) => s + (Number(r.cnt) || 0), 0)
        const infusionPump = list.filter(r => r.equipType === '2').reduce((s, r) => s + (Number(r.cnt) || 0), 0)
        const syringePump = list.filter(r => r.equipType === '3').reduce((s, r) => s + (Number(r.cnt) || 0), 0)
        this.summary = {
          monitor,
          infusionPump,
          syringePump,
          total: monitor + infusionPump + syringePump
        }
      })
    },
    loadUsageAll() {
      const end = new Date()
      const endTime = dateStr(end)

      const startMonth = new Date()
      startMonth.setMonth(startMonth.getMonth() - 1)
      sumLifeUsage({
        useDept: this.useDept,
        beginTime: dateStr(startMonth),
        endTime,
        groupBy: 'day'
      }).then(res => { this.usageDaily = res.data || [] })

      const startYear = new Date()
      startYear.setFullYear(startYear.getFullYear() - 1)
      sumLifeUsage({
        useDept: this.useDept,
        beginTime: dateStr(startYear),
        endTime,
        groupBy: 'month'
      }).then(res => { this.usageMonthly = res.data || [] })
    }
  }
}
</script>

<style lang="scss" scoped>
.page-header {
  margin-bottom: 20px;
}
.empty-tip {
  color: #909399;
  padding: 40px 0;
  text-align: center;
}
.stats-section {
  padding: 0 4px;
  margin-bottom: 24px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: #303133;
}
.summary-cards {
  margin-bottom: 8px;
}
.summary-card {
  text-align: center;
  .card-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }
  .card-value {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
  .card-unit {
    font-size: 12px;
    color: #909399;
  }
  &.total .card-value {
    color: #409eff;
  }
}
.table-title {
  font-size: 14px;
  font-weight: 600;
  margin: 24px 0 12px 0;
  color: #606266;
}
.table-title:first-of-type {
  margin-top: 0;
}
.usage-table {
  margin-bottom: 8px;
}
</style>
