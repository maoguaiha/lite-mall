import request from '@/utils/axios'

export function getProductList(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

export function getProductDetail(id) {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

export function getCategoryList(params) {
  return request({
    url: '/product/category/list',
    method: 'get',
    params
  })
}

export function getCategoryAll() {
  return request({
    url: '/product/category/all',
    method: 'get'
  })
}
