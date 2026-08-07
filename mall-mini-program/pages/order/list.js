const api = require('../../utils/api')
const { formatPrice } = require('../../utils/util')
const auth = require('../../utils/auth')

const STATUS_TEXT = {
  0: '待付款',
  1: '待发货',
  2: '待收货',
  3: '已完成',
  4: '已取消'
}

const TABS = [
  { label: '全部', status: '' },
  { label: '待付款', status: '0' },
  { label: '待发货', status: '1' },
  { label: '待收货', status: '2' },
  { label: '已完成', status: '3' }
]

function mapOrder(o) {
  return {
    id: o.id,
    orderSn: o.orderSn,
    status: o.status,
    statusText: STATUS_TEXT[o.status] || '未知',
    totalAmount: formatPrice(o.payAmount != null ? o.payAmount : o.totalAmount),
    receiverName: o.receiverName || '',
    receiverPhone: o.receiverPhone || '',
    address: [o.receiverProvince, o.receiverCity, o.receiverDistrict, o.receiverDetailAddress]
      .filter(Boolean).join(' ')
  }
}

Page({
  data: {
    tabs: TABS,
    activeTab: 0,
    currentStatus: '',
    orders: [],
    pageNum: 1,
    pageSize: 10,
    totalPage: 1,
    loading: false,
    finished: false,
    isLogin: false
  },

  onLoad(options) {
    let tab = 0
    if (options.status !== undefined && options.status !== '') {
      const idx = TABS.findIndex((t) => t.status === String(options.status))
      if (idx > -1) tab = idx
    }
    this.setData({ activeTab: tab, currentStatus: TABS[tab].status })
    this.setData({ isLogin: auth.isLogin() })
    if (auth.isLogin()) this.loadOrders(true)
  },

  onShow() {
    if (auth.isLogin() && this.data.isLogin) {
      this.loadOrders(true)
    }
  },

  onPullDownRefresh() {
    if (auth.isLogin()) this.loadOrders(true, () => wx.stopPullDownRefresh())
    else wx.stopPullDownRefresh()
  },

  onReachBottom() {
    if (!this.data.finished && !this.data.loading && auth.isLogin()) {
      this.loadOrders(false)
    }
  },

  switchTab(e) {
    const idx = e.currentTarget.dataset.index
    if (idx === this.data.activeTab) return
    const status = TABS[idx].status
    this.setData({ activeTab: idx, currentStatus: status, orders: [], pageNum: 1, finished: false })
    this.loadOrders(true)
  },

  loadOrders(reset, done) {
    if (this.data.loading) return
    const pageNum = reset ? 1 : this.data.pageNum
    this.setData({ loading: true })
    const params = { pageNum, pageSize: this.data.pageSize }
    if (this.data.currentStatus !== '') params.status = this.data.currentStatus

    api.getOrderList(params)
      .then((res) => {
        const list = (res && res.list) || []
        const orders = (reset ? [] : this.data.orders).concat(list.map(mapOrder))
        const totalPage = (res && res.totalPage) || 1
        this.setData({
          orders,
          pageNum: pageNum + 1,
          totalPage,
          finished: pageNum >= totalPage
        })
      })
      .catch(() => wx.showToast({ title: '加载失败', icon: 'none' }))
      .then(() => { this.setData({ loading: false }); if (done) done() })
  },

  goDetail(e) {
    wx.navigateTo({ url: '/pages/order/detail?id=' + e.currentTarget.dataset.id })
  },

  noop() {},

  onPay(e) {
    const id = e.currentTarget.dataset.id
    wx.showLoading({ title: '支付中' })
    // 模拟支付：直接调用支付接口标记已支付
    api.payOrder(id)
      .then(() => {
        wx.showToast({ title: '支付成功', icon: 'success' })
        this.loadOrders(true)
      })
      .catch((err) => wx.showToast({ title: err.message || '支付失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  },

  onCancel(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          api.cancelOrder(id)
            .then(() => { wx.showToast({ title: '已取消', icon: 'success' }); this.loadOrders(true) })
            .catch((err) => wx.showToast({ title: err.message || '操作失败', icon: 'none' }))
        }
      }
    })
  },

  onConfirm(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确认已收到商品？',
      success: (res) => {
        if (res.confirm) {
          api.confirmOrder(id)
            .then(() => { wx.showToast({ title: '已确认收货', icon: 'success' }); this.loadOrders(true) })
            .catch((err) => wx.showToast({ title: err.message || '操作失败', icon: 'none' }))
        }
      }
    })
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  }
})
