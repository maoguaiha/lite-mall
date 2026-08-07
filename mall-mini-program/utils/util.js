const { IMG_BASE } = require('./config')

/**
 * 处理图片地址：
 * - 已为完整 http(s) 链接的保持不变
 * - 以 / 开头的相对路径拼接资源根地址
 * - 其它情况返回占位图
 */
function formatImage(url) {
  if (!url) return ''
  if (url.indexOf('http://') === 0 || url.indexOf('https://') === 0) {
    return url
  }
  if (url.indexOf('/') === 0) {
    return IMG_BASE + url
  }
  return IMG_BASE + '/' + url
}

/**
 * 金额格式化：分 -> 元，保留两位
 */
function formatPrice(price) {
  const n = Number(price || 0)
  return n.toFixed(2)
}

/**
 * 千分位
 */
function thousands(num) {
  const n = Number(num || 0)
  return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

module.exports = {
  formatImage,
  formatPrice,
  thousands
}
