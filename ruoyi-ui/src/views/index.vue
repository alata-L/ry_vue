<template>
  <div class="app-container">
    <div class="stats-section">
      <h2 class="section-title">首页-全院重点设备装备情况</h2>
      <el-row :gutter="16" class="summary-cards">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">单价≥50万元设备台数</div>
            <div class="card-value">{{ summary.equipCount50 || 0 }}</div>
            <div class="card-unit">台</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">单价≥50万元设备总金额</div>
            <div class="card-value">{{ formatMoney(summary.totalValue50) }}</div>
            <div class="card-unit">元</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">单价≥100万元设备台数</div>
            <div class="card-value">{{ summary.equipCount100 || 0 }}</div>
            <div class="card-unit">台</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">单价≥100万元设备总金额</div>
            <div class="card-value">{{ formatMoney(summary.totalValue100) }}</div>
            <div class="card-unit">元</div>
          </el-card>
        </el-col>
      </el-row>
      <!-- 左侧：单价50万元以上TOP10 -->
      <el-row :gutter="16" class="chart-row">
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-title">收费对比（今年 vs 去年）<br/>单价50万元以上TOP10（按设备名称）</div>
            <div ref="chartCharge50" class="chart-container" />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-title">收费对比（今年 vs 去年）<br/>单价100万元以上TOP10（按设备名称）</div>
            <div ref="chartCharge100" class="chart-container" />
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="16" class="chart-row">
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-title">工作时长对比（今年 vs 去年）<br/>单价50万元以上TOP10（按设备名称）</div>
            <div ref="chartWorkHours50" class="chart-container" />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-title">工作时长对比（今年 vs 去年）<br/>单价100万元以上TOP10（按设备名称）</div>
            <div ref="chartWorkHours100" class="chart-container" />
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="16" class="chart-row">
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-title">诊疗例数对比（今年 vs 去年）<br/>单价50万元以上TOP10（按设备名称）</div>
            <div ref="chartTreat50" class="chart-container" />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-title">诊疗例数对比（今年 vs 去年）<br/>单价100万元以上TOP10（按设备名称）</div>
            <div ref="chartTreat100" class="chart-container" />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons')
import resize from './dashboard/mixins/resize'
import { getKeyStatsSummary, getTopEquipChargeYearCompare } from '@/api/custom/keyStats'

