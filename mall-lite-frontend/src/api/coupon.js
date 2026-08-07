import request from '@/utils/axios'

export default {
  center: () => request({ url: '/coupon/center', method: 'get' }).then((r) => r.data),
  my: () => request({ url: '/coupon/my', method: 'get' }).then((r) => r.data),
  receive: (couponId) =>
    request({ url: '/coupon/receive', method: 'post', params: { couponId } }).then((r) => r.data),
  use: (couponId, orderId) =>
    request({ url: '/coupon/use', method: 'post', params: { couponId, orderId } }).then(
      (r) => r.data
    )
}
