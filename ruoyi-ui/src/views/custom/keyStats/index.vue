<template>
  <div class="app-container home">
    <div class="stats-section">
      <h2 class="stats-title">全院重点设备统计</h2>
      <el-row :gutter="16" class="summary-cards">
        <el-col :span="4">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">设备数量</div>
            <div class="card-value">{{ summary.equipCount }}</div>
            <div class="card-unit">台</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">设备价值</div>
            <div class="card-value">{{ formatMoney(summary.totalValue) }}</div>
            <div class="card-unit">元</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">今月收费</div>
            <div class="card-value">{{ formatMoney(summary.chargeThisMonth) }}</div>
            <div class="card-unit">元</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">上月收费</div>
            <div class="card-value">{{ formatMoney(summary.chargeLastMonth) }}</div>
            <div class="card-unit">元</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">今年收费</div>
            <div class="card-value">{{ formatMoney(summary.chargeThisYear) }}</div>
            <div class="card-unit">元</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card shadow="hover" class="summary-card">
            <div class="card-label">去年收费</div>
            <div class="card-value">{{ formatMoney(summary.chargeLastYear) }}</div>
            <div class="card-unit">元</div>
          </el-card>
        </el-col>
      </el-row>
      <el-table :data="deptList" border size="small" class="mt16">
        <el-table-column label="科室" prop="useDept" min-width="200">
          <template slot-scope="scope">
            <el-link type="primary" :underline="false" @click="goDeptDetail(scope.row.useDept)">{{ scope.row.useDept }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="设备数量" prop="equipCount" width="110" align="right" />
        <el-table-column label="设备价值" width="120" align="right">
          <template slot-scope="scope">{{ formatMoney(scope.row.totalValue) }}</template>
        </el-table-column>
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
        <el-table-column label="今月诊治例数" prop="treatThisMonth" width="110" align="right" />
        <el-table-column label="上月诊治例数" prop="treatLastMonth" width="110" align="right" />
        <el-table-column label="今年诊治例数" prop="treatThisYear" width="110" align="right" />
        <el-table-column label="去年诊治例数" prop="treatLastYear" width="110" align="right" />
      </el-table>
    </div>
  </div>
</template>

<script>
import { getKeyStatsSummary } from '@/api/custom/keyStats'

export default {
  name: 'KeyStats',
  data() {
    return {
      summary: {
        equipCount: 0,
        totalValue: 0,
        chargeThisMonth: 0,
        chargeLastMonth: 0,
        chargeThisYear: 0,
        chargeLastYear: 0
      },
      deptList: []
    }
  },
  mounted() {
    this.loadSummary()
  },
  methods: {
    formatMoney(v) {
      if (v == null || v === '') return '0.00'
      const n = Number(v)
      return isNaN(n) ? '0.00' : n.toFixed(2)
    },
    goDeptDetail(useDept) {
      if (!useDept) return
      this.$router.push({ path: '/custom/keyStats/dept', query: { useDept } })
    },
    loadSummary() {
      getKeyStatsSummary().then(res => {
        const data = res.data || {}
        this.summary = data.summary || this.summary
        this.deptList = data.deptList || []
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
    font-size: 22px;
    font-weight: 600;
    color: #303133;
  }
  .card-unit {
    font-size: 12px;
    color: #909399;
  }
}
.mt16 {
  margin-top: 16px;
}
</style>
