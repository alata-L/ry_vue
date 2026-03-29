import request from '@/utils/request'

export function listKeyUsage(query) {
  return request({
    url: '/custom/keyUsage/list',
    method: 'get',
    params: query
  })
}

/** 上报弹窗：按科室查询重点设备选项（仅需 keyUsage 权限） */
export function listKeyEquipsForUsage(useDept) {
  return request({
    url: '/custom/keyUsage/options/keyEquips',
    method: 'get',
    params: { useDept }
  })
}

export function getKeyUsage(id) {
  return request({
    url: '/custom/keyUsage/' + id,
    method: 'get'
  })
}

export function addKeyUsage(data) {
  return request({
    url: '/custom/keyUsage',
    method: 'post',
    data: data
  })
}

export function updateKeyUsage(data) {
  return request({
    url: '/custom/keyUsage',
    method: 'put',
    data: data
  })
}

export function delKeyUsage(ids) {
  return request({
    url: '/custom/keyUsage/' + ids,
    method: 'delete'
  })
}

export function listKeyUsageHistory(query) {
  return request({
    url: '/custom/keyUsage/history/list',
    method: 'get',
    params: query
  })
}
