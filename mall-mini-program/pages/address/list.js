const api = require('../../utils/api')
const app = getApp()

function mapAddr(a) {
  const region = [a.province, a.city, a.district].filter(Boolean)
  return {
    id: a.id,
    name: a.name,
    phone: a.phone,
    province: a.province || '',
    city: a.city || '',
    district: a.district || '',
    detailAddress: a.detailAddress || '',
    isDefault: a.isDefault === 1,
    fullAddress: region.concat(a.detailAddress || '').filter(Boolean).join(' ')
  }
}

Page({
  data: {
    addresses: [],
    selectMode: false
  },

  onLoad(options) {
    this.setData({ selectMode: options.mode === 'select' })
    wx.setNavigationBarTitle({ title: this.data.selectMode ? '选择收货地址' : '收货地址' })
  },

  onShow() {
    this.loadAddresses()
  },

  loadAddresses() {
    api.getAddressList()
      .then((list) => this.setData({ addresses: (list || []).map(mapAddr) }))
      .catch(() => wx.showToast({ title: '加载失败', icon: 'none' }))
  },

  onSelect(e) {
    if (!this.data.selectMode) return
    const id = e.currentTarget.dataset.id
    const addr = this.data.addresses.find((a) => a.id === id)
    if (!addr) return
    app.globalData.selectedAddress = addr
    wx.navigateBack()
  },

  goEdit(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: '/pages/address/edit' + (id ? '?id=' + id : '') })
  },

  goAdd() {
    wx.navigateTo({ url: '/pages/address/edit' })
  },

  onSetDefault(e) {
    const id = e.currentTarget.dataset.id
    wx.showLoading({ title: '设置中' })
    api.setDefaultAddress(id)
      .then(() => this.loadAddresses())
      .catch((err) => wx.showToast({ title: err.message || '操作失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  },

  onDelete(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定删除该地址吗？',
      success: (res) => {
        if (res.confirm) {
          wx.showLoading({ title: '删除中' })
          api.deleteAddress(id)
            .then(() => this.loadAddresses())
            .catch((err) => wx.showToast({ title: err.message || '删除失败', icon: 'none' }))
            .then(() => wx.hideLoading())
        }
      }
    })
  }
})
