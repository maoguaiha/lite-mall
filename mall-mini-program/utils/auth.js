const { get } = require('./request')

/**
 * 从本地缓存恢复会员信息（启动时调用）
 */
function initMember() {
  const member = wx.getStorageSync('member')
  if (member) {
    return member
  }
  return null
}

/**
 * 是否已登录
 */
function isLogin() {
  return !!wx.getStorageSync('token')
}

/**
 * 拉取最新会员信息（登录后 / 进入「我的」页时调用）
 */
function fetchMember() {
  return get('/member/info')
    .then((member) => {
      if (member) {
        wx.setStorageSync('member', member)
      }
      return member
    })
    .catch(() => {
      wx.removeStorageSync('token')
      wx.removeStorageSync('member')
      return null
    })
}

/**
 * 获取当前会员信息（优先缓存）
 */
function getMember() {
  return wx.getStorageSync('member') || null
}

/**
 * 退出登录
 */
function logout() {
  wx.removeStorageSync('token')
  wx.removeStorageSync('member')
}

module.exports = {
  initMember,
  isLogin,
  fetchMember,
  getMember,
  logout
}
