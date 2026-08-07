import request from '@/utils/axios'

export default {
  list: () => request({ url: '/seckill/list', method: 'get' }).then((r) => r.data),
  buy: (seckillProductId) =>
    request({ url: '/seckill/buy', method: 'post', params: { seckillProductId } }).then((r) => r.data)
}
