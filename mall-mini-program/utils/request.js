const { BASE_URL } = require('./config')

/**
 * 封装 wx.request，返回 Promise
 * - options.url: 接口路径（不含 BASE_URL）
 * - options.method: GET/POST/...
 * - options.params: 拼接到 URL 的查询参数（@RequestParam 用）
 * - options.data: 请求体（@RequestBody 用，JSON）
 */
function request(options) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    let url = BASE_URL + options.url

    // 拼接查询参数
    if (options.params) {
      const query = []
      Object.keys(options.params).forEach((key) => {
        const value = options.params[key]
        if (value !== undefined && value !== null && value !== '') {
          query.push(encodeURIComponent(key) + '=' + encodeURIComponent(value))
        }
      })
      if (query.length > 0) {
        url += (url.indexOf('?') > -1 ? '&' : '?') + query.join('&')
      }
    }

    const header = Object.assign(
      { 'Content-Type': 'application/json' },
      token ? { Authorization: 'Bearer ' + token } : {},
      options.header || {}
    )

    wx.request({
      url,
      method: options.method || 'GET',
      data: options.data,
      header,
      success(res) {
        const { statusCode, data } = res
        if (statusCode !== 200) {
          if (statusCode === 401) {
            handleUnauthorized()
          }
          reject(new Error('网络错误 ' + statusCode))
          return
        }
        // 统一响应结构 CommonResult { code, message, data }
        if (data && typeof data.code === 'number') {
          if (data.code === 200) {
            resolve(data.data)
          } else if (data.code === 401) {
            handleUnauthorized()
            reject(new Error(data.message || '登录已过期'))
          } else {
            reject(new Error(data.message || '请求失败'))
          }
        } else {
          // 部分接口返回纯字符串（如支付回调），直接透传
          resolve(data)
        }
      },
      fail(err) {
        reject(new Error(err.errMsg || '网络请求失败'))
      }
    })
  })
}

let redirectingLogin = false
function handleUnauthorized() {
  wx.removeStorageSync('token')
  wx.removeStorageSync('member')
  // 已在登录页则不再重复跳转，避免死循环
  const pages = getCurrentPages()
  const current = pages[pages.length - 1]
  const onLogin = current && current.route && current.route.indexOf('login') > -1
  if (onLogin || redirectingLogin) return
  redirectingLogin = true
  wx.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
  setTimeout(() => {
    wx.reLaunch({ url: '/pages/login/login' })
    redirectingLogin = false
  }, 1200)
}

function get(url, params, header) {
  return request({ url, method: 'GET', params, header })
}

function post(url, data, params, header) {
  return request({ url, method: 'POST', data, params, header })
}

module.exports = {
  request,
  get,
  post
}
