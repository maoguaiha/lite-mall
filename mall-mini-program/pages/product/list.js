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
    categoryId: '',
    name: '',
    products: [],
    pageNum: 1,
    pageSize: 10,
    totalPage: 1,
    loading: false,
    finished: false
  },

  onLoad(options) {
    const categoryId = options.categoryId || ''
    const keyword = options.keyword || ''
    const name = options.name || (categoryId ? '分类商品' : '搜索结果')
    wx.setNavigationBarTitle({ title: keyword ? '搜索：' + keyword : name })
    this.setData({ categoryId, keyword, name })
    this.loadProducts(true)
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onSearch() {
    this.setData({ categoryId: '', products: [], pageNum: 1, finished: false })
    wx.setNavigationBarTitle({ title: this.data.keyword ? '搜索：' + this.data.keyword : '搜索结果' })
    this.loadProducts(true)
  },

  loadProducts(reset) {
    if (this.data.loading) return
    const pageNum = reset ? 1 : this.data.pageNum
    this.setData({ loading: true })

    const params = {
      pageNum,
      pageSize: this.data.pageSize
    }
    if (this.data.categoryId) params.categoryId = this.data.categoryId
    if (this.data.keyword) params.keyword = this.data.keyword

    api.getProductList(params)
      .then((res) => {
        const list = (res && res.list) || []
        const products = (reset ? [] : this.data.products).concat(list.map(mapProduct))
        const totalPage = (res && res.totalPage) || 1
        this.setData({
          products,
          pageNum: pageNum + 1,
          totalPage,
          finished: pageNum >= totalPage
        })
      })
      .catch(() => {
        wx.showToast({ title: '加载失败', icon: 'none' })
      })
      .then(() => this.setData({ loading: false }))
  },

  onReachBottom() {
    if (!this.data.finished && !this.data.loading) {
      this.loadProducts(false)
    }
  },

  goProduct(e) {
    wx.navigateTo({ url: '/pages/product/detail?id=' + e.currentTarget.dataset.id })
  }
})
