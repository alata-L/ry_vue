import request from '@/utils/request'

/** 一级页：全院汇总 + 按科室表格 */
export function getKeyStatsSummary() {
  return request({
    url: '/custom/keyStats/summary',
    method: 'get'
  })
}

/** 首页：全院重点设备今年 vs 去年 按月序列（收费与诊治例数） */
export function getKeyStatsSummarySeries() {
  return request({
    url: '/custom/keyStats/summarySeries',
    method: 'get'
  })
}

/** 首页：重点设备今年收费 TOP N */
export function getKeyStatsTopEquip(limit) {
  return request({
    url: '/custom/keyStats/topEquip',
    method: 'get',
    params: { limit: limit || 10 }
  })
}

/** 二级页：某科室下按设备统计 */
export function getKeyStatsDept(useDept) {
  return request({
    url: '/custom/keyStats/dept',
    method: 'get',
    params: { useDept }
  })
}

/** 三级页：单设备按时段序列，groupBy=day|week|month|year */
export function getKeyStatsEquipSeries(equipId, params) {
  return request({
    url: '/custom/keyStats/equip/' + equipId + '/series',
    method: 'get',
    params: params || {}
  })
}
