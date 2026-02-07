<template>
  <div class="app-container">
    <el-page-header @back="goBack" :content="pageTitle" class="page-header" />
    <div v-if="!useDept" class="empty-tip">请从统计页选择科室进入</div>
    <template v-else>
      <!-- 三类设备总台数汇总 -->
      <div class="stats-section">
        <h3 class="section-title">设备总台数</h3>
        <el-row :gutter="16">
          <el-col :span="8">
            <div ref="summaryPieChart" class="chart-container chart-pie"></div>
          </el-col>
          <el-col :span="16">
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
          </el-col>
        </el-row>
      </div>
      <!-- 设备使用数据：标签页展示 -->
      <div class="stats-section usage-section">
        <h3 class="section-title">设备使用数据</h3>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="监护仪" name="1">
            <h4 class="table-title">最近一年的每月使用情况</h4>
            <el-row :gutter="16">
              <el-col :span="16">
                <div ref="monthlyChart1" class="chart-container"></div>
              </el-col>
              <el-col :span="8">
                <el-table :data="getUsageByType(usageMonthly, '1')" border size="small" class="usage-table">
                  <el-table-column label="月份" prop="statPeriod" min-width="80" />
                  <el-table-column label="使用台数" prop="totalCount" width="80" align="right" />
                </el-table>
              </el-col>
            </el-row>

            <div class="table-header">
              <h4 class="table-title">每日使用情况</h4>
              <el-select v-model="selectedMonth" placeholder="选择月份" size="small" style="width:160px" @change="handleMonthChange">
                <el-option v-for="month in availableMonths" :key="month.value" :label="month.label" :value="month.value"></el-option>
              </el-select>
            </div>
            <el-row :gutter="16">
              <el-col :span="16">
                <div ref="dailyChart1" class="chart-container"></div>
              </el-col>
              <el-col :span="8">
                <el-table :data="getUsageByType(usageDaily, '1')" border size="small" class="usage-table">
                  <el-table-column label="日期" prop="statPeriod" min-width="80" />
                  <el-table-column label="使用台数" prop="totalCount" width="80" align="right" />
                </el-table>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="输液泵" name="2">
            <h4 class="table-title">最近一年的每月使用情况</h4>
            <el-row :gutter="16">
              <el-col :span="16">
                <div ref="monthlyChart2" class="chart-container"></div>
              </el-col>
              <el-col :span="8">
                <el-table :data="getUsageByType(usageMonthly, '2')" border size="small" class="usage-table">
                  <el-table-column label="月份" prop="statPeriod" min-width="80" />
                  <el-table-column label="使用台数" prop="totalCount" width="80" align="right" />
                </el-table>
              </el-col>
            </el-row>

            <div class="table-header">
              <h4 class="table-title">每日使用情况</h4>
              <el-select v-model="selectedMonth" placeholder="选择月份" size="small" style="width:160px" @change="handleMonthChange">
                <el-option v-for="month in availableMonths" :key="month.value" :label="month.label" :value="month.value"></el-option>
              </el-select>
            </div>
            <el-row :gutter="16">
              <el-col :span="16">
                <div ref="dailyChart2" class="chart-container"></div>
              </el-col>
              <el-col :span="8">
                <el-table :data="getUsageByType(usageDaily, '2')" border size="small" class="usage-table">
                  <el-table-column label="日期" prop="statPeriod" min-width="80" />
                  <el-table-column label="使用台数" prop="totalCount" width="80" align="right" />
                </el-table>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="注射泵" name="3">
            <h4 class="table-title">最近一年的每月使用情况</h4>
            <el-row :gutter="16">
              <el-col :span="16">
                <div ref="monthlyChart3" class="chart-container"></div>
              </el-col>
              <el-col :span="8">
                <el-table :data="getUsageByType(usageMonthly, '3')" border size="small" class="usage-table">
                  <el-table-column label="月份" prop="statPeriod" min-width="80" />
                  <el-table-column label="使用台数" prop="totalCount" width="80" align="right" />
                </el-table>
              </el-col>
            </el-row>

            <div class="table-header">
              <h4 class="table-title">每日使用情况</h4>
              <el-select v-model="selectedMonth" placeholder="选择月份" size="small" style="width:160px" @change="handleMonthChange">
                <el-option v-for="month in availableMonths" :key="month.value" :label="month.label" :value="month.value"></el-option>
              </el-select>
            </div>
            <el-row :gutter="16">
              <el-col :span="16">
                <div ref="dailyChart3" class="chart-container"></div>
              </el-col>
              <el-col :span="8">
                <el-table :data="getUsageByType(usageDaily, '3')" border size="small" class="usage-table">
                  <el-table-column label="日期" prop="statPeriod" min-width="80" />
                  <el-table-column label="使用台数" prop="totalCount" width="80" align="right" />
                </el-table>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </div>
    </template>
  </div>
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons')
import { statsDeptType } from '@/api/custom/lifeEquip'
import { sumLifeUsage } from '@/api/custom/lifeUsage'

