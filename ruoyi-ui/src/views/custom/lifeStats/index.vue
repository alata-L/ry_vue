<template>
  <div class="app-container">
    <div class="stats-section">
      <h2 class="stats-title">全院生命支持类设备统计</h2>
      <el-row :gutter="16" class="summary-cards">
        <el-col :span="6">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">6年以上</div>
            <div class="card-value">{{ summaryCnt6 }}</div>
            <div class="card-unit">台</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">10年以上</div>
            <div class="card-value">{{ summaryCnt10 }}</div>
            <div class="card-unit">台</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">15年以上</div>
            <div class="card-value">{{ summaryCnt15 }}</div>
            <div class="card-unit">台</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="summary-card total">
            <div class="card-label">合计</div>
            <div class="card-value">{{ summaryTotal }}</div>
            <div class="card-unit">台</div>
          </el-card>
        </el-col>
      </el-row>
      <el-table :data="yearsTableData" border size="small" class="mt16">
        <el-table-column label="使用科室" prop="useDept" min-width="140">
          <template slot-scope="scope">
            <el-link type="primary" :underline="false" @click="goDeptDetail(scope.row.useDept)">{{ scope.row.useDept }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="6年以上" prop="cnt6" width="120" align="right" />
        <el-table-column label="10年以上" prop="cnt10" width="120" align="right" />
        <el-table-column label="15年以上" prop="cnt15" width="120" align="right" />
      </el-table>
    </div>
  </div>
</template>

<script>
import { statsYears } from '@/api/custom/lifeEquip'

export default {
  name: 'LifeStats',
  data() {
    return {
      years6List: [],
      years10List: [],
      years15List: []
    }
  },
  computed: {
    // 6年及以上、10年以下
    summaryCnt6() {
      const s6 = (this.years6List || []).reduce((s, r) => s + (Number(r.cnt) || 0), 0)
      const s10 = (this.years10List || []).reduce((s, r) => s + (Number(r.cnt) || 0), 0)
      return Math.max(0, s6 - s10)
    },
    // 10年及以上、15年以下
    summaryCnt10() {
      const s10 = (this.years10List || []).reduce((s, r) => s + (Number(r.cnt) || 0), 0)
      const s15 = (this.years15List || []).reduce((s, r) => s + (Number(r.cnt) || 0), 0)
      return Math.max(0, s10 - s15)
    },
    // 15年及以上
    summaryCnt15() {
      return (this.years15List || []).reduce((s, r) => s + (Number(r.cnt) || 0), 0)
    },
    summaryTotal() {
      return this.summaryCnt6 + this.summaryCnt10 + this.summaryCnt15
    },
    yearsTableData() {
      const map6 = {}
      const map10 = {}
      const map15 = {}
      ;(this.years6List || []).forEach(r => { map6[r.useDept] = Number(r.cnt) || 0 })
      ;(this.years10List || []).forEach(r => { map10[r.useDept] = Number(r.cnt) || 0 })
      ;(this.years15List || []).forEach(r => { map15[r.useDept] = Number(r.cnt) || 0 })
      const depts = new Set([...Object.keys(map6), ...Object.keys(map10), ...Object.keys(map15)])
      return Array.from(depts).sort().map(useDept => {
        const m6 = map6[useDept] || 0
        const m10 = map10[useDept] || 0
        const m15 = map15[useDept] || 0
        return {
          useDept,
          cnt6: Math.max(0, m6 - m10),
          cnt10: Math.max(0, m10 - m15),
          cnt15: m15
        }
      })
    }
  },
  mounted() {
    this.loadYearsAll()
  },
  methods: {
    goDeptDetail(useDept) {
      if (!useDept) return
      this.$router.push({ path: '/custom/lifeStats/dept', query: { useDept } })
    },
    loadYearsAll() {
      Promise.all([
        statsYears(6),
        statsYears(10),
        statsYears(15)
      ]).then(([r6, r10, r15]) => {
        this.years6List = r6.data || []
        this.years10List = r10.data || []
        this.years15List = r15.data || []
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.stats-section {
  padding: 0 4px;
}
.stats-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 16px 0;
  color: #303133;
}
.summary-cards {
  margin-bottom: 16px;
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
.mt16 {
  margin-top: 16px;
}
</style>
