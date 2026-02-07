import request from '@/utils/request'

export function listKeyEquip(query) {
  return request({
    url: '/custom/keyEquip/list',
    method: 'get',
    params: query
  })
}

export function getKeyEquip(id) {
  return request({
    url: '/custom/keyEquip/' + id,
    method: 'get'
  })
}

export function addKeyEquip(data) {
  return request({
    url: '/custom/keyEquip',
    method: 'post',
    data: data
  })
}

export function updateKeyEquip(data) {
  return request({
    url: '/custom/keyEquip',
    method: 'put',
    data: data
  })
}

export function delKeyEquip(ids) {
  return request({
    url: '/custom/keyEquip/' + ids,
    method: 'delete'
  })
}

export function exportKeyEquip(query) {
  return request({
    url: '/custom/keyEquip/export',
    method: 'post',
    params: query
  })
}

export function importTemplate() {
  return request({
    url: '/custom/keyEquip/importTemplate',
    method: 'post'
  })
}