const EQUIP_TYPE_MAP = { '1': '监护仪', '2': '输液泵', '3': '注射泵' }

function dateStr(d) {
  return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0') + '-' + String(d.getDate()).padStart(2, '0')
}

function monthStr(d) {
  return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0')
}

export default {
  name: 'LifeStatsDeptDetail',
  data() {
    // 默认选择当前月份
    const now = new Date()
    return {
      activeTab: '1',
      useDept: '',
      summary: { monitor: 0, infusionPump: 0, syringePump: 0, total: 0 },
      usageDaily: [],
      usageMonthly: [],
      selectedMonth: monthStr(now), // 默认当前月份
      // 图表实例
      summaryPieChart: null,
      monthlyChart1: null,
      monthlyChart2: null,
      monthlyChart3: null,
      dailyChart1: null,
      dailyChart2: null,
      dailyChart3: null
    }
  },
  computed: {
    pageTitle() {
      return this.useDept ? `${this.useDept} - 通用设备统计` : '科室设备统计'
    },
    // 计算最近12个月的范围（从当前月份往前推11个月，包含当前月）
    monthRange() {
      const now = new Date()
      const endMonth = new Date(now.getFullYear(), now.getMonth(), 1) // 当前月份第一天
      const startMonth = new Date(now.getFullYear(), now.getMonth() - 11, 1) // 往前推11个月
      return {
        start: monthStr(startMonth),
        end: monthStr(endMonth)
      }
    },
    // 生成最近12个月的选项列表（从最早到最新）
    availableMonths() {
      const months = []
      const now = new Date()
      // 从当前月份往前推11个月，包含当前月，共12个月
      for (let i = 11; i >= 0; i--) {
        const date = new Date(now.getFullYear(), now.getMonth() - i, 1)
        const value = monthStr(date)
        const year = date.getFullYear()
        const month = date.getMonth() + 1
        const monthNames = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二']
        const label = `${year}年${monthNames[month - 1]}月`
        months.push({ value, label })
      }
      return months
    }
  },
  watch: {
    '$route.query.useDept': {
      immediate: true,
      handler(val) {
        this.useDept = val || ''
        if (this.useDept) {
          // 确保选中的月份在有效范围内
          if (this.selectedMonth && (this.selectedMonth < this.monthRange.start || this.selectedMonth > this.monthRange.end)) {
            this.selectedMonth = this.monthRange.end
          }
          this.loadDeptSummary()
          this.loadUsageAll()
        }
      }
    },
    activeTab() {
      this.$nextTick(() => {
        this.updateCharts()
      })
    },
    usageMonthly() {
      this.$nextTick(() => {
        this.updateMonthlyCharts()
      })
    },
    usageDaily() {
      this.$nextTick(() => {
        this.updateDailyCharts()
      })
    },
    summary: {
      deep: true,
      handler() {
        this.$nextTick(() => {
          this.updateSummaryPieChart()
        })
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
      this.initResizeListener()
    })
  },
  beforeDestroy() {
    this.destroyCharts()
    this.destroyResizeListener()
  },
  methods: {
    goBack() {
      this.$router.push({ path: '/custom/lifeStats' })
    },
    equipTypeLabel(type) {
      return EQUIP_TYPE_MAP[String(type)] || type || '-'
    },
    // 按设备类型过滤使用数据
    getUsageByType(list, equipType) {
      return (list || []).filter(item => String(item.equipType) === String(equipType))
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
        this.$nextTick(() => {
          this.updateSummaryPieChart()
        })
      })
    },
    loadUsageAll() {
      // 加载指定月份的每日数据
      this.loadDailyUsage()

      // 加载最近12个月的每月数据（从当前月份往前推11个月，包含当前月）
      const now = new Date()
      const endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0) // 当前月份最后一天
      const startDate = new Date(now.getFullYear(), now.getMonth() - 11, 1) // 往前推11个月的第一天
      
      sumLifeUsage({
        useDept: this.useDept,
        beginTime: dateStr(startDate),
        endTime: dateStr(endDate),
        groupBy: 'month'
      }).then(res => { 
        this.usageMonthly = res.data || []
        this.$nextTick(() => {
          this.updateMonthlyCharts()
        })
      })
    },
    // 加载指定月份的每日使用数据
    loadDailyUsage() {
      if (!this.selectedMonth || !this.useDept) return
      
      // 解析选中的月份，计算该月的开始和结束日期
      const [year, month] = this.selectedMonth.split('-').map(Number)
      const startDate = new Date(year, month - 1, 1)
      const endDate = new Date(year, month, 0) // 该月最后一天
      
      sumLifeUsage({
        useDept: this.useDept,
        beginTime: dateStr(startDate),
        endTime: dateStr(endDate),
        groupBy: 'day'
      }).then(res => { 
        this.usageDaily = res.data || []
        this.$nextTick(() => {
          this.updateDailyCharts()
        })
      })
    },
    // 处理月份变化
    handleMonthChange() {
      this.loadDailyUsage()
    },
    // 初始化所有图表
    initCharts() {
      this.initSummaryPieChart()
      this.initMonthlyChart('1', 'monthlyChart1')
      this.initMonthlyChart('2', 'monthlyChart2')
      this.initMonthlyChart('3', 'monthlyChart3')
      this.initDailyChart('1', 'dailyChart1')
      this.initDailyChart('2', 'dailyChart2')
      this.initDailyChart('3', 'dailyChart3')
    },
    // 初始化设备总台数饼图
    initSummaryPieChart() {
      const el = this.$refs.summaryPieChart
      if (!el) return
      if (this.summaryPieChart) this.summaryPieChart.dispose()
      this.summaryPieChart = echarts.init(el, 'macarons')
      this.updateSummaryPieChart()
    },
    // 更新设备总台数饼图
    updateSummaryPieChart() {
      const chart = this.summaryPieChart
      if (!chart) return
      
      const data = [
        { name: '监护仪', value: this.summary.monitor || 0 },
        { name: '输液泵', value: this.summary.infusionPump || 0 },
        { name: '注射泵', value: this.summary.syringePump || 0 }
      ].filter(item => item.value > 0) // 只显示有数据的设备类型
      
      chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} 台 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          bottom: '10%',
          data: data.map(item => item.name)
        },
        series: [{
          name: '设备总台数',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['60%', '50%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: true,
            formatter: '{b}\n{c} 台\n({d}%)'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 16,
              fontWeight: 'bold'
            }
          },
          data: data,
          color: ['#409EFF', '#67C23A', '#E6A23C']
        }]
      })
    },
    // 初始化每月使用情况图表
    initMonthlyChart(equipType, refName) {
      const el = this.$refs[refName]
      if (!el) return
      if (this[refName]) this[refName].dispose()
      this[refName] = echarts.init(el, 'macarons')
      this.updateMonthlyChart(equipType, refName)
    },
    // 更新每月使用情况图表
    updateMonthlyChart(equipType, refName) {
      const chart = this[refName]
      if (!chart) return
      
      const data = this.getUsageByType(this.usageMonthly, equipType)
      const xAxisData = data.map(item => {
        const period = item.statPeriod || ''
        // 如果是 yyyy-MM 格式，转换为 "MM月"
        if (period.length >= 7) {
          const month = parseInt(period.slice(5))
          return month + '月'
        }
        return period
      })
      const seriesData = data.map(item => Number(item.totalCount) || 0)
      
      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          formatter: function(params) {
            if (!params || !params.length) return ''
            const p = params[0]
            return p.axisValue + '<br/>' + p.marker + ' 使用台数: ' + (p.value || 0) + ' 台'
          }
        },
        grid: { left: '3%', right: '4%', bottom: '10%', top: '10%', containLabel: true },
        xAxis: { 
          type: 'category', 
          boundaryGap: false, 
          data: xAxisData, 
          axisTick: { show: false } 
        },
        yAxis: { 
          type: 'value', 
          axisTick: { show: false },
          axisLabel: { formatter: '{value}' }
        },
        series: [{
          name: '使用台数',
          type: 'line',
          smooth: true,
          data: seriesData,
          itemStyle: { color: '#409EFF' },
          lineStyle: { width: 2 },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0, color: 'rgba(64, 158, 255, 0.3)'
              }, {
                offset: 1, color: 'rgba(64, 158, 255, 0.1)'
              }]
            }
          }
        }]
      })
    },
    // 更新所有每月图表
    updateMonthlyCharts() {
      if (this.activeTab === '1') this.updateMonthlyChart('1', 'monthlyChart1')
      if (this.activeTab === '2') this.updateMonthlyChart('2', 'monthlyChart2')
      if (this.activeTab === '3') this.updateMonthlyChart('3', 'monthlyChart3')
    },
    // 初始化每日使用情况图表
    initDailyChart(equipType, refName) {
      const el = this.$refs[refName]
      if (!el) return
      if (this[refName]) this[refName].dispose()
      this[refName] = echarts.init(el, 'macarons')
      this.updateDailyChart(equipType, refName)
    },
    // 更新每日使用情况图表
    updateDailyChart(equipType, refName) {
      const chart = this[refName]
      if (!chart) return
      
      const data = this.getUsageByType(this.usageDaily, equipType)
      const xAxisData = data.map(item => {
        const period = item.statPeriod || ''
        // 如果是 yyyy-MM-dd 格式，转换为 "MM/dd"
        if (period.length >= 10) {
          return period.slice(5).replace('-', '/')
        }
        return period
      })
      const seriesData = data.map(item => Number(item.totalCount) || 0)
      
      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          formatter: function(params) {
            if (!params || !params.length) return ''
            const p = params[0]
            return p.axisValue + '<br/>' + p.marker + ' 使用台数: ' + (p.value || 0) + ' 台'
          }
        },
        grid: { left: '3%', right: '4%', bottom: '10%', top: '10%', containLabel: true },
        xAxis: { 
          type: 'category', 
          boundaryGap: false, 
          data: xAxisData, 
          axisTick: { show: false } 
        },
        yAxis: { 
          type: 'value', 
          axisTick: { show: false },
          axisLabel: { formatter: '{value}' }
        },
        series: [{
          name: '使用台数',
          type: 'line',
          smooth: true,
          data: seriesData,
          itemStyle: { color: '#67C23A' },
          lineStyle: { width: 2 },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0, color: 'rgba(103, 194, 58, 0.3)'
              }, {
                offset: 1, color: 'rgba(103, 194, 58, 0.1)'
              }]
            }
          }
        }]
      })
    },
    // 更新所有每日图表
    updateDailyCharts() {
      if (this.activeTab === '1') this.updateDailyChart('1', 'dailyChart1')
      if (this.activeTab === '2') this.updateDailyChart('2', 'dailyChart2')
      if (this.activeTab === '3') this.updateDailyChart('3', 'dailyChart3')
    },
    // 更新所有图表
    updateCharts() {
      this.updateMonthlyCharts()
      this.updateDailyCharts()
    },
    // 销毁所有图表
    destroyCharts() {
      const charts = ['summaryPieChart', 'monthlyChart1', 'monthlyChart2', 'monthlyChart3', 'dailyChart1', 'dailyChart2', 'dailyChart3']
      charts.forEach(name => {
        if (this[name]) {
          this[name].dispose()
          this[name] = null
        }
      })
    },
    // 初始化窗口大小变化监听
    initResizeListener() {
      const { debounce } = require('@/utils')
      this.$_resizeHandler = debounce(() => {
        this.resizeCharts()
      }, 100)
      window.addEventListener('resize', this.$_resizeHandler)
      
      this.$_sidebarElm = document.getElementsByClassName('sidebar-container')[0]
      this.$_sidebarElm && this.$_sidebarElm.addEventListener('transitionend', this.$_sidebarResizeHandler)
    },
    // 销毁窗口大小变化监听
    destroyResizeListener() {
      if (this.$_resizeHandler) {
        window.removeEventListener('resize', this.$_resizeHandler)
        this.$_resizeHandler = null
      }
      if (this.$_sidebarElm) {
        this.$_sidebarElm.removeEventListener('transitionend', this.$_sidebarResizeHandler)
        this.$_sidebarElm = null
      }
    },
    // 侧边栏大小变化处理
    $_sidebarResizeHandler(e) {
      if (e.propertyName === 'width') {
        if (this.$_resizeHandler) {
          this.$_resizeHandler()
        }
      }
    },
    // 调整所有图表大小
    resizeCharts() {
      const charts = ['summaryPieChart', 'monthlyChart1', 'monthlyChart2', 'monthlyChart3', 'dailyChart1', 'dailyChart2', 'dailyChart3']
      charts.forEach(name => {
        if (this[name]) {
          this[name].resize()
        }
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
.table-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin: 24px 0 12px 0;
  gap: 12px;
}
.table-header .table-title {
  margin: 0;
}
.usage-table {
  margin-bottom: 8px;
}
.chart-container {
  height: 350px;
  width: 100%;
}
.chart-pie {
  height: 200px;
}
</style>
