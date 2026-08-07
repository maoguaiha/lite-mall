import request from '@/utils/axios'

export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

export function getOrderList(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

export function getOrderDetail(id) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

export function cancelOrder(id) {
  return request({
    url: `/order/cancel/${id}`,
    method: 'post'
  })
}

export function payOrder(id) {
  return request({
    url: `/order/pay/${id}`,
    method: 'post'
  })
}
