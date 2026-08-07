const api = require('../../utils/api')
const { formatImage, formatPrice } = require('../../utils/util')
const auth = require('../../utils/auth')
const app = getApp()

Page({
  data: {
    id: null,
    product: {},
    images: [],
    detail: '',
    skuList: [],          // 原始 sku（含 attrs 对象）
    specKeys: [],         // [{ key, values: [v1, v2] }]
    selected: {},         // { key: value }
    currentSku: null,     // 当前命中的 sku
    price: '0.00',
    stock: 0,
    quantity: 1,
    cartCount: 0,
    isFav: false
  },

  onLoad(options) {
    const id = options.id
    this.setData({ id })
    this.loadDetail(id)
    this.checkFavorite(id)
    this.refreshCartBadge()
  },

  checkFavorite(id) {
    if (!auth.isLogin()) return
    api.favoriteCheck(id)
      .then((fav) => this.setData({ isFav: !!fav }))
      .catch(() => {})
  },

  toggleFavorite() {
    if (!auth.isLogin()) {
      this.goLogin()
      return
    }
    const id = this.data.id
    const wasFav = this.data.isFav
    const req = wasFav ? api.favoriteDelete(id) : api.favoriteAdd(id)
    wx.showLoading({ title: '处理中' })
    req
      .then(() => {
        this.setData({ isFav: !wasFav })
        wx.showToast({ title: wasFav ? '已取消收藏' : '收藏成功', icon: 'none' })
      })
      .catch((err) => wx.showToast({ title: err.message || '操作失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  },

  onShow() {
    this.refreshCartBadge()
  },

  loadDetail(id) {
    wx.showLoading({ title: '加载中' })
    api.getProductDetail(id)
      .then((res) => {
        const product = res.product || {}
        const skuList = (res.skuList || []).map((s) => {
          let attrs = {}
          try {
            attrs = s.attributes ? JSON.parse(s.attributes) : {}
          } catch (e) {
            attrs = {}
          }
          return Object.assign({}, s, { attrs })
        })
        // 商品主图 + 副图
        let imgs = []
        if (product.subImages) {
          imgs = product.subImages.split(',').map((u) => formatImage(u.trim())).filter(Boolean)
        }
        if (imgs.length === 0 && product.mainImage) {
          imgs = [formatImage(product.mainImage)]
        }
        if (imgs.length === 0) imgs = ['']

        // 规格维度
        const specMap = {}
        skuList.forEach((s) => {
          Object.keys(s.attrs).forEach((k) => {
            if (!specMap[k]) specMap[k] = []
            if (specMap[k].indexOf(s.attrs[k]) === -1) specMap[k].push(s.attrs[k])
          })
        })
        const specKeys = Object.keys(specMap).map((k) => ({ key: k, values: specMap[k], selectedValue: '' }))

        // 默认选中第一个 sku 的规格
        const selected = {}
        let currentSku = null
        if (skuList.length > 0) {
          const first = skuList[0]
          Object.keys(first.attrs).forEach((k) => { selected[k] = first.attrs[k] })
          const idx = specKeys.findIndex((s) => s.key === k)
          if (idx > -1) specKeys[idx].selectedValue = first.attrs[k]
          currentSku = first
        }

        this.setData({
          product: {
            id: product.id,
            name: product.name,
            subtitle: product.subtitle || '',
            price: formatPrice(product.price),
            stock: product.stock || 0,
            sales: product.sales || 0
          },
          images: imgs,
          detail: product.detail || '',
          skuList,
          specKeys,
          selected,
          currentSku,
          price: currentSku ? formatPrice(currentSku.price) : formatPrice(product.price),
          stock: currentSku ? (currentSku.stock || 0) : (product.stock || 0)
        })
      })
      .catch(() => wx.showToast({ title: '加载失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  },

  // 选择规格值
  onSelectSpec(e) {
    const { key, value } = e.currentTarget.dataset
    const selected = Object.assign({}, this.data.selected)
    selected[key] = value
    // 同步渲染态
    const specKeys = this.data.specKeys.map((s) => {
      if (s.key === key) return Object.assign({}, s, { selectedValue: value })
      return s
    })
    // 命中 sku
    const currentSku = this.matchSku(selected)
    this.setData({
      selected,
      specKeys,
      currentSku,
      price: currentSku ? formatPrice(currentSku.price) : this.data.product.price,
      stock: currentSku ? (currentSku.stock || 0) : this.data.product.stock
    })
  },

  matchSku(selected) {
    const keys = Object.keys(selected)
    return this.data.skuList.find((s) => {
      return keys.every((k) => s.attrs[k] === selected[k])
    }) || null
  },

  onMinus() {
    if (this.data.quantity > 1) {
      this.setData({ quantity: this.data.quantity - 1 })
    }
  },

  onPlus() {
    const stock = this.data.stock
    if (this.data.quantity < stock) {
      this.setData({ quantity: this.data.quantity + 1 })
    } else {
      wx.showToast({ title: '库存不足', icon: 'none' })
    }
  },

  onQuantityInput(e) {
    let q = parseInt(e.detail.value, 10) || 1
    if (q > this.data.stock) q = this.data.stock
    if (q < 1) q = 1
    this.setData({ quantity: q })
  },

  addToCart() {
    if (!auth.isLogin()) {
      this.goLogin()
      return
    }
    if (this.data.skuList.length > 0 && !this.data.currentSku) {
      wx.showToast({ title: '请选择规格', icon: 'none' })
      return
    }
    if (this.data.stock < 1) {
      wx.showToast({ title: '商品缺货', icon: 'none' })
      return
    }
    const params = {
      productId: this.data.id,
      quantity: this.data.quantity
    }
    if (this.data.currentSku) {
      params.productSkuId = this.data.currentSku.id
    }
    wx.showLoading({ title: '提交中' })
    api.addCart(params)
      .then(() => {
        wx.showToast({ title: '已加入购物车', icon: 'success' })
        this.refreshCartBadge()
      })
      .catch((err) => wx.showToast({ title: err.message || '加入失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  },

  buyNow() {
    if (!auth.isLogin()) {
      this.goLogin()
      return
    }
    if (this.data.skuList.length > 0 && !this.data.currentSku) {
      wx.showToast({ title: '请选择规格', icon: 'none' })
      return
    }
    if (this.data.stock < 1) {
      wx.showToast({ title: '商品缺货', icon: 'none' })
      return
    }
    const params = {
      productId: this.data.id,
      quantity: this.data.quantity
    }
    if (this.data.currentSku) params.productSkuId = this.data.currentSku.id
    wx.showLoading({ title: '提交中' })
    api.addCart(params)
      .then(() => api.getCartList())
      .then((list) => {
        const target = (list || []).find((it) => {
          return it.productId === this.data.id &&
            (this.data.currentSku ? it.productSkuId === this.data.currentSku.id : !it.productSkuId)
        })
        if (target) {
          app.globalData.checkoutIds = [target.id]
          wx.navigateTo({ url: '/pages/order/create' })
        } else {
          wx.switchTab({ url: '/pages/cart/cart' })
        }
      })
      .catch((err) => wx.showToast({ title: err.message || '下单失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  },

  refreshCartBadge() {
    if (!auth.isLogin()) {
      wx.removeTabBarBadge({ index: 2 })
      return
    }
    api.getCartList()
      .then((list) => {
        const count = (list || []).reduce((sum, it) => sum + (it.quantity || 0), 0)
        this.setData({ cartCount: count })
        if (count > 0) {
          wx.setTabBarBadge({ index: 2, text: count > 99 ? '99+' : String(count) })
        } else {
          wx.removeTabBarBadge({ index: 2 })
        }
      })
      .catch(() => {})
  },

  goCart() {
    wx.switchTab({ url: '/pages/cart/cart' })
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' })
  }
})
