<template>
  <div class="app-container">
    <el-page-header @back="goBack" :content="pageTitle" class="page-header" />
    <div v-if="!equipId" class="empty-tip">请从科室统计页选择设备进入</div>
    <template v-else>
      <div class="filter-row">
        <div class="year-switch">
          <span class="switch-label">年份：</span>
          <el-select v-model="selectedYear" @change="onYearChange" size="small" style="width: 100px">
            <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
          </el-select>
        </div>
        <div class="x-axis-switch">
          <span class="switch-label">维度：</span>
          <el-radio-group v-model="activeTab" @change="onXAxisChange" size="small">
            <el-radio-button label="day">按天</el-radio-button>
            <el-radio-button label="week">按周</el-radio-button>
          </el-radio-group>
        </div>
      </div>
      <el-table :data="seriesList" border size="small" class="data-table">
        <el-table-column :label="periodColumnLabel" prop="period" min-width="120">
          <template slot-scope="scope">{{ formatPeriod(scope.row.period) }}</template>
        </el-table-column>
        <el-table-column label="工作时间" prop="workHours" width="120" align="right">
          <template slot-scope="scope">{{ formatNum(scope.row.workHours) }}</template>
        </el-table-column>
        <el-table-column label="诊治例数" prop="treatCount" width="120" align="right" />
        <el-table-column label="收费价格" width="120" align="right">
          <template slot-scope="scope">{{ formatMoney(scope.row.totalCharge) }}</template>
        </el-table-column>
        <el-table-column label="每例收费价格" width="130" align="right">
          <template slot-scope="scope">{{ formatMoney(scope.row.avgChargePerCase) }}</template>
        </el-table-column>
      </el-table>
    </template>
  </div>
</template>

<script>
import { getKeyStatsEquipSeries } from '@/api/custom/keyStats'
import { getKeyEquip } from '@/api/custom/keyEquip'

export default {
  name: 'KeyStatsEquipDetail',
  data() {
    const currentYear = new Date().getFullYear()
    const yearOptions = []
    for (let y = currentYear; y >= currentYear - 10; y--) yearOptions.push(y)
    return {
      equipId: null,
      useDept: '',
      equipInfo: null,
      activeTab: 'day',
      selectedYear: currentYear,
      yearOptions,
      seriesList: []
    }
  },
  computed: {
    periodColumnLabel() {
      return this.activeTab === 'week' ? '周次' : '日期'
    },
    yearLabel() {
      const now = new Date().getFullYear()
      return this.selectedYear === now ? '今年' : this.selectedYear + '年'
    },
    pageTitle() {
      if (this.equipInfo) {
        const name = (this.equipInfo.equipNo || '') + (this.equipInfo.equipDesc ? ' ' + this.equipInfo.equipDesc : '')
        return name + ' - 统计明细（' + this.yearLabel + '）'
      }
      return '设备统计明细（' + this.yearLabel + '）'
    }
  },
  watch: {
    '$route.query.equipId': {
      immediate: true,
      handler(val) {
        this.equipId = val ? Number(val) : null
        this.useDept = this.$route.query.useDept || ''
        if (this.equipId) {
          this.loadEquipInfo()
          this.loadSeries()
        }
      }
    }
  },
  methods: {
    formatPeriod(period) {
      if (period == null || period === '') return '-'
      const s = String(period)
      if (this.activeTab === 'week') {
        const parts = s.split('-')
        if (parts.length >= 2) return parts[0] + '年第' + parts[1] + '周'
        return s
      }
      return s
    },
    formatMoney(v) {
      if (v == null || v === '') return '-'
      const n = Number(v)
      return isNaN(n) ? '-' : n.toFixed(2)
    },
    formatNum(v) {
      if (v == null || v === '') return '-'
      const n = Number(v)
      return isNaN(n) ? '-' : String(n)
    },
    getYearRange(year) {
      const y = Number(year) || new Date().getFullYear()
      const beginTime = `${y}-01-01`
      const now = new Date()
      const currentYear = now.getFullYear()
      let endTime
      if (y === currentYear) {
        const m = String(now.getMonth() + 1).padStart(2, '0')
        const d = String(now.getDate()).padStart(2, '0')
        endTime = `${y}-${m}-${d}`
      } else {
        endTime = `${y}-12-31`
      }
      return { beginTime, endTime }
    },
    goBack() {
      if (this.useDept) {
        this.$router.push({ path: '/custom/keyStats/dept', query: { useDept: this.useDept } })
      } else {
        this.$router.push({ path: '/custom/keyStats' })
      }
    },
    onXAxisChange() {
      this.loadSeries()
    },
    onYearChange() {
      this.loadSeries()
    },
    loadEquipInfo() {
      getKeyEquip(this.equipId).then(res => {
        this.equipInfo = res.data || null
      })
    },
    loadSeries() {
      const { beginTime, endTime } = this.getYearRange(this.selectedYear)
      getKeyStatsEquipSeries(this.equipId, {
        groupBy: this.activeTab,
        beginTime,
        endTime
      }).then(res => {
        this.seriesList = res.data || []
      })
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
.filter-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 24px;
  margin-bottom: 16px;
}
.x-axis-switch,
.year-switch {
  display: flex;
  align-items: center;
  .switch-label {
    margin-right: 8px;
    color: #606266;
    font-size: 14px;
  }
}
.data-table {
  margin-top: 0;
}
</style>
