const api = require('../../utils/api')

Page({
  data: {
    username: '',
    password: '',
    phone: '',
    nickname: ''
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [field]: e.detail.value })
  },

  onRegister() {
    const { username, password, phone, nickname } = this.data
    if (!username.trim()) return wx.showToast({ title: '请输入用户名', icon: 'none' })
    if (!password) return wx.showToast({ title: '请输入密码', icon: 'none' })
    if (!/^1\d{10}$/.test(phone.trim())) return wx.showToast({ title: '请输入正确的手机号', icon: 'none' })

    wx.showLoading({ title: '注册中' })
    api.register({
      username: username.trim(),
      password,
      phone: phone.trim(),
      nickname: nickname.trim() || username.trim()
    })
      .then(() => {
        wx.showToast({ title: '注册成功', icon: 'success' })
        setTimeout(() => {
          wx.redirectTo({ url: '/pages/login/login' })
        }, 700)
      })
      .catch((err) => wx.showToast({ title: err.message || '注册失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  },

  goLogin() {
    wx.navigateBack()
  }
})
