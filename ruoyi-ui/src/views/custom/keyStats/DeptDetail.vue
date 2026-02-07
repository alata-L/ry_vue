<template>
  <div class="app-container">
    <el-page-header @back="goBack" :content="pageTitle" class="page-header" />
    <div v-if="!useDept" class="empty-tip">请从统计页选择科室进入</div>
    <template v-else>
      <!-- 饼图 + 汇总卡片 -->
      <div class="stats-section">
        <h3 class="section-title">收费统计</h3>
        <el-row :gutter="16">
          <el-col :span="8">
            <div ref="chargePieChart" class="chart-container chart-pie"></div>
          </el-col>
          <el-col :span="16">
            <el-row :gutter="16" class="summary-cards">
              <el-col :span="6">
                <el-card shadow="hover" class="summary-card">
                  <div class="card-label">今年收费</div>
                  <div class="card-value">{{ formatMoney(summary.chargeThisYear) }}</div>
                  <div class="card-unit">元</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover" class="summary-card">
                  <div class="card-label">去年收费</div>
                  <div class="card-value">{{ formatMoney(summary.chargeLastYear) }}</div>
                  <div class="card-unit">元</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover" class="summary-card">
                  <div class="card-label">今年诊治例数</div>
                  <div class="card-value">{{ summary.treatThisYear || 0 }}</div>
                  <div class="card-unit">例</div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card shadow="hover" class="summary-card">
                  <div class="card-label">去年诊治例数</div>
                  <div class="card-value">{{ summary.treatLastYear || 0 }}</div>
                  <div class="card-unit">例</div>
                </el-card>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </div>
      <!-- 柱状图：收费对比 -->
      <div class="stats-section">
        <h3 class="section-title">各设备收费对比（Top10）</h3>
        <div ref="chargeBarChart" class="chart-container" style="height: 400px;"></div>
      </div>
      <!-- 数据表格 -->
      <div class="stats-section">
        <h3 class="section-title">设备明细</h3>
        <el-table :data="equipList" border size="small" class="mt16">
          <el-table-column label="设备编号" prop="equipNo" min-width="120">
            <template slot-scope="scope">
              <el-link type="primary" :underline="false" @click="goEquipDetail(scope.row)">{{ scope.row.equipNo }}</el-link>
            </template>
          </el-table-column>
          <el-table-column label="设备描述" prop="equipDesc" min-width="180" show-overflow-tooltip />
          <el-table-column label="今月收费" width="110" align="right">
            <template slot-scope="scope">{{ formatMoney(scope.row.chargeThisMonth) }}</template>
          </el-table-column>
          <el-table-column label="上月收费" width="110" align="right">
            <template slot-scope="scope">{{ formatMoney(scope.row.chargeLastMonth) }}</template>
          </el-table-column>
          <el-table-column label="今年收费" width="110" align="right">
            <template slot-scope="scope">{{ formatMoney(scope.row.chargeThisYear) }}</template>
          </el-table-column>
          <el-table-column label="去年收费" width="110" align="right">
            <template slot-scope="scope">{{ formatMoney(scope.row.chargeLastYear) }}</template>
          </el-table-column>
          <el-table-column label="今月诊治例数" prop="treatThisMonth" width="120" align="right" />
          <el-table-column label="上月诊治例数" prop="treatLastMonth" width="120" align="right" />
          <el-table-column label="今年诊治例数" prop="treatThisYear" width="120" align="right" />
          <el-table-column label="去年诊治例数" prop="treatLastYear" width="120" align="right" />
        </el-table>
      </div>
    </template>
  </div>
</template>

<script>
import { getKeyStatsDept } from '@/api/custom/keyStats'
import * as echarts from 'echarts'
require('echarts/theme/macarons')

