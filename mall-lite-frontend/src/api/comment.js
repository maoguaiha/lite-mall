import request from '@/utils/axios'

export default {
  submit: (data) =>
    request({ url: '/member/comment/submit', method: 'post', data }).then((r) => r.data),
  my: (pageNum = 1, pageSize = 10) =>
    request({ url: '/member/comment/my', method: 'get', params: { pageNum, pageSize } }).then(
      (r) => r.data
    ),
  productList: (productId, pageNum = 1, pageSize = 10) =>
    request({
      url: '/product/comment/list',
      method: 'get',
      params: { productId, pageNum, pageSize }
    }).then((r) => r.data)
}
