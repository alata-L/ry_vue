<template>
  <div class="app-container">
    <el-page-header @back="goBack" :content="pageTitle" class="page-header" />
    <div v-if="!equipId" class="empty-tip">请从科室统计页选择设备进入</div>
    <template v-else>
      <div class="filter-row">
        <div class="year-switch">
          <span class="switch-label">年份：</span>
          <el-select v-model="selectedYear" @change="onFilterChange" size="small" style="width: 100px">
            <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
          </el-select>
        </div>
        <div class="month-switch">
          <span class="switch-label">月份：</span>
          <el-select v-model="selectedMonth" @change="onFilterChange" size="small" clearable placeholder="全部" style="width: 120px">
            <el-option v-for="m in monthOptions" :key="m.value" :label="m.label" :value="m.value" />
          </el-select>
        </div>
        <div class="x-axis-switch">
          <span class="switch-label">维度：</span>
          <span class="dimension-label">按周</span>
        </div>
      </div>
      <!-- 效率指标卡片 -->
      <div class="stats-section">
        <h3 class="section-title">效率指标</h3>
        <el-row :gutter="16" class="summary-cards">
          <el-col :span="6">
            <el-card shadow="hover" class="summary-card">
              <div class="card-label">总工作时间</div>
              <div class="card-value">{{ summaryStats.totalWorkHours }}</div>
              <div class="card-unit">小时</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="summary-card">
              <div class="card-label">总服务例数</div>
              <div class="card-value">{{ summaryStats.totalTreatCount }}</div>
              <div class="card-unit">例</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="summary-card">
              <div class="card-label">总收费</div>
              <div class="card-value">{{ formatMoney(summaryStats.totalCharge) }}</div>
              <div class="card-unit">元</div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="summary-card">
              <div class="card-label">平均每例收费</div>
              <div class="card-value">{{ formatMoney(summaryStats.avgChargePerCase) }}</div>
              <div class="card-unit">元</div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <!-- 第一行：趋势图 -->
      <div class="stats-section">
        <h3 class="section-title">趋势分析</h3>
        <div ref="trendChart" class="chart-container" style="height: 400px;"></div>
      </div>
      <!-- 第二行：柱状图 + 饼图 -->
      <el-row :gutter="16" class="stats-section-row">
        <el-col :span="16">
          <div class="stats-section">
            <h3 class="section-title">各周数据对比</h3>
            <div ref="barChart" class="chart-container" style="height: 350px;"></div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stats-section">
            <h3 class="section-title">收费占比分析</h3>
            <div ref="pieChart" class="chart-container" style="height: 350px;"></div>
          </div>
        </el-col>
      </el-row>
      <!-- 数据表格 -->
      <div class="stats-section">
        <h3 class="section-title">数据明细</h3>
        <el-table :data="seriesList" border size="small" class="data-table">
        <el-table-column :label="periodColumnLabel" prop="period" min-width="120">
          <template slot-scope="scope">{{ formatPeriod(scope.row.period) }}</template>
        </el-table-column>
        <el-table-column label="上报日期" prop="reportDate" width="120" align="center">
          <template slot-scope="scope">{{ formatDate(scope.row.reportDate || scope.row.reportdate) }}</template>
        </el-table-column>
        <el-table-column label="周工作天数" prop="weekWorkDays" width="110" align="right">
          <template slot-scope="scope">{{ formatNum(scope.row.weekWorkDays || scope.row.weekworkdays) }}</template>
        </el-table-column>
        <el-table-column label="工作时间" prop="workHours" width="120" align="right">
          <template slot-scope="scope">{{ formatNum(scope.row.workHours) }}</template>
        </el-table-column>
        <el-table-column label="日均服务例数" prop="treatCount" width="130" align="right" />
        <el-table-column label="收费价格" width="120" align="right">
          <template slot-scope="scope">{{ formatMoney(scope.row.totalCharge) }}</template>
        </el-table-column>
        <el-table-column label="每例收费价格" width="130" align="right">
          <template slot-scope="scope">{{ formatMoney(scope.row.avgChargePerCase) }}</template>
        </el-table-column>
      </el-table>
      </div>
    </template>
  </div>
