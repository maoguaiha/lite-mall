const api = require('../../utils/api')
const { formatImage, formatPrice } = require('../../utils/util')
const auth = require('../../utils/auth')

Page({
  data: {
    list: [],
    loading: true
  },

  onShow() {
    if (!auth.isLogin()) {
      wx.redirectTo({ url: '/pages/login/login' })
      return
    }
    this.loadList()
  },

  loadList() {
    this.setData({ loading: true })
    api.favoriteList()
      .then((list) => {
        const items = (list || []).map((f) => ({
          favId: f.id,
          productId: f.productId,
          name: f.productName,
          price: formatPrice(f.productPrice),
          img: formatImage(f.productPic)
        }))
        this.setData({ list: items })
      })
      .catch(() => wx.showToast({ title: '加载失败', icon: 'none' }))
      .then(() => this.setData({ loading: false }))
  },

  goProduct(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/product/detail?id=' + id })
  },

  onRemove(e) {
    const id = e.currentTarget.dataset.id
    wx.showLoading({ title: '删除中' })
    api.favoriteDelete(id)
      .then(() => this.loadList())
      .catch((err) => wx.showToast({ title: err.message || '删除失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  }
})
