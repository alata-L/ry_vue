<template>
  <div class="app-container">
    <div class="stats-section">
      <h2 class="section-title">重点设备统计概览</h2>
      <el-row :gutter="16" class="summary-cards">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">今年收费</div>
            <div class="card-value">{{ formatMoney(summary.chargeThisYear) }}</div>
            <div class="card-unit">元</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">去年收费</div>
            <div class="card-value">{{ formatMoney(summary.chargeLastYear) }}</div>
            <div class="card-unit">元</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">今年诊治例数</div>
            <div class="card-value">{{ summary.treatThisYear }}</div>
            <div class="card-unit">例</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">去年诊治例数</div>
            <div class="card-value">{{ summary.treatLastYear }}</div>
            <div class="card-unit">例</div>
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="16" class="chart-row">
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-title">收费趋势（今年 vs 去年）</div>
            <div ref="chartCharge" class="chart-container" />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-title">诊治例数趋势（今年 vs 去年）</div>
            <div ref="chartTreat" class="chart-container" />
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="16" class="chart-row">
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-title">科室今年收费 TOP 5</div>
            <div ref="chartDeptTop" class="chart-container chart-sm" />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-title">科室今年收费占比</div>
            <div ref="chartDeptPie" class="chart-container chart-sm" />
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="16" class="chart-row">
        <el-col :xs="24">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-title">重点设备今年收费 TOP 10</div>
            <div ref="chartTopEquip" class="chart-container chart-bar" />
          </el-card>
        </el-col>
      </el-row>
      <div class="stats-section life-section">
        <h2 class="section-title">生命支持类设备使用概览</h2>
        <el-row :gutter="16" class="chart-row">
          <el-col :xs="24" :lg="12">
            <el-card shadow="hover" class="chart-card">
              <div slot="header" class="chart-title">按设备类型使用趋势（今年按月）</div>
              <div ref="chartLifeTrend" class="chart-container chart-sm" />
            </el-card>
          </el-col>
          <el-col :xs="24" :lg="12">
            <el-card shadow="hover" class="chart-card">
              <div slot="header" class="chart-title">科室使用台数排名（今年）</div>
              <div ref="chartLifeDept" class="chart-container chart-sm" />
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons')
import resize from './dashboard/mixins/resize'
import { getKeyStatsSummary, getKeyStatsSummarySeries, getKeyStatsTopEquip } from '@/api/custom/keyStats'
import { getLifeUsageTrend, getLifeUsageByDept } from '@/api/custom/lifeStats'

