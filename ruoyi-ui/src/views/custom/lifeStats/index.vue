<template>
  <div class="app-container">
    <div class="stats-section">
      <h2 class="stats-title">全院生命支持类设备统计</h2>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="监护仪" name="1">
          <!-- 监护仪统计卡片 -->
          <el-row :gutter="16" class="summary-cards">
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card">
                <div class="card-label">6年以下</div>
                <div class="card-value">{{ getSummaryByType('1').cnt0 }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card">
                <div class="card-label">6年以上</div>
                <div class="card-value">{{ getSummaryByType('1').cnt6 }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card">
                <div class="card-label">10年以上</div>
                <div class="card-value">{{ getSummaryByType('1').cnt10 }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card">
                <div class="card-label">15年以上</div>
                <div class="card-value">{{ getSummaryByType('1').cnt15 }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card total">
                <div class="card-label">合计</div>
                <div class="card-value">{{ getSummaryByType('1').total }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
          </el-row>
          <!-- 监护仪表格 -->
          <el-table :data="getTableDataByType('1')" border size="small" class="mt16">
            <el-table-column label="使用科室" prop="useDept" min-width="140">
              <template slot-scope="scope">
                <el-link type="primary" :underline="false" @click="goDeptDetail(scope.row.useDept)">{{ scope.row.useDept }}</el-link>
              </template>
            </el-table-column>
            <el-table-column label="6年以下" prop="cnt0" width="100" align="right" />
            <el-table-column label="6年以上" prop="cnt6" width="100" align="right" />
            <el-table-column label="10年以上" prop="cnt10" width="100" align="right" />
            <el-table-column label="15年以上" prop="cnt15" width="100" align="right" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="输液泵" name="2">
          <!-- 输液泵统计卡片 -->
          <el-row :gutter="16" class="summary-cards">
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card">
                <div class="card-label">6年以下</div>
                <div class="card-value">{{ getSummaryByType('2').cnt0 }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card">
                <div class="card-label">6年以上</div>
                <div class="card-value">{{ getSummaryByType('2').cnt6 }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card">
                <div class="card-label">10年以上</div>
                <div class="card-value">{{ getSummaryByType('2').cnt10 }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card">
                <div class="card-label">15年以上</div>
                <div class="card-value">{{ getSummaryByType('2').cnt15 }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card total">
                <div class="card-label">合计</div>
                <div class="card-value">{{ getSummaryByType('2').total }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
          </el-row>
          <!-- 输液泵表格 -->
          <el-table :data="getTableDataByType('2')" border size="small" class="mt16">
            <el-table-column label="使用科室" prop="useDept" min-width="140">
              <template slot-scope="scope">
                <el-link type="primary" :underline="false" @click="goDeptDetail(scope.row.useDept)">{{ scope.row.useDept }}</el-link>
              </template>
            </el-table-column>
            <el-table-column label="6年以下" prop="cnt0" width="100" align="right" />
            <el-table-column label="6年以上" prop="cnt6" width="100" align="right" />
            <el-table-column label="10年以上" prop="cnt10" width="100" align="right" />
            <el-table-column label="15年以上" prop="cnt15" width="100" align="right" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="注射泵" name="3">
          <!-- 注射泵统计卡片 -->
          <el-row :gutter="16" class="summary-cards">
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card">
                <div class="card-label">6年以下</div>
                <div class="card-value">{{ getSummaryByType('3').cnt0 }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card">
                <div class="card-label">6年以上</div>
                <div class="card-value">{{ getSummaryByType('3').cnt6 }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card">
                <div class="card-label">10年以上</div>
                <div class="card-value">{{ getSummaryByType('3').cnt10 }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card">
                <div class="card-label">15年以上</div>
                <div class="card-value">{{ getSummaryByType('3').cnt15 }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
            <el-col :span="4">
              <el-card shadow="hover" class="summary-card total">
                <div class="card-label">合计</div>
                <div class="card-value">{{ getSummaryByType('3').total }}</div>
                <div class="card-unit">台</div>
              </el-card>
            </el-col>
          </el-row>
          <!-- 注射泵表格 -->
          <el-table :data="getTableDataByType('3')" border size="small" class="mt16">
            <el-table-column label="使用科室" prop="useDept" min-width="140">
              <template slot-scope="scope">
                <el-link type="primary" :underline="false" @click="goDeptDetail(scope.row.useDept)">{{ scope.row.useDept }}</el-link>
              </template>
            </el-table-column>
            <el-table-column label="6年以下" prop="cnt0" width="100" align="right" />
            <el-table-column label="6年以上" prop="cnt6" width="100" align="right" />
            <el-table-column label="10年以上" prop="cnt10" width="100" align="right" />
            <el-table-column label="15年以上" prop="cnt15" width="100" align="right" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import { statsYears, statsYearsByType, statsDeptType } from '@/api/custom/lifeEquip'

export default {
  name: 'LifeStats',
  dicts: ['cst_life_equip_type'],
  data() {
    return {
      activeTab: '1',
      years6List: [],
      years10List: [],
      years15List: [],
      years6ListByType: [],
      years10ListByType: [],
      years15ListByType: [],
      allEquipList: [] // 所有设备（用于计算总数和6年以下）
    }
  },
  mounted() {
    this.loadYearsAll()
  },
  methods: {
    // 按设备类型获取统计汇总
    getSummaryByType(equipType) {
      const list6 = (this.years6ListByType || []).filter(r => r.equipType === equipType)
      const list10 = (this.years10ListByType || []).filter(r => r.equipType === equipType)
      const list15 = (this.years15ListByType || []).filter(r => r.equipType === equipType)
      const listAll = (this.allEquipList || []).filter(r => r.equipType === equipType)
      
      const total = listAll.reduce((s, r) => s + (Number(r.cnt) || 0), 0)
      const s6 = list6.reduce((s, r) => s + (Number(r.cnt) || 0), 0)
      const s10 = list10.reduce((s, r) => s + (Number(r.cnt) || 0), 0)
      const s15 = list15.reduce((s, r) => s + (Number(r.cnt) || 0), 0)
      
      return {
        cnt0: Math.max(0, total - s6), // 6年以下
        cnt6: Math.max(0, s6 - s10), // 6年以上、10年以下
        cnt10: Math.max(0, s10 - s15), // 10年以上、15年以下
        cnt15: s15, // 15年以上
        total: total // 合计
      }
    },
    goDeptDetail(useDept) {
      if (!useDept) return
      this.$router.push({ path: '/custom/lifeStats/dept', query: { useDept } })
    },
    loadYearsAll() {
      Promise.all([
        statsYears(6),
        statsYears(10),
        statsYears(15),
        statsYearsByType(6),
        statsYearsByType(10),
        statsYearsByType(15),
        statsDeptType()
      ]).then(([r6, r10, r15, r6Type, r10Type, r15Type, rAll]) => {
        this.years6List = r6.data || []
        this.years10List = r10.data || []
        this.years15List = r15.data || []
        this.years6ListByType = r6Type.data || []
        this.years10ListByType = r10Type.data || []
        this.years15ListByType = r15Type.data || []
        this.allEquipList = rAll.data || []
      })
    },
    getTableDataByType(equipType) {
      const map6 = {}
      const map10 = {}
      const map15 = {}
      const mapAll = {}
      
      // 按设备类型过滤数据
      ;(this.years6ListByType || []).filter(r => r.equipType === equipType).forEach(r => {
        map6[r.useDept] = Number(r.cnt) || 0
      })
      ;(this.years10ListByType || []).filter(r => r.equipType === equipType).forEach(r => {
        map10[r.useDept] = Number(r.cnt) || 0
      })
      ;(this.years15ListByType || []).filter(r => r.equipType === equipType).forEach(r => {
        map15[r.useDept] = Number(r.cnt) || 0
      })
      ;(this.allEquipList || []).filter(r => r.equipType === equipType).forEach(r => {
        mapAll[r.useDept] = Number(r.cnt) || 0
      })
      
      const depts = new Set([...Object.keys(map6), ...Object.keys(map10), ...Object.keys(map15), ...Object.keys(mapAll)])
      return Array.from(depts).sort().map(useDept => {
        const mAll = mapAll[useDept] || 0
        const m6 = map6[useDept] || 0
        const m10 = map10[useDept] || 0
        const m15 = map15[useDept] || 0
        return {
          useDept,
          cnt0: Math.max(0, mAll - m6), // 6年以下
          cnt6: Math.max(0, m6 - m10), // 6年以上、10年以下
          cnt10: Math.max(0, m10 - m15), // 10年以上、15年以下
          cnt15: m15 // 15年以上
        }
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
