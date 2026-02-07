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
      <!-- 设备使用数据：标签页展示 -->
      <div class="stats-section usage-section">
        <h3 class="section-title">设备使用数据</h3>
        <el-tabs v-model="activeTab">
          <el-tab-pane label="监护仪" name="1">
            <h4 class="table-title">最近一年的每月使用情况</h4>
            <el-table :data="getUsageByType(usageMonthly, '1')" border size="small" class="usage-table">
              <el-table-column label="月份" prop="statPeriod" min-width="120" />
              <el-table-column label="使用台数" prop="totalCount" width="120" align="right" />
            </el-table>

            <div class="table-header">
              <h4 class="table-title">每日使用情况</h4>
              <el-select v-model="selectedMonth" placeholder="选择月份" size="small" style="width:160px" @change="handleMonthChange">
                <el-option v-for="month in availableMonths" :key="month.value" :label="month.label" :value="month.value"></el-option>
              </el-select>
            </div>
            <el-table :data="getUsageByType(usageDaily, '1')" border size="small" class="usage-table">
              <el-table-column label="日期" prop="statPeriod" min-width="120" />
              <el-table-column label="使用台数" prop="totalCount" width="120" align="right" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="输液泵" name="2">
            <h4 class="table-title">最近一年的每月使用情况</h4>
            <el-table :data="getUsageByType(usageMonthly, '2')" border size="small" class="usage-table">
              <el-table-column label="月份" prop="statPeriod" min-width="120" />
              <el-table-column label="使用台数" prop="totalCount" width="120" align="right" />
            </el-table>

            <div class="table-header">
              <h4 class="table-title">每日使用情况</h4>
              <el-select v-model="selectedMonth" placeholder="选择月份" size="small" style="width:160px" @change="handleMonthChange">
                <el-option v-for="month in availableMonths" :key="month.value" :label="month.label" :value="month.value"></el-option>
              </el-select>
            </div>
            <el-table :data="getUsageByType(usageDaily, '2')" border size="small" class="usage-table">
              <el-table-column label="日期" prop="statPeriod" min-width="120" />
              <el-table-column label="使用台数" prop="totalCount" width="120" align="right" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="注射泵" name="3">
            <h4 class="table-title">最近一年的每月使用情况</h4>
            <el-table :data="getUsageByType(usageMonthly, '3')" border size="small" class="usage-table">
              <el-table-column label="月份" prop="statPeriod" min-width="120" />
              <el-table-column label="使用台数" prop="totalCount" width="120" align="right" />
            </el-table>

            <div class="table-header">
              <h4 class="table-title">每日使用情况</h4>
              <el-select v-model="selectedMonth" placeholder="选择月份" size="small" style="width:160px" @change="handleMonthChange">
                <el-option v-for="month in availableMonths" :key="month.value" :label="month.label" :value="month.value"></el-option>
              </el-select>
            </div>
            <el-table :data="getUsageByType(usageDaily, '3')" border size="small" class="usage-table">
              <el-table-column label="日期" prop="statPeriod" min-width="120" />
              <el-table-column label="使用台数" prop="totalCount" width="120" align="right" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
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
      selectedMonth: monthStr(now) // 默认当前月份
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
    }
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
      }).then(res => { this.usageMonthly = res.data || [] })
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
      }).then(res => { this.usageDaily = res.data || [] })
    },
    // 处理月份变化
    handleMonthChange() {
      this.loadDailyUsage()
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
</style>