export default {
  name: 'Index',
  mixins: [resize],
  data() {
    return {
      summary: {
        chargeThisMonth: 0,
        chargeLastMonth: 0,
        chargeThisYear: 0,
        chargeLastYear: 0,
        treatThisMonth: 0,
        treatLastMonth: 0,
        treatThisYear: 0,
        treatLastYear: 0
      },
      deptList: [],
      seriesData: {
        thisYear: [],
        lastYear: []
      },
      topEquipList: [],
      lifeUsageTrend: [],
      lifeDeptRank: [],
      chartCharge: null,
      chartTreat: null,
      chartDeptTop: null,
      chartDeptPie: null,
      chartTopEquip: null,
      chartLifeTrend: null,
      chartLifeDept: null
    }
  },
  mounted() {
    this.loadData()
  },
  beforeDestroy() {
    const charts = [this.chartCharge, this.chartTreat, this.chartDeptTop, this.chartDeptPie, this.chartTopEquip, this.chartLifeTrend, this.chartLifeDept]
    charts.forEach(ch => {
      if (ch) {
        ch.dispose()
      }
    })
    this.chartCharge = this.chartTreat = this.chartDeptTop = this.chartDeptPie = this.chartTopEquip = this.chartLifeTrend = this.chartLifeDept = null
  },
  methods: {
    resize() {
      const charts = [this.chartCharge, this.chartTreat, this.chartDeptTop, this.chartDeptPie, this.chartTopEquip, this.chartLifeTrend, this.chartLifeDept]
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
        getKeyStatsSummarySeries(),
        getKeyStatsTopEquip(10),
        getLifeUsageTrend(),
        getLifeUsageByDept()
      ]).then(([r0, r1, r2, r3, r4]) => {
        if (r0.status === 'fulfilled' && r0.value.data) {
          const data = r0.value.data
          this.summary = data.summary || this.summary
          this.deptList = data.deptList || []
        }
        if (r1.status === 'fulfilled' && r1.value.data) {
          const series = r1.value.data
          this.seriesData.thisYear = series.thisYear || []
          this.seriesData.lastYear = series.lastYear || []
        }
        if (r2.status === 'fulfilled' && r2.value.data) this.topEquipList = r2.value.data || []
        if (r3.status === 'fulfilled' && r3.value.data) this.lifeUsageTrend = r3.value.data || []
        if (r4.status === 'fulfilled' && r4.value.data) this.lifeDeptRank = r4.value.data || []
        this.$nextTick(() => this.initCharts())
      })
    },
    initCharts() {
      const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
      const thisYearCharge = this.buildMonthlyValues(this.seriesData.thisYear, 'totalCharge', 12)
      const lastYearCharge = this.buildMonthlyValues(this.seriesData.lastYear, 'totalCharge', 12)
      const thisYearTreat = this.buildMonthlyValues(this.seriesData.thisYear, 'totalTreat', 12)
      const lastYearTreat = this.buildMonthlyValues(this.seriesData.lastYear, 'totalTreat', 12)

      this.initChargeChart(months, thisYearCharge, lastYearCharge)
      this.initTreatChart(months, thisYearTreat, lastYearTreat)
      this.initDeptTopChart()
      this.initDeptPieChart()
      this.initTopEquipChart()
      this.initLifeTrendChart()
      this.initLifeDeptChart()
    },
    buildMonthlyValues(list, field, length) {
      const arr = new Array(length).fill(null)
      if (!list || !list.length) return arr
      for (let i = 0; i < list.length && i < length; i++) {
        const v = list[i][field]
        arr[i] = v != null ? Number(v) : 0
      }
      return arr
    },
    initChargeChart(xAxisData, thisYearData, lastYearData) {
      const el = this.$refs.chartCharge
      if (!el) return
      if (this.chartCharge) this.chartCharge.dispose()
      this.chartCharge = echarts.init(el, 'macarons')
      this.chartCharge.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          formatter: function(params) {
            if (!params || !params.length) return ''
            let s = params[0].axisValue + '<br/>'
            params.forEach(p => {
              const v = p.value != null ? Number(p.value).toLocaleString('zh-CN', { minimumFractionDigits: 0, maximumFractionDigits: 0 }) : '-'
              s += p.marker + ' ' + p.seriesName + ': ' + v + ' 元<br/>'
            })
            return s
          }
        },
        legend: { data: ['今年', '去年'], bottom: 0 },
        grid: { left: '3%', right: '4%', bottom: '15%', top: '10%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: xAxisData, axisTick: { show: false } },
        yAxis: { type: 'value', axisTick: { show: false }, axisLabel: { formatter: '{value}' } },
        series: [
          { name: '今年', type: 'line', smooth: true, data: thisYearData, itemStyle: { color: '#409EFF' }, lineStyle: { width: 2 } },
          { name: '去年', type: 'line', smooth: true, data: lastYearData, itemStyle: { color: '#E6A23C' }, lineStyle: { width: 2 } }
        ]
      })
    },
    initTreatChart(xAxisData, thisYearData, lastYearData) {
      const el = this.$refs.chartTreat
      if (!el) return
      if (this.chartTreat) this.chartTreat.dispose()
      this.chartTreat = echarts.init(el, 'macarons')
      this.chartTreat.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          formatter: function(params) {
            if (!params || !params.length) return ''
            let s = params[0].axisValue + '<br/>'
            params.forEach(p => {
              const v = p.value != null ? p.value : '-'
              s += p.marker + ' ' + p.seriesName + ': ' + v + ' 例<br/>'
            })
            return s
          }
        },
        legend: { data: ['今年', '去年'], bottom: 0 },
        grid: { left: '3%', right: '4%', bottom: '15%', top: '10%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: xAxisData, axisTick: { show: false } },
        yAxis: { type: 'value', axisTick: { show: false } },
        series: [
          { name: '今年', type: 'line', smooth: true, data: thisYearData, itemStyle: { color: '#409EFF' }, lineStyle: { width: 2 } },
          { name: '去年', type: 'line', smooth: true, data: lastYearData, itemStyle: { color: '#E6A23C' }, lineStyle: { width: 2 } }
        ]
      })
    },
    initDeptTopChart() {
      const el = this.$refs.chartDeptTop
      if (!el) return
      if (this.chartDeptTop) this.chartDeptTop.dispose()
      const list = (this.deptList || []).slice().sort((a, b) => (Number(b.chargeThisYear) || 0) - (Number(a.chargeThisYear) || 0)).slice(0, 5)
      const names = list.map(r => r.useDept || '')
      const values = list.map(r => Number(r.chargeThisYear) || 0)
      this.chartDeptTop = echarts.init(el, 'macarons')
      this.chartDeptTop.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '10%', top: '10%', containLabel: true },
        xAxis: { type: 'category', data: names, axisTick: { show: false } },
        yAxis: { type: 'value', axisTick: { show: false } },
        series: [{ type: 'bar', data: values, itemStyle: { color: '#409EFF' } }]
      })
    },
    initDeptPieChart() {
      const el = this.$refs.chartDeptPie
      if (!el) return
      if (this.chartDeptPie) this.chartDeptPie.dispose()
      const list = (this.deptList || []).filter(r => (Number(r.chargeThisYear) || 0) > 0)
      const data = list.map(r => ({ name: r.useDept || '', value: Number(r.chargeThisYear) || 0 }))
      this.chartDeptPie = echarts.init(el, 'macarons')
      this.chartDeptPie.setOption({
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left', bottom: '5%' },
        series: [{ type: 'pie', radius: '60%', data, emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.2)' } } }]
      })
    },
    initTopEquipChart() {
      const el = this.$refs.chartTopEquip
      if (!el) return
      if (this.chartTopEquip) this.chartTopEquip.dispose()
      const list = (this.topEquipList || []).slice(0, 10)
      const names = list.map(r => (r.equipDesc || r.equipNo || '').slice(0, 12))
      const values = list.map(r => Number(r.totalCharge) || 0)
      this.chartTopEquip = echarts.init(el, 'macarons')
      this.chartTopEquip.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: function(params) { const p = params[0]; const i = p.dataIndex; const row = list[i]; return (row.equipNo || '') + ' ' + (row.equipDesc || '') + '<br/>' + p.marker + ' 收费: ' + (Number(row.totalCharge) || 0).toLocaleString() + ' 元' } },
        grid: { left: '3%', right: '4%', bottom: '15%', top: '5%', containLabel: true },
        xAxis: { type: 'category', data: names, axisLabel: { rotate: 30 }, axisTick: { show: false } },
        yAxis: { type: 'value', axisTick: { show: false } },
        series: [{ type: 'bar', data: values, itemStyle: { color: '#67C23A' } }]
      })
    },
    initLifeTrendChart() {
      const el = this.$refs.chartLifeTrend
      if (!el) return
      if (this.chartLifeTrend) this.chartLifeTrend.dispose()
      const typeNames = { '1': '监护仪', '2': '输液泵', '3': '注射泵' }
      const byType = {}
      ;(this.lifeUsageTrend || []).forEach(r => {
        const t = r.equipType != null ? String(r.equipType) : ''
        if (!byType[t]) byType[t] = {}
        byType[t][r.statPeriod] = Number(r.totalCount) || 0
      })
      const periods = [...new Set((this.lifeUsageTrend || []).map(r => r.statPeriod))].sort()
      const months = periods.map(p => (p && p.length >= 7 ? p.slice(5) + '月' : p))
      const series = Object.keys(typeNames).map(t => ({
        name: typeNames[t] || t,
        type: 'line',
        smooth: true,
        data: periods.map(p => byType[t] && byType[t][p] != null ? byType[t][p] : null)
      }))
      this.chartLifeTrend = echarts.init(el, 'macarons')
      this.chartLifeTrend.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: series.map(s => s.name), bottom: 0 },
        grid: { left: '3%', right: '4%', bottom: '15%', top: '10%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: months, axisTick: { show: false } },
        yAxis: { type: 'value', axisTick: { show: false } },
        series
      })
    },
    initLifeDeptChart() {
      const el = this.$refs.chartLifeDept
      if (!el) return
      if (this.chartLifeDept) this.chartLifeDept.dispose()
      const list = (this.lifeDeptRank || []).slice().sort((a, b) => (Number(b.totalCount) || 0) - (Number(a.totalCount) || 0)).slice(0, 10)
      const names = list.map(r => r.useDept || '')
      const values = list.map(r => Number(r.totalCount) || 0)
      this.chartLifeDept = echarts.init(el, 'macarons')
      this.chartLifeDept.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '10%', top: '10%', containLabel: true },
        xAxis: { type: 'category', data: names, axisTick: { show: false } },
        yAxis: { type: 'value', axisTick: { show: false } },
        series: [{ type: 'bar', data: values, itemStyle: { color: '#909399' } }]
      })
    }
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
.chart-container {
  height: 350px;
  width: 100%;
}
.chart-container.chart-sm {
  height: 280px;
}
.chart-container.chart-bar {
  height: 320px;
}
.life-section {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
