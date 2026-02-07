import request from '@/utils/request'

export function listLifeEquip(query) {
  return request({
    url: '/custom/lifeEquip/list',
    method: 'get',
    params: query
  })
}

export function getLifeEquip(id) {
  return request({
    url: '/custom/lifeEquip/' + id,
    method: 'get'
  })
}

export function addLifeEquip(data) {
  return request({
    url: '/custom/lifeEquip',
    method: 'post',
    data: data
  })
}

export function updateLifeEquip(data) {
  return request({
    url: '/custom/lifeEquip',
    method: 'put',
    data: data
  })
}

export function delLifeEquip(ids) {
  return request({
    url: '/custom/lifeEquip/' + ids,
    method: 'delete'
  })
}

export function exportLifeEquip(query) {
  return request({
    url: '/custom/lifeEquip/export',
    method: 'post',
    params: query
  })
}

export function exportLifeEquipByType(equipType) {
  return request({
    url: '/custom/lifeEquip/exportByType',
    method: 'post',
    params: { equipType }
  })
}

export function statsDeptType() {
  return request({
    url: '/custom/lifeEquip/stats/deptType',
    method: 'get'
  })
}

export function statsYears(minYears) {
  return request({
    url: '/custom/lifeEquip/stats/years',
    method: 'get',
    params: { minYears }
  })
}
