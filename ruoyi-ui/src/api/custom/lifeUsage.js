import request from '@/utils/request'

export function listLifeUsage(query) {
  return request({
    url: '/custom/lifeUsage/list',
    method: 'get',
    params: query
  })
}

export function getLifeUsage(id) {
  return request({
    url: '/custom/lifeUsage/' + id,
    method: 'get'
  })
}

export function addLifeUsage(data) {
  return request({
    url: '/custom/lifeUsage',
    method: 'post',
    data: data
  })
}

export function updateLifeUsage(data) {
  return request({
    url: '/custom/lifeUsage',
    method: 'put',
    data: data
  })
}

export function delLifeUsage(ids) {
  return request({
    url: '/custom/lifeUsage/' + ids,
    method: 'delete'
  })
}

export function sumLifeUsage(params) {
  return request({
    url: '/custom/lifeUsage/sum',
    method: 'get',
    params: params
  })
}
