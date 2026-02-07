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