export default {
  name: 'KeyStatsDeptDetail',
  data() {
    return {
      useDept: '',
      equipList: [],
      summary: {
        chargeThisYear: 0,
        chargeLastYear: 0,
        treatThisYear: 0,
        treatLastYear: 0
      },
      chargePieChart: null,
      chargeBarChart: null
    }
  },
  computed: {
    pageTitle() {
      return this.useDept ? `${this.useDept} - 重点设备统计` : '科室重点设备统计'
    }
  },
  watch: {
    '$route.query.useDept': {
      immediate: true,
      handler(val) {
        this.useDept = val || ''
        if (this.useDept) {
          this.loadDeptEquip()
        }
      }
    },
    equipList: {
      deep: true,
      handler() {
        this.$nextTick(() => {
          this.updateSummary()
          this.updateCharts()
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
    formatMoney(v) {
      if (v == null || v === '') return '0.00'
      const n = Number(v)
      return isNaN(n) ? '0.00' : n.toFixed(2)
    },
    goBack() {
      this.$router.push({ path: '/custom/keyStats' })
    },
    goEquipDetail(row) {
      if (!row || !row.equipId) return
      this.$router.push({ path: '/custom/keyStats/equip', query: { equipId: row.equipId, useDept: this.useDept } })
    },
    loadDeptEquip() {
      getKeyStatsDept(this.useDept).then(res => {
        this.equipList = res.data || []
      })
    },
    // 更新汇总数据
    updateSummary() {
      const summary = {
        chargeThisYear: 0,
        chargeLastYear: 0,
        treatThisYear: 0,
        treatLastYear: 0
      }
      this.equipList.forEach(item => {
        summary.chargeThisYear += Number(item.chargeThisYear) || 0
        summary.chargeLastYear += Number(item.chargeLastYear) || 0
        summary.treatThisYear += Number(item.treatThisYear) || 0
        summary.treatLastYear += Number(item.treatLastYear) || 0
      })
      this.summary = summary
    },
    // 初始化图表
    initCharts() {
      this.initChargePieChart()
      this.initChargeBarChart()
    },
    // 初始化收费占比饼图
    initChargePieChart() {
      const el = this.$refs.chargePieChart
      if (!el) return
      if (this.chargePieChart) this.chargePieChart.dispose()
      this.chargePieChart = echarts.init(el, 'macarons')
      this.updateChargePieChart()
    },
    // 更新收费占比饼图
    updateChargePieChart() {
      const chart = this.chargePieChart
      if (!chart) return
      
      // 只显示今年收费大于0的设备
      const data = this.equipList
        .filter(item => (Number(item.chargeThisYear) || 0) > 0)
        .map(item => ({
          name: item.equipDesc || item.equipNo || '未知设备',
          value: Number(item.chargeThisYear) || 0
        }))
        .sort((a, b) => b.value - a.value) // 按收费从高到低排序
        .slice(0, 10) // 最多显示10个设备
      
      if (data.length === 0) {
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
            fontSize: 12
          }
        },
        series: [{
          name: '今年收费',
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
            }
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 14,
              fontWeight: 'bold'
            }
          },
          data: data
        }]
      })
    },
    // 初始化收费对比柱状图
    initChargeBarChart() {
      const el = this.$refs.chargeBarChart
      if (!el) return
      if (this.chargeBarChart) this.chargeBarChart.dispose()
      this.chargeBarChart = echarts.init(el, 'macarons')
      this.updateChargeBarChart()
    },
    // 更新收费对比柱状图
    updateChargeBarChart() {
      const chart = this.chargeBarChart
      if (!chart) return
      
      // 按今年收费排序，取前10个设备
      const sortedList = [...this.equipList]
        .sort((a, b) => (Number(b.chargeThisYear) || 0) - (Number(a.chargeThisYear) || 0))
        .slice(0, 10)
      
      const xAxisData = sortedList.map(item => {
        const name = item.equipDesc || item.equipNo || '未知设备'
        return name.length > 10 ? name.substring(0, 10) + '...' : name
      })
      const thisYearData = sortedList.map(item => Number(item.chargeThisYear) || 0)
      const lastYearData = sortedList.map(item => Number(item.chargeLastYear) || 0)
      
      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          formatter: function(params) {
            if (!params || !params.length) return ''
            let result = params[0].axisValue + '<br/>'
            params.forEach(param => {
              result += param.marker + param.seriesName + ': ' + (param.value || 0).toFixed(2) + ' 元<br/>'
            })
            return result
          }
        },
        legend: {
          data: ['今年收费', '去年收费'],
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
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: function(value) {
              if (value >= 10000) {
                return (value / 10000).toFixed(1) + '万'
              }
              return value.toFixed(0)
            }
          }
        },
        series: [
          {
            name: '今年收费',
            type: 'bar',
            data: thisYearData,
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '去年收费',
            type: 'bar',
            data: lastYearData,
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      })
    },
    // 更新所有图表
    updateCharts() {
      this.updateChargePieChart()
      this.updateChargeBarChart()
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
      const charts = ['chargePieChart', 'chargeBarChart']
      charts.forEach(name => {
        if (this[name]) {
          this[name].resize()
        }
      })
    },
    // 销毁所有图表
    destroyCharts() {
      if (this.chargePieChart) {
        this.chargePieChart.dispose()
        this.chargePieChart = null
      }
      if (this.chargeBarChart) {
        this.chargeBarChart.dispose()
        this.chargeBarChart = null
      }
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
  &.total .card-value {
    color: #409eff;
  }
}
.chart-container {
  width: 100%;
  height: 300px;
  &.chart-pie {
    height: 300px;
  }
}
.mt16 {
  margin-top: 16px;
}
</style>
