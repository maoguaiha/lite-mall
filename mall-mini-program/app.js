const { initMember } = require('./utils/auth')

App({
  globalData: {
    // 结算页选中的购物车项（页面间临时传递）
    checkoutItems: [],
    // 跳转结算页携带的购物车项 id 列表
    checkoutIds: []
  },

  onLaunch() {
    // 启动时尝试恢复登录态
    initMember()
  }
})
