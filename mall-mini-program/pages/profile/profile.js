const auth = require('../../utils/auth')

const ORDER_ENTRIES = [
  { label: '待付款', status: 0, icon: '💰' },
  { label: '待发货', status: 1, icon: '📦' },
  { label: '待收货', status: 2, icon: '🚚' },
  { label: '已完成', status: 3, icon: '✅' }
]

Page({
  data: {
    isLogin: false,
    member: null,
    orders: ORDER_ENTRIES
  },

  onShow() {
    const isLogin = auth.isLogin()
    this.setData({ isLogin })
    if (isLogin) {
      auth.fetchMember().then((member) => {
        this.setData({ member })
      })
    } else {
      this.setData({ member: null })
    }
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  goRegister() {
    wx.navigateTo({ url: '/pages/register/register' })
  },

  goAddress() {
    wx.navigateTo({ url: '/pages/address/list' })
  },

  goFavorite() {
    wx.navigateTo({ url: '/pages/favorite/list' })
  },

  goOrders(e) {
    if (!this.data.isLogin) {
      return this.goLogin()
    }
    const status = e.currentTarget.dataset.status
    wx.navigateTo({ url: '/pages/order/list?status=' + status })
  },

  goAllOrders() {
    if (!this.data.isLogin) return this.goLogin()
    wx.navigateTo({ url: '/pages/order/list' })
  },

  onLogout() {
    wx.showModal({
      title: '提示',
      content: '确定退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          auth.logout()
          wx.removeTabBarBadge({ index: 2 })
          this.setData({ isLogin: false, member: null })
          wx.showToast({ title: '已退出', icon: 'success' })
        }
      }
    })
  }
})
