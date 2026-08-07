const api = require('../../utils/api')
const { formatImage, formatPrice } = require('../../utils/util')

const STATUS_TEXT = {
  0: '待付款',
  1: '待发货',
  2: '待收货',
  3: '已完成',
  4: '已取消'
}

Page({
  data: {
    id: null,
    order: {},
    items: []
  },

  onLoad(options) {
    this.setData({ id: options.id })
    this.loadDetail(options.id)
  },

  loadDetail(id) {
    wx.showLoading({ title: '加载中' })
    api.getOrderDetail(id)
      .then((res) => {
        const o = res.order || {}
        const order = {
          id: o.id,
          orderSn: o.orderSn,
          status: o.status,
          statusText: STATUS_TEXT[o.status] || '未知',
          totalAmount: formatPrice(o.totalAmount),
          payAmount: formatPrice(o.payAmount),
          freightAmount: formatPrice(o.freightAmount || 0),
          receiverName: o.receiverName || '',
          receiverPhone: o.receiverPhone || '',
          address: [o.receiverProvince, o.receiverCity, o.receiverDistrict, o.receiverDetailAddress]
            .filter(Boolean).join(' '),
          createTime: o.createTime || ''
        }
        const items = (res.orderItems || []).map((it) => ({
          id: it.id,
          productName: it.productName,
          productImage: it.productImage,
          img: formatImage(it.productImage),
          price: formatPrice(it.productPrice),
          quantity: it.quantity,
          totalPrice: formatPrice(it.totalPrice)
        }))
        this.setData({ order, items })
      })
      .catch(() => wx.showToast({ title: '加载失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  },

  onPay() {
    wx.showLoading({ title: '支付中' })
    api.payOrder(this.data.id)
      .then(() => {
        wx.showToast({ title: '支付成功', icon: 'success' })
        this.loadDetail(this.data.id)
      })
      .catch((err) => wx.showToast({ title: err.message || '支付失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  },

  onCancel() {
    wx.showModal({
      title: '提示',
      content: '确定取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          api.cancelOrder(this.data.id)
            .then(() => { wx.showToast({ title: '已取消', icon: 'success' }); this.loadDetail(this.data.id) })
            .catch((err) => wx.showToast({ title: err.message || '操作失败', icon: 'none' }))
        }
      }
    })
  },

  onConfirm() {
    wx.showModal({
      title: '提示',
      content: '确认已收到商品？',
      success: (res) => {
        if (res.confirm) {
          api.confirmOrder(this.data.id)
            .then(() => { wx.showToast({ title: '已确认收货', icon: 'success' }); this.loadDetail(this.data.id) })
            .catch((err) => wx.showToast({ title: err.message || '操作失败', icon: 'none' }))
        }
      }
    })
  }
})
