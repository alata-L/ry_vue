<template>
  <div class="app-container">
    <!-- 首页2：全院生命支持类设备装备使用情况 -->
    <div class="stats-section">
      <h2 class="section-title">首页2-全院生命支持类设备装备使用情况</h2>
      <el-row :gutter="16" class="summary-cards">
        <el-col :xs="24" :sm="12" :md="8">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">监护仪台数</div>
            <div class="card-value">{{ lifeEquipCount.monitor || 0 }}</div>
            <div class="card-unit">台</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">输液泵台数</div>
            <div class="card-value">{{ lifeEquipCount.infusionPump || 0 }}</div>
            <div class="card-unit">台</div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">注射泵台数</div>
            <div class="card-value">{{ lifeEquipCount.syringePump || 0 }}</div>
            <div class="card-unit">台</div>
          </el-card>
        </el-col>
      </el-row>
      <!-- 左侧：全院使用趋势 -->
      <el-row :gutter="16" class="chart-row">
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-header">
              <span class="chart-title">全院监护仪使用趋势（日均数据）</span>
              <el-select v-model="lifeGroupBy.monitor" @change="onLifeGroupByChange('monitor')" size="small" style="width: 100px; margin-left: 10px;">
                <el-option label="按日" value="day" />
                <el-option label="按月" value="month" />
                <el-option label="按年" value="year" />
              </el-select>
            </div>
            <div ref="chartLifeMonitor" class="chart-container" />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-header">
              <span class="chart-title">监护仪科室日均使用排名TOP5（今年）</span>
              <el-select v-model="lifeGroupBy.monitorRank" @change="onLifeGroupByChange('monitorRank')" size="small" style="width: 100px; margin-left: 10px;">
                <el-option label="按日" value="day" />
                <el-option label="按月" value="month" />
                <el-option label="按年" value="year" />
              </el-select>
            </div>
            <div ref="chartLifeMonitorRank" class="chart-container" />
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="16" class="chart-row">
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-header">
              <span class="chart-title">全院输液泵使用趋势（日均数据）</span>
              <el-select v-model="lifeGroupBy.infusionPump" @change="onLifeGroupByChange('infusionPump')" size="small" style="width: 100px; margin-left: 10px;">
                <el-option label="按日" value="day" />
                <el-option label="按月" value="month" />
                <el-option label="按年" value="year" />
              </el-select>
            </div>
            <div ref="chartLifeInfusionPump" class="chart-container" />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-header">
              <span class="chart-title">输液泵科室日均使用排名TOP5（今年）</span>
              <el-select v-model="lifeGroupBy.infusionPumpRank" @change="onLifeGroupByChange('infusionPumpRank')" size="small" style="width: 100px; margin-left: 10px;">
                <el-option label="按日" value="day" />
                <el-option label="按月" value="month" />
                <el-option label="按年" value="year" />
              </el-select>
            </div>
            <div ref="chartLifeInfusionPumpRank" class="chart-container" />
          </el-card>
        </el-col>
      </el-row>
      <el-row :gutter="16" class="chart-row">
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-header">
              <span class="chart-title">全院注射泵使用趋势（日均数据）</span>
              <el-select v-model="lifeGroupBy.syringePump" @change="onLifeGroupByChange('syringePump')" size="small" style="width: 100px; margin-left: 10px;">
                <el-option label="按日" value="day" />
                <el-option label="按月" value="month" />
                <el-option label="按年" value="year" />
              </el-select>
            </div>
            <div ref="chartLifeSyringePump" class="chart-container" />
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="12">
          <el-card shadow="hover" class="chart-card">
            <div slot="header" class="chart-header">
              <span class="chart-title">注射泵科室日均使用排名TOP5（今年）</span>
              <el-select v-model="lifeGroupBy.syringePumpRank" @change="onLifeGroupByChange('syringePumpRank')" size="small" style="width: 100px; margin-left: 10px;">
                <el-option label="按日" value="day" />
                <el-option label="按月" value="month" />
                <el-option label="按年" value="year" />
              </el-select>
            </div>
            <div ref="chartLifeSyringePumpRank" class="chart-container" />
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
import { getLifeEquipCountByType, getLifeUsageTrendByType, getLifeUsageDeptRankByType } from '@/api/custom/lifeStats'

