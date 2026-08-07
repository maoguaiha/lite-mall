const api = require('../../utils/api')
const { formatImage, formatPrice } = require('../../utils/util')
const auth = require('../../utils/auth')
const app = getApp()

Page({
  data: {
    items: [],
    total: '0.00',
    receiverName: '',
    receiverPhone: '',
    region: ['', '', ''],
    receiverDetailAddress: '',
    regionText: '请选择省 / 市 / 区',
    submitting: false,
    selectedAddressId: ''
  },

  onLoad() {
    if (!auth.isLogin()) {
      wx.redirectTo({ url: '/pages/login/login' })
      return
    }
    const ids = app.globalData.checkoutIds || []
    if (!ids.length) {
      wx.showToast({ title: '未选择商品', icon: 'none' })
      setTimeout(() => wx.navigateBack(), 800)
      return
    }
    this.loadItems(ids)
  },

  onShow() {
    const sel = app.globalData.selectedAddress
    if (sel) {
      this.setData({
        receiverName: sel.name,
        receiverPhone: sel.phone,
        region: [sel.province, sel.city, sel.district],
        regionText: [sel.province, sel.city, sel.district].filter(Boolean).join(' '),
        receiverDetailAddress: sel.detailAddress,
        selectedAddressId: sel.id
      })
      app.globalData.selectedAddress = null
    }
  },

  goSelectAddress() {
    wx.navigateTo({ url: '/pages/address/list?mode=select' })
  },

  loadItems(ids) {
    wx.showLoading({ title: '加载中' })
    api.getCartList()
      .then((list) => {
        const idSet = ids.map(String)
        const items = (list || [])
          .filter((it) => idSet.indexOf(String(it.id)) > -1)
          .map((it) => ({
            id: it.id,
            productId: it.productId,
            productName: it.productName,
            skuAttributes: it.skuAttributes || '',
            price: formatPrice(it.productPrice),
            quantity: it.quantity,
            img: formatImage(it.productImage)
          }))
        let total = 0
        items.forEach((it) => { total += Number(it.price) * it.quantity })
        this.setData({ items, total: formatPrice(total) })
        if (items.length === 0) {
          wx.showToast({ title: '商品已失效', icon: 'none' })
        }
      })
      .catch(() => wx.showToast({ title: '加载失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [field]: e.detail.value })
  },

  onRegionChange(e) {
    const region = e.detail.value
    this.setData({
      region,
      regionText: region.join(' ')
    })
  },

  onSubmit() {
    const { receiverName, receiverPhone, region, receiverDetailAddress, items } = this.data
    if (!receiverName.trim()) {
      return wx.showToast({ title: '请填写收货人', icon: 'none' })
    }
    if (!/^1\d{10}$/.test(receiverPhone.trim())) {
      return wx.showToast({ title: '请填写正确的手机号', icon: 'none' })
    }
    if (!region[0] || !region[1] || !region[2]) {
      return wx.showToast({ title: '请选择所在地区', icon: 'none' })
    }
    if (!receiverDetailAddress.trim()) {
      return wx.showToast({ title: '请填写详细地址', icon: 'none' })
    }
    if (items.length === 0) {
      return wx.showToast({ title: '订单为空', icon: 'none' })
    }

    const data = {
      cartItemIds: items.map((it) => it.id),
      addressId: this.data.selectedAddressId || undefined,
      receiverName: receiverName.trim(),
      receiverPhone: receiverPhone.trim(),
      receiverProvince: region[0],
      receiverCity: region[1],
      receiverDistrict: region[2],
      receiverDetailAddress: receiverDetailAddress.trim()
    }

    this.setData({ submitting: true })
    wx.showLoading({ title: '提交中' })
    api.createOrder(data)
      .then(() => {
        // 清空结算态并删除已下单的购物车项（忽略失败，进入购物车页会重新拉取）
        app.globalData.checkoutIds = []
        api.deleteCartBatch(items.map((it) => it.id)).catch(() => {})
        wx.removeTabBarBadge({ index: 2 })
        wx.showToast({ title: '下单成功', icon: 'success' })
        setTimeout(() => {
          wx.redirectTo({ url: '/pages/order/list?status=0' })
        }, 800)
      })
      .catch((err) => wx.showToast({ title: err.message || '下单失败', icon: 'none' }))
      .then(() => {
        this.setData({ submitting: false })
        wx.hideLoading()
      })
  }
})
