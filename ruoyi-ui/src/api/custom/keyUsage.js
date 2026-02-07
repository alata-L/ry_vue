import request from '@/utils/request'

export function listKeyUsage(query) {
  return request({
    url: '/custom/keyUsage/list',
    method: 'get',
    params: query
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
