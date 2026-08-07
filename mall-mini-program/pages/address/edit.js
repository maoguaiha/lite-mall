const api = require('../../utils/api')

function emptyForm() {
  return {
    id: '',
    name: '',
    phone: '',
    region: ['', '', ''],
    regionText: '请选择省 / 市 / 区',
    detailAddress: '',
    isDefault: false
  }
}

Page({
  data: {
    form: emptyForm(),
    isEdit: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ isEdit: true })
      wx.setNavigationBarTitle({ title: '编辑地址' })
      wx.showLoading({ title: '加载中' })
      api.getAddress(options.id)
        .then((a) => {
          const region = [a.province || '', a.city || '', a.district || '']
          this.setData({
            form: {
              id: a.id,
              name: a.name || '',
              phone: a.phone || '',
              region,
              regionText: region.filter(Boolean).join(' ') || '请选择省 / 市 / 区',
              detailAddress: a.detailAddress || '',
              isDefault: a.isDefault === 1
            }
          })
        })
        .catch(() => wx.showToast({ title: '加载失败', icon: 'none' }))
        .then(() => wx.hideLoading())
    } else {
      wx.setNavigationBarTitle({ title: '新增地址' })
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ ['form.' + field]: e.detail.value })
  },

  onRegionChange(e) {
    const region = e.detail.value
    this.setData({
      'form.region': region,
      'form.regionText': region.join(' ')
    })
  },

  onToggleDefault(e) {
    this.setData({ 'form.isDefault': e.detail.value })
  },

  onSave() {
    const f = this.data.form
    const name = f.name.trim()
    const phone = f.phone.trim()
    const detail = f.detailAddress.trim()
    if (!name) return wx.showToast({ title: '请填写收货人', icon: 'none' })
    if (!/^1\d{10}$/.test(phone)) return wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
    if (!f.region[0] || !f.region[1] || !f.region[2]) return wx.showToast({ title: '请选择所在地区', icon: 'none' })
    if (!detail) return wx.showToast({ title: '请填写详细地址', icon: 'none' })

    const payload = {
      name,
      phone,
      province: f.region[0],
      city: f.region[1],
      district: f.region[2],
      detailAddress: detail,
      isDefault: f.isDefault ? 1 : 0
    }
    if (this.data.isEdit) payload.id = f.id

    wx.showLoading({ title: '保存中' })
    const req = this.data.isEdit ? api.updateAddress(payload) : api.createAddress(payload)
    req
      .then(() => {
        wx.showToast({ title: '已保存', icon: 'success' })
        setTimeout(() => wx.navigateBack(), 600)
      })
      .catch((err) => wx.showToast({ title: err.message || '保存失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  }
})
