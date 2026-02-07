<template>
  <div class="app-container">
    <el-page-header @back="goBack" :content="pageTitle" class="page-header" />
    <div v-if="!useDept" class="empty-tip">请从统计页选择科室进入</div>
    <template v-else>
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
    </template>
  </div>
</template>

<script>
import { getKeyStatsDept } from '@/api/custom/keyStats'

export default {
  name: 'KeyStatsDeptDetail',
  data() {
    return {
      useDept: '',
      equipList: []
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
        if (this.useDept) this.loadDeptEquip()
      }
    }
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
.mt16 {
  margin-top: 16px;
}
</style>
