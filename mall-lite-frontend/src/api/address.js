import request from '@/utils/axios'

export default {
  list: () => request({ url: '/member/address/list', method: 'get' }).then((r) => r.data),
  get: (id) => request({ url: `/member/address/${id}`, method: 'get' }).then((r) => r.data),
  create: (data) =>
    request({ url: '/member/address/create', method: 'post', data }).then((r) => r.data),
  update: (data) =>
    request({ url: '/member/address/update', method: 'post', data }).then((r) => r.data),
  remove: (id) =>
    request({ url: '/member/address/delete', method: 'post', params: { id } }).then((r) => r.data),
  setDefault: (id) =>
    request({ url: '/member/address/default', method: 'post', params: { id } }).then((r) => r.data)
}
