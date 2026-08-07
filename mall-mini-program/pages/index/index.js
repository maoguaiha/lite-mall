const api = require('../../utils/api')
const { formatImage, formatPrice } = require('../../utils/util')

function mapProduct(p) {
  return {
    id: p.id,
    name: p.name,
    subtitle: p.subtitle || '',
    price: formatPrice(p.price),
    sales: p.sales || 0,
    img: formatImage(p.mainImage)
  }
}

Page({
  data: {
    keyword: '',
    banners: [
      { id: 1, title: '新人专享', sub: '注册即享会员价', bg: 'linear-gradient(135deg,#2ab795,#3fd6a8)' },
      { id: 2, title: '新品上架', sub: '潮流好物抢先购', bg: 'linear-gradient(135deg,#ff9a44,#ff6a3d)' },
      { id: 3, title: '限时特惠', sub: '每日精选低价好货', bg: 'linear-gradient(135deg,#5b8def,#3a5fd9)' }
    ],
    categories: [],
    recommend: [],
    news: []
  },

  onLoad() {
    this.loadData()
  },

  onPullDownRefresh() {
    this.loadData(() => wx.stopPullDownRefresh())
  },

  onShow() {
    // 登录态变化后无需刷新首页，但确保分类等已加载
  },

  loadData(done) {
    wx.showLoading({ title: '加载中' })
    Promise.all([
      api.getCategoryList(0).catch(() => []),
      api.getRecommendProducts().catch(() => []),
      api.getNewProducts().catch(() => [])
    ]).then(([categories, recommend, news]) => {
      this.setData({
        categories: (categories || []).map((c) => ({
          id: c.id,
          name: c.name,
          img: formatImage(c.icon)
        })),
        recommend: (recommend || []).map(mapProduct),
        news: (news || []).map(mapProduct)
      })
    }).catch(() => {
      wx.showToast({ title: '加载失败', icon: 'none' })
    }).then(() => {
      wx.hideLoading()
      if (done) done()
    })
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onSearch() {
    const keyword = this.data.keyword.trim()
    wx.navigateTo({
      url: '/pages/product/list?keyword=' + encodeURIComponent(keyword)
    })
  },

  goCategory(e) {
    const id = e.currentTarget.dataset.id
    const name = e.currentTarget.dataset.name
    wx.navigateTo({
      url: '/pages/product/list?categoryId=' + id + '&name=' + encodeURIComponent(name || '')
    })
  },

  goProduct(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/product/detail?id=' + id })
  },

  goCategoryPage() {
    wx.switchTab({ url: '/pages/category/category' })
  },

  goCart() {
    wx.switchTab({ url: '/pages/cart/cart' })
  }
})
