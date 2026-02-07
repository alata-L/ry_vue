import request from '@/utils/request'

/** 首页：生命支持类设备按类型使用趋势（今年按月） */
export function getLifeUsageTrend() {
  return request({
    url: '/custom/lifeStats/usageTrend',
    method: 'get'
  })
}

/** 首页：生命支持类设备科室使用台数排名（今年） */
export function getLifeUsageByDept() {
  return request({
    url: '/custom/lifeStats/usageByDept',
    method: 'get'
  })
}

/** 首页：获取设备台数统计（按类型） */
export function getLifeEquipCountByType() {
  return request({
    url: '/custom/lifeStats/equipCountByType',
    method: 'get'
  })
}

/** 首页：全院使用趋势（按日、月、年，按设备类型，日均数据） */
export function getLifeUsageTrendByType(equipType, groupBy) {
  return request({
    url: '/custom/lifeStats/usageTrendByType',
    method: 'get',
    params: { equipType, groupBy: groupBy || 'day' }
  })
}

/** 首页：科室排名TOP5（按日、月、年，按设备类型，日均数据） */
export function getLifeUsageDeptRankByType(equipType, groupBy) {
  return request({
    url: '/custom/lifeStats/usageDeptRankByType',
    method: 'get',
    params: { equipType, groupBy: groupBy || 'day' }
  })
}