</template>

<script>
import { getKeyStatsEquipSeries } from '@/api/custom/keyStats'
import { getKeyEquip } from '@/api/custom/keyEquip'
import * as echarts from 'echarts'
require('echarts/theme/macarons')

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
      activeTab: 'week',
      selectedYear: currentYear,
      selectedMonth: null,
      yearOptions,
      monthOptions: [
        { value: 1, label: '1月' },
        { value: 2, label: '2月' },
        { value: 3, label: '3月' },
        { value: 4, label: '4月' },
        { value: 5, label: '5月' },
        { value: 6, label: '6月' },
        { value: 7, label: '7月' },
        { value: 8, label: '8月' },
        { value: 9, label: '9月' },
        { value: 10, label: '10月' },
        { value: 11, label: '11月' },
        { value: 12, label: '12月' }
      ],
      seriesList: [],
      trendChart: null,
      barChart: null,
      pieChart: null
    }
  },
  computed: {
    periodColumnLabel() {
      return '周次'
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
    },
    // 汇总统计数据
    summaryStats() {
      if (!this.seriesList || this.seriesList.length === 0) {
        return {
          totalWorkHours: 0,
          totalTreatCount: 0,
          totalCharge: 0,
          avgChargePerCase: 0
        }
      }
      const totalWorkHours = this.seriesList.reduce((sum, item) => sum + (Number(item.workHours) || 0), 0)
      const totalTreatCount = this.seriesList.reduce((sum, item) => sum + (Number(item.treatCount) || 0), 0)
      const totalCharge = this.seriesList.reduce((sum, item) => sum + (Number(item.totalCharge) || 0), 0)
      const avgChargePerCase = totalTreatCount > 0 ? totalCharge / totalTreatCount : 0
      
      return {
        totalWorkHours: totalWorkHours.toFixed(1),
        totalTreatCount,
        totalCharge,
        avgChargePerCase
      }
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
    },
    seriesList: {
      deep: true,
      handler() {
        this.$nextTick(() => {
          // 如果图表未初始化，先初始化
          if (!this.trendChart && this.$refs.trendChart) {
            this.initTrendChart()
          } else {
            this.updateTrendChart()
          }
          if (!this.barChart && this.$refs.barChart) {
            this.initBarChart()
          } else {
            this.updateBarChart()
          }
          if (!this.pieChart && this.$refs.pieChart) {
            this.initPieChart()
          } else {
            this.updatePieChart()
          }
        })
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initTrendChart()
      this.initBarChart()
      this.initPieChart()
      this.initResizeListener()
    })
  },
  beforeDestroy() {
    this.destroyTrendChart()
    this.destroyResizeListener()
  },
  methods: {
    formatPeriod(period) {
      if (period == null || period === '') return '-'
      const s = String(period)
      const parts = s.split('-')
      if (parts.length >= 2) return parts[0] + '年第' + parts[1] + '周'
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
    formatDate(v) {
      if (v == null || v === '') return '-'
      // 如果是日期对象，转换为字符串
      if (v instanceof Date) {
        return v.toISOString().split('T')[0]
      }
      return String(v)
    },
    getYearRange(year, month) {
      const y = Number(year) || new Date().getFullYear()
      const now = new Date()
      const currentYear = now.getFullYear()
      const currentMonth = now.getMonth() + 1
      
      let beginTime, endTime
      
      if (month != null && month !== '') {
        // 如果选择了月份，只查询该月份的数据
        const m = Number(month)
        beginTime = `${y}-${String(m).padStart(2, '0')}-01`
        // 计算该月的最后一天
        const lastDay = new Date(y, m, 0).getDate()
        // 如果是当前年当前月，结束日期为今天；否则为该月最后一天
        if (y === currentYear && m === currentMonth) {
          const d = String(now.getDate()).padStart(2, '0')
          endTime = `${y}-${String(m).padStart(2, '0')}-${d}`
        } else {
          endTime = `${y}-${String(m).padStart(2, '0')}-${String(lastDay).padStart(2, '0')}`
        }
      } else {
        // 如果没有选择月份，查询整年的数据
        beginTime = `${y}-01-01`
        if (y === currentYear) {
          const m = String(currentMonth).padStart(2, '0')
          const d = String(now.getDate()).padStart(2, '0')
          endTime = `${y}-${m}-${d}`
        } else {
          endTime = `${y}-12-31`
        }
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
    onFilterChange() {
      this.loadSeries()
    },
    loadEquipInfo() {
      getKeyEquip(this.equipId).then(res => {
        this.equipInfo = res.data || null
      })
    },
    loadSeries() {
      const { beginTime, endTime } = this.getYearRange(this.selectedYear, this.selectedMonth)
      getKeyStatsEquipSeries(this.equipId, {
        groupBy: this.activeTab,
        beginTime,
        endTime
      }).then(res => {
        this.seriesList = res.data || []
        this.$nextTick(() => {
          // 数据加载完成后，确保图表已初始化
          if (!this.trendChart && this.$refs.trendChart) {
            this.initTrendChart()
          } else if (this.trendChart) {
            this.updateTrendChart()
          }
          if (!this.barChart && this.$refs.barChart) {
            this.initBarChart()
          } else if (this.barChart) {
            this.updateBarChart()
          }
          if (!this.pieChart && this.$refs.pieChart) {
            this.initPieChart()
          } else if (this.pieChart) {
            this.updatePieChart()
          }
        })
      })
    },
    // 初始化趋势图表
    initTrendChart() {
      const el = this.$refs.trendChart
      if (!el) return
      if (this.trendChart) this.trendChart.dispose()
      this.trendChart = echarts.init(el, 'macarons')
      this.updateTrendChart()
    },
    // 更新趋势图表
    updateTrendChart() {
      const chart = this.trendChart
      if (!chart) return
      
      // 如果没有数据，显示空状态
      if (!this.seriesList || this.seriesList.length === 0) {
        chart.setOption({
          title: {
            text: '暂无数据',
            left: 'center',
            top: 'center',
            textStyle: {
              color: '#909399',
              fontSize: 14
            }
          }
        })
        return
      }
      
      // 准备X轴数据（周次）
      const xAxisData = this.seriesList.map(item => {
        const period = item.period || ''
        const parts = period.split('-')
        if (parts.length >= 2) {
          return parts[0] + '年第' + parts[1] + '周'
        }
        return period
      })
      
      // 工作时间数据
      const workHoursData = this.seriesList.map(item => Number(item.workHours) || 0)
      
      // 日均服务例数数据
      const treatCountData = this.seriesList.map(item => Number(item.treatCount) || 0)
      
      // 收费价格数据
      const totalChargeData = this.seriesList.map(item => Number(item.totalCharge) || 0)
      
      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          formatter: function(params) {
            if (!params || !params.length) return ''
            let result = params[0].axisValue + '<br/>'
            params.forEach(param => {
              if (param.seriesName === '收费价格') {
                result += param.marker + param.seriesName + ': ' + (param.value || 0).toFixed(2) + ' 元<br/>'
              } else if (param.seriesName === '工作时间') {
                result += param.marker + param.seriesName + ': ' + (param.value || 0) + ' 小时<br/>'
              } else {
                result += param.marker + param.seriesName + ': ' + (param.value || 0) + '<br/>'
              }
            })
            return result
          }
        },
        legend: {
          data: ['工作时间', '日均服务例数', '收费价格'],
          top: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '10%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: xAxisData,
          axisLabel: {
            rotate: 45,
            interval: 0,
            fontSize: 12
          }
        },
        yAxis: [
          {
            type: 'value',
            name: '工作时间/日均服务例数',
            position: 'left',
            axisLabel: {
              formatter: '{value}'
            }
          },
          {
            type: 'value',
            name: '收费价格(元)',
            position: 'right',
            axisLabel: {
              formatter: function(value) {
                if (value >= 10000) {
                  return (value / 10000).toFixed(1) + '万'
                }
                return value.toFixed(0)
              }
            }
          }
        ],
        series: [
          {
            name: '工作时间',
            type: 'line',
            smooth: true,
            data: workHoursData,
            itemStyle: { color: '#409EFF' },
            lineStyle: { width: 2 },
            yAxisIndex: 0
          },
          {
            name: '日均服务例数',
            type: 'line',
            smooth: true,
            data: treatCountData,
            itemStyle: { color: '#67C23A' },
            lineStyle: { width: 2 },
            yAxisIndex: 0
          },
          {
            name: '收费价格',
            type: 'line',
            smooth: true,
            data: totalChargeData,
            itemStyle: { color: '#E6A23C' },
            lineStyle: { width: 2 },
            yAxisIndex: 1
          }
        ]
      })
    },
    // 初始化窗口大小变化监听
    initResizeListener() {
      const { debounce } = require('@/utils')
      this.$_resizeHandler = debounce(() => {
        this.resizeChart()
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
    // 调整图表大小
    resizeChart() {
      if (this.trendChart) {
        this.trendChart.resize()
      }
      if (this.barChart) {
        this.barChart.resize()
      }
      if (this.pieChart) {
        this.pieChart.resize()
      }
    },
    // 销毁图表
    destroyTrendChart() {
      if (this.trendChart) {
        this.trendChart.dispose()
        this.trendChart = null
      }
      if (this.barChart) {
        this.barChart.dispose()
        this.barChart = null
      }
      if (this.pieChart) {
        this.pieChart.dispose()
        this.pieChart = null
      }
    },
    // 初始化柱状图
    initBarChart() {
      const el = this.$refs.barChart
      if (!el) return
      if (this.barChart) this.barChart.dispose()
      this.barChart = echarts.init(el, 'macarons')
      this.updateBarChart()
    },
    // 更新柱状图
    updateBarChart() {
      const chart = this.barChart
      if (!chart) return
      
      if (!this.seriesList || this.seriesList.length === 0) {
        chart.setOption({
          title: {
            text: '暂无数据',
            left: 'center',
            top: 'center',
            textStyle: {
              color: '#909399',
              fontSize: 14
            }
          }
        })
        return
      }
      
      const xAxisData = this.seriesList.map(item => {
        const period = item.period || ''
        const parts = period.split('-')
        if (parts.length >= 2) {
          return parts[0] + '年第' + parts[1] + '周'
        }
        return period
      })
      
      const workHoursData = this.seriesList.map(item => Number(item.workHours) || 0)
      const treatCountData = this.seriesList.map(item => Number(item.treatCount) || 0)
      const totalChargeData = this.seriesList.map(item => Number(item.totalCharge) || 0)
      
      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: function(params) {
            if (!params || !params.length) return ''
            let result = params[0].axisValue + '<br/>'
            params.forEach(param => {
              if (param.seriesName === '收费价格') {
                result += param.marker + param.seriesName + ': ' + (param.value || 0).toFixed(2) + ' 元<br/>'
              } else if (param.seriesName === '工作时间') {
                result += param.marker + param.seriesName + ': ' + (param.value || 0) + ' 小时<br/>'
              } else {
                result += param.marker + param.seriesName + ': ' + (param.value || 0) + '<br/>'
              }
            })
            return result
          }
        },
        legend: {
          data: ['工作时间', '日均服务例数', '收费价格'],
          top: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xAxisData,
          axisLabel: {
            rotate: 45,
            interval: 0,
            fontSize: 12
          }
        },
        yAxis: [
          {
            type: 'value',
            name: '工作时间/日均服务例数',
            position: 'left',
            axisLabel: {
              formatter: '{value}'
            }
          },
          {
            type: 'value',
            name: '收费价格(元)',
            position: 'right',
            axisLabel: {
              formatter: function(value) {
                if (value >= 10000) {
                  return (value / 10000).toFixed(1) + '万'
                }
                return value.toFixed(0)
              }
            }
          }
        ],
        series: [
          {
            name: '工作时间',
            type: 'bar',
            data: workHoursData,
            itemStyle: { color: '#409EFF' },
            yAxisIndex: 0
          },
          {
            name: '日均服务例数',
            type: 'bar',
            data: treatCountData,
            itemStyle: { color: '#67C23A' },
            yAxisIndex: 0
          },
          {
            name: '收费价格',
            type: 'bar',
            data: totalChargeData,
            itemStyle: { color: '#E6A23C' },
            yAxisIndex: 1
          }
        ]
      })
    },
    // 初始化饼图
    initPieChart() {
      const el = this.$refs.pieChart
      if (!el) return
      if (this.pieChart) this.pieChart.dispose()
      this.pieChart = echarts.init(el, 'macarons')
      this.updatePieChart()
    },
    // 更新饼图
    updatePieChart() {
      const chart = this.pieChart
      if (!chart) return
      
      if (!this.seriesList || this.seriesList.length === 0) {
        chart.setOption({
          title: {
            text: '暂无数据',
            left: 'center',
            top: 'center',
            textStyle: {
              color: '#909399',
              fontSize: 14
            }
          }
        })
        return
      }
      
      // 计算总收费
      const totalCharge = this.seriesList.reduce((sum, item) => sum + (Number(item.totalCharge) || 0), 0)
      
      if (totalCharge === 0) {
        chart.setOption({
          title: {
            text: '暂无数据',
            left: 'center',
            top: 'center',
            textStyle: {
              color: '#909399',
              fontSize: 14
            }
          }
        })
        return
      }
      
      // 准备饼图数据（只显示收费大于0的周）
      const data = this.seriesList
        .filter(item => (Number(item.totalCharge) || 0) > 0)
        .map(item => {
          const period = item.period || ''
          const parts = period.split('-')
          const name = parts.length >= 2 ? parts[0] + '年第' + parts[1] + '周' : period
          return {
            name: name,
            value: Number(item.totalCharge) || 0
          }
        })
        .sort((a, b) => b.value - a.value) // 按收费从高到低排序
        .slice(0, 10) // 最多显示10个
      
      chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} 元 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          bottom: '10%',
          data: data.map(item => item.name),
          textStyle: {
            fontSize: 11
          }
        },
        series: [{
          name: '收费占比',
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
            formatter: function(params) {
              const name = params.name.length > 8 ? params.name.substring(0, 8) + '...' : params.name
              return name + '\n' + params.value.toFixed(2) + ' 元\n(' + params.percent + '%)'
            },
            fontSize: 10
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 12,
              fontWeight: 'bold'
            }
          },
          data: data
        }]
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
.year-switch,
.month-switch {
  display: flex;
  align-items: center;
  .switch-label {
    margin-right: 8px;
    color: #606266;
    font-size: 14px;
  }
  .dimension-label {
    color: #606266;
    font-size: 14px;
    font-weight: 500;
  }
}
.stats-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #fff;
  border-radius: 4px;
  .section-title {
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 16px 0;
    color: #303133;
  }
}
.chart-container {
  width: 100%;
}
.stats-section-row {
  margin-bottom: 0;
}
.summary-cards {
  margin-bottom: 0;
}
.summary-card {
  text-align: center;
  .card-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 8px;
  }
  .card-value {
    font-size: 22px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 4px;
  }
  .card-unit {
    font-size: 12px;
    color: #909399;
  }
}
.data-table {
  margin-top: 0;
}
</style>
