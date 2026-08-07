import request from '@/utils/axios'

export function login(data) {
  return request({
    url: '/member/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/member/register',
    method: 'post',
    data
  })
}

export function getMemberInfo() {
  return request({
    url: '/member/info',
    method: 'get'
  })
}

export function updatePassword(data) {
  return request({
    url: '/member/updatePassword',
    method: 'post',
    params: data
  })
}

export function updateMember(data) {
  return request({
    url: '/member/update',
    method: 'post',
    data
  })
}
