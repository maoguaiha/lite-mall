import request from '@/utils/axios'

export function addCartItem(data) {
  return request({
    url: '/cart/add',
    method: 'post',
    data
  })
}

export function getCartList() {
  return request({
    url: '/cart/list',
    method: 'get'
  })
}

export function updateCartItem(data) {
  return request({
    url: '/cart/update',
    method: 'post',
    data
  })
}

export function deleteCartItem(id) {
  return request({
    url: `/cart/delete/${id}`,
    method: 'post'
  })
}

export function clearCart() {
  return request({
    url: '/cart/clear',
    method: 'post'
  })
}
