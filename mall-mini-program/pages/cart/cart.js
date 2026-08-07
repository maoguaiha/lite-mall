const api = require('../../utils/api')
const { formatImage, formatPrice } = require('../../utils/util')
const auth = require('../../utils/auth')
const app = getApp()

Page({
  data: {
    items: [],
    allChecked: true,
    total: '0.00',
    selectedCount: 0,
    isLogin: false
  },

  onShow() {
    this.setData({ isLogin: auth.isLogin() })
    if (auth.isLogin()) {
      this.loadCart()
    }
  },

  onPullDownRefresh() {
    if (auth.isLogin()) {
      this.loadCart(() => wx.stopPullDownRefresh())
    } else {
      wx.stopPullDownRefresh()
    }
  },

  loadCart(done) {
    api.getCartList()
      .then((list) => {
        const items = (list || []).map((it) => ({
          id: it.id,
          productId: it.productId,
          productName: it.productName,
          skuAttributes: it.skuAttributes || '',
          price: formatPrice(it.productPrice),
          quantity: it.quantity,
          img: formatImage(it.productImage),
          checked: true
        }))
        this.setData({ items })
        this.recompute()
      })
      .catch(() => wx.showToast({ title: '加载失败', icon: 'none' }))
      .then(() => { if (done) done() })
  },

  recompute() {
    let total = 0
    let count = 0
    let allChecked = this.data.items.length > 0
    this.data.items.forEach((it) => {
      if (it.checked) {
        total += Number(it.price) * it.quantity
        count += it.quantity
      } else {
        allChecked = false
      }
    })
    this.setData({
      total: formatPrice(total),
      selectedCount: count,
      allChecked
    })
    this.refreshBadge()
  },

  onCheckItem(e) {
    const id = e.currentTarget.dataset.id
    const items = this.data.items.map((it) => {
      if (it.id === id) return Object.assign({}, it, { checked: !it.checked })
      return it
    })
    this.setData({ items })
    this.recompute()
  },

  onCheckAll() {
    const next = !this.data.allChecked
    const items = this.data.items.map((it) => Object.assign({}, it, { checked: next }))
    this.setData({ items, allChecked: next })
    this.recompute()
  },

  onMinus(e) {
    const id = e.currentTarget.dataset.id
    const item = this.data.items.find((it) => it.id === id)
    if (!item) return
    if (item.quantity <= 1) return
    this.changeQuantity(id, item.quantity - 1)
  },

  onPlus(e) {
    const id = e.currentTarget.dataset.id
    const item = this.data.items.find((it) => it.id === id)
    if (!item) return
    this.changeQuantity(id, item.quantity + 1)
  },

  onQuantityInput(e) {
    const id = e.currentTarget.dataset.id
    let q = parseInt(e.detail.value, 10) || 1
    if (q < 1) q = 1
    this.changeQuantity(id, q)
  },

  changeQuantity(id, quantity) {
    const items = this.data.items.map((it) => {
      if (it.id === id) return Object.assign({}, it, { quantity })
      return it
    })
    this.setData({ items })
    this.recompute()
    api.updateCart(id, quantity).catch(() => {
      wx.showToast({ title: '更新失败', icon: 'none' })
      this.loadCart()
    })
  },

  onDelete(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定删除该商品吗？',
      success: (res) => {
        if (res.confirm) {
          api.deleteCart(id)
            .then(() => {
              const items = this.data.items.filter((it) => it.id !== id)
              this.setData({ items })
              this.recompute()
              wx.showToast({ title: '已删除', icon: 'success' })
            })
            .catch(() => wx.showToast({ title: '删除失败', icon: 'none' }))
        }
      }
    })
  },

  onCheckout() {
    const ids = this.data.items.filter((it) => it.checked).map((it) => it.id)
    if (ids.length === 0) {
      wx.showToast({ title: '请选择商品', icon: 'none' })
      return
    }
    app.globalData.checkoutIds = ids
    wx.navigateTo({ url: '/pages/order/create' })
  },

  refreshBadge() {
    const count = this.data.items.reduce((sum, it) => sum + it.quantity, 0)
    if (count > 0) {
      wx.setTabBarBadge({ index: 2, text: count > 99 ? '99+' : String(count) })
    } else {
      wx.removeTabBarBadge({ index: 2 })
    }
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  },

  goShopping() {
    wx.switchTab({ url: '/pages/index/index' })
  }
})