export default {
  name: 'Index',
  mixins: [resize],
  data() {
    return {
      summary: {
        equipCount50: 0,
        totalValue50: 0,
        equipCount100: 0,
        totalValue100: 0
      },
      /** ≥50万 / ≥100万：TOP10 设备今年/去年（收费、工作时长、诊疗例数，柱状图共用同一批数据） */
      compare50: [],
      compare100: [],
      chartCharge50: null,
      chartCharge100: null,
      chartWorkHours50: null,
      chartWorkHours100: null,
      chartTreat50: null,
      chartTreat100: null
    }
  },
  mounted() {
    this.loadData()
  },
  beforeDestroy() {
    const charts = [this.chartCharge50, this.chartCharge100, this.chartWorkHours50, this.chartWorkHours100, this.chartTreat50, this.chartTreat100]
    charts.forEach(ch => {
      if (ch) {
        ch.dispose()
      }
    })
    this.chartCharge50 = this.chartCharge100 = this.chartWorkHours50 = this.chartWorkHours100 = this.chartTreat50 = this.chartTreat100 = null
  },
  methods: {
    resize() {
      const charts = [this.chartCharge50, this.chartCharge100, this.chartWorkHours50, this.chartWorkHours100, this.chartTreat50, this.chartTreat100]
      charts.forEach(ch => ch && ch.resize())
    },
    formatMoney(v) {
      if (v == null || v === '') return '0'
      const n = Number(v)
      return isNaN(n) ? '0' : n.toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 })
    },
    loadData() {
      Promise.allSettled([
        getKeyStatsSummary(),
        getTopEquipChargeYearCompare(500000, 10),
        getTopEquipChargeYearCompare(1000000, 10)
      ]).then(([r0, r1, r2]) => {
        if (r0.status === 'fulfilled' && r0.value.data) {
          const data = r0.value.data
          this.summary = data.summary || this.summary
        }
        if (r1.status === 'fulfilled' && r1.value.data) {
          this.compare50 = Array.isArray(r1.value.data) ? r1.value.data : []
        }
        if (r2.status === 'fulfilled' && r2.value.data) {
          this.compare100 = Array.isArray(r2.value.data) ? r2.value.data : []
        }
        this.$nextTick(() => this.initCharts())
      })
    },
    initCharts() {
      this.initBarCompareByEquip(this.compare50, 'chartCharge50', 'chargeLastYear', 'chargeThisYear', '元')
      this.initBarCompareByEquip(this.compare100, 'chartCharge100', 'chargeLastYear', 'chargeThisYear', '元')
      this.initBarCompareByEquip(this.compare50, 'chartWorkHours50', 'workHoursLastYear', 'workHoursThisYear', '小时')
      this.initBarCompareByEquip(this.compare100, 'chartWorkHours100', 'workHoursLastYear', 'workHoursThisYear', '小时')
      this.initBarCompareByEquip(this.compare50, 'chartTreat50', 'treatLastYear', 'treatThisYear', '例')
      this.initBarCompareByEquip(this.compare100, 'chartTreat100', 'treatLastYear', 'treatThisYear', '例')
    },
    /** TOP10 设备名称横轴，分组柱（左去年、右今年）；unit 为 tooltip 后缀 */
    initBarCompareByEquip(rows, refName, lastField, thisField, unit) {
      const el = this.$refs[refName]
      if (!el) return
      if (this[refName]) this[refName].dispose()
      this[refName] = echarts.init(el, 'macarons')
      const list = rows || []
      const categories = list.map(r => {
        const d = r.equipDesc != null ? String(r.equipDesc).trim() : ''
        if (d) return d
        const n = r.equipNo != null ? String(r.equipNo).trim() : ''
        return n || '—'
      })
      const lastYearData = list.map(r => {
        const v = r[lastField]
        return v != null ? Number(v) : 0
      })
      const thisYearData = list.map(r => {
        const v = r[thisField]
        return v != null ? Number(v) : 0
      })
      this[refName].setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: function(params) {
            if (!params || !params.length) return ''
            let s = params[0].axisValue + '<br/>'
            params.forEach(p => {
              const v = p.value != null ? Number(p.value).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 }) : '-'
              s += p.marker + ' ' + p.seriesName + ': ' + v + ' ' + unit + '<br/>'
            })
            return s
          }
        },
        legend: { data: ['去年', '今年'], bottom: 0 },
        grid: { left: '3%', right: '4%', bottom: '18%', top: '10%', containLabel: true },
        xAxis: {
          type: 'category',
          data: categories,
          axisTick: { show: false },
          axisLabel: { interval: 0, rotate: 28, fontSize: 11 }
        },
        yAxis: { type: 'value', axisTick: { show: false }, axisLabel: { formatter: '{value}' } },
        series: [
          { name: '去年', type: 'bar', data: lastYearData, barMaxWidth: 32, itemStyle: { color: '#E6A23C' } },
          { name: '今年', type: 'bar', data: thisYearData, barMaxWidth: 32, itemStyle: { color: '#409EFF' } }
        ]
      })
    },
  }
}
</script>

<style scoped lang="scss">
.stats-section {
  padding: 0 4px;
}
.section-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: #303133;
}
.summary-cards {
  margin-bottom: 20px;
}
.summary-card {
  text-align: center;
  margin-bottom: 16px;
  .card-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }
  .card-value {
    font-size: 22px;
    font-weight: 600;
    color: #303133;
  }
  .card-unit {
    font-size: 12px;
    color: #909399;
  }
}
.chart-row {
  margin-bottom: 16px;
}
.chart-card {
  margin-bottom: 16px;
}
.chart-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}
.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.life-section {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
.chart-container {
  height: 350px;
  width: 100%;
}
.chart-title {
  line-height: 1.6;
}
</style>