export default {
  name: 'Index2',
  mixins: [resize],
  data() {
    return {
      lifeEquipCount: {
        monitor: 0,
        infusionPump: 0,
        syringePump: 0
      },
      lifeGroupBy: {
        monitor: 'day',
        monitorRank: 'day',
        infusionPump: 'day',
        infusionPumpRank: 'day',
        syringePump: 'day',
        syringePumpRank: 'day'
      },
      lifeTrendData: {
        monitor: [],
        infusionPump: [],
        syringePump: []
      },
      lifeRankData: {
        monitor: [],
        infusionPump: [],
        syringePump: []
      },
      chartLifeMonitor: null,
      chartLifeMonitorRank: null,
      chartLifeInfusionPump: null,
      chartLifeInfusionPumpRank: null,
      chartLifeSyringePump: null,
      chartLifeSyringePumpRank: null
    }
  },
  mounted() {
    this.loadData()
  },
  beforeDestroy() {
    const charts = [this.chartLifeMonitor, this.chartLifeMonitorRank, this.chartLifeInfusionPump, this.chartLifeInfusionPumpRank,
      this.chartLifeSyringePump, this.chartLifeSyringePumpRank]
    charts.forEach(ch => {
      if (ch) {
        ch.dispose()
      }
    })
    this.chartLifeMonitor = this.chartLifeMonitorRank = this.chartLifeInfusionPump = this.chartLifeInfusionPumpRank = null
    this.chartLifeSyringePump = this.chartLifeSyringePumpRank = null
  },
  methods: {
    resize() {
      const charts = [this.chartLifeMonitor, this.chartLifeMonitorRank, this.chartLifeInfusionPump, this.chartLifeInfusionPumpRank,
        this.chartLifeSyringePump, this.chartLifeSyringePumpRank]
      charts.forEach(ch => ch && ch.resize())
    },
    loadData() {
      Promise.allSettled([
        getLifeEquipCountByType()
      ]).then(([r0]) => {
        if (r0.status === 'fulfilled' && r0.value.data) {
          const data = r0.value.data
          this.lifeEquipCount.monitor = data['1'] || 0
          this.lifeEquipCount.infusionPump = data['2'] || 0
          this.lifeEquipCount.syringePump = data['3'] || 0
        }
        this.$nextTick(() => {
          this.loadLifeCharts()
        })
      })
    },
    loadLifeCharts() {
      // 加载所有生命支持类设备图表数据
      this.loadLifeTrendChart('monitor', '1')
      this.loadLifeTrendChart('infusionPump', '2')
      this.loadLifeTrendChart('syringePump', '3')
      this.loadLifeRankChart('monitor', '1')
      this.loadLifeRankChart('infusionPump', '2')
      this.loadLifeRankChart('syringePump', '3')
    },
    loadLifeTrendChart(key, equipType) {
      const groupBy = this.lifeGroupBy[key]
      getLifeUsageTrendByType(equipType, groupBy).then(res => {
        this.lifeTrendData[key] = res.data || []
        this.$nextTick(() => {
          this.updateLifeTrendChart(key)
        })
      })
    },
    loadLifeRankChart(key, equipType) {
      const groupBy = this.lifeGroupBy[key + 'Rank']
      getLifeUsageDeptRankByType(equipType, groupBy).then(res => {
        this.lifeRankData[key] = res.data || []
        this.$nextTick(() => {
          this.updateLifeRankChart(key)
        })
      })
    },
    onLifeGroupByChange(key) {
      if (key === 'monitor') {
        this.loadLifeTrendChart('monitor', '1')
      } else if (key === 'monitorRank') {
        this.loadLifeRankChart('monitor', '1')
      } else if (key === 'infusionPump') {
        this.loadLifeTrendChart('infusionPump', '2')
      } else if (key === 'infusionPumpRank') {
        this.loadLifeRankChart('infusionPump', '2')
      } else if (key === 'syringePump') {
        this.loadLifeTrendChart('syringePump', '3')
      } else if (key === 'syringePumpRank') {
        this.loadLifeRankChart('syringePump', '3')
      }
    },
    updateLifeTrendChart(key) {
      const chartRef = 'chartLife' + key.charAt(0).toUpperCase() + key.slice(1)
      const el = this.$refs[chartRef]
      if (!el) return
      if (this[chartRef]) this[chartRef].dispose()
      this[chartRef] = echarts.init(el, 'macarons')
      
      const data = this.lifeTrendData[key] || []
      const xAxisData = data.map(item => {
        const period = item.statPeriod || ''
        if (this.lifeGroupBy[key] === 'month') {
          return period.length >= 7 ? period.slice(5) + '月' : period
        } else if (this.lifeGroupBy[key] === 'year') {
          return period + '年'
        }
        return period
      })
      const values = data.map(item => Number(item.avgDailyCount) || 0)
      
      this[chartRef].setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross' },
          formatter: function(params) {
            if (!params || !params.length) return ''
            const p = params[0]
            return p.axisValue + '<br/>日均使用: ' + (p.value || 0).toFixed(2) + ' 台'
          }
        },
        grid: { left: '3%', right: '4%', bottom: '15%', top: '10%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: xAxisData, axisTick: { show: false } },
        yAxis: { type: 'value', axisTick: { show: false }, name: '日均使用(台)' },
        series: [{
          name: '日均使用',
          type: 'line',
          smooth: true,
          data: values,
          itemStyle: { color: '#409EFF' },
          lineStyle: { width: 2 }
        }]
      })
    },
    updateLifeRankChart(key) {
      const chartRef = 'chartLife' + key.charAt(0).toUpperCase() + key.slice(1) + 'Rank'
      const el = this.$refs[chartRef]
      if (!el) return
      if (this[chartRef]) this[chartRef].dispose()
      this[chartRef] = echarts.init(el, 'macarons')
      
      const data = this.lifeRankData[key] || []
      // 按period分组，每个period取TOP5
      const byPeriod = {}
      data.forEach(item => {
        const period = item.statPeriod || ''
        if (!byPeriod[period]) {
          byPeriod[period] = []
        }
        byPeriod[period].push(item)
      })
      
      // 取最新的period的TOP5
      const periods = Object.keys(byPeriod).sort()
      const latestPeriod = periods[periods.length - 1] || ''
      const top5 = (byPeriod[latestPeriod] || [])
        .sort((a, b) => (Number(b.avgDailyCount) || 0) - (Number(a.avgDailyCount) || 0))
        .slice(0, 5)
      
      const names = top5.map(item => item.useDept || '')
      const values = top5.map(item => Number(item.avgDailyCount) || 0)
      
      this[chartRef].setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: function(params) {
            if (!params || !params.length) return ''
            const p = params[0]
            return p.axisValue + '<br/>日均使用: ' + (p.value || 0).toFixed(2) + ' 台'
          }
        },
        grid: { left: '3%', right: '4%', bottom: '10%', top: '10%', containLabel: true },
        xAxis: { type: 'category', data: names, axisTick: { show: false } },
        yAxis: { type: 'value', axisTick: { show: false }, name: '日均使用(台)' },
        series: [{
          type: 'bar',
          data: values,
          itemStyle: { color: '#67C23A' }
        }]
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
.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.chart-container {
  height: 350px;
  width: 100%;
}
</style>
