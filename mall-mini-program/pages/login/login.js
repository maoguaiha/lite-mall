const api = require('../../utils/api')
const auth = require('../../utils/auth')

Page({
  data: {
    username: '',
    password: ''
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [field]: e.detail.value })
  },

  onLogin() {
    const { username, password } = this.data
    if (!username.trim()) return wx.showToast({ title: '请输入用户名', icon: 'none' })
    if (!password) return wx.showToast({ title: '请输入密码', icon: 'none' })

    wx.showLoading({ title: '登录中' })
    api.login({ username: username.trim(), password })
      .then((token) => {
        wx.setStorageSync('token', token)
        return api.getMemberInfo()
      })
      .then((member) => {
        if (member) wx.setStorageSync('member', member)
        wx.showToast({ title: '登录成功', icon: 'success' })
        setTimeout(() => wx.switchTab({ url: '/pages/profile/profile' }), 600)
      })
      .catch((err) => wx.showToast({ title: err.message || '登录失败', icon: 'none' }))
      .then(() => wx.hideLoading())
  },

  goRegister() {
    wx.navigateTo({ url: '/pages/register/register' })
  },

  // 微信一键登录：wx.login 获取 code，再请求后端换取 token
  onWechatLogin() {
    wx.showLoading({ title: '登录中' })
    wx.login({
      success: (res) => {
        if (!res.code) {
          wx.hideLoading()
          return wx.showToast({ title: '获取登录凭证失败', icon: 'none' })
        }
        api.loginByWeixin(res.code)
          .then((token) => {
            wx.setStorageSync('token', token)
            return api.getMemberInfo()
          })
          .then((member) => {
            if (member) wx.setStorageSync('member', member)
            wx.showToast({ title: '登录成功', icon: 'success' })
            setTimeout(() => wx.switchTab({ url: '/pages/profile/profile' }), 600)
          })
          .catch((err) => wx.showToast({ title: err.message || '微信登录失败', icon: 'none' }))
          .then(() => wx.hideLoading())
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '微信登录失败', icon: 'none' })
      }
    })
  }
})
