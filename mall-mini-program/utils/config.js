/**
 * 全局配置
 *
 * 注意：微信小程序正式环境要求请求域名必须为 HTTPS 且在
 * 「微信公众平台 -> 开发 -> 开发设置 -> 服务器域名」中配置。
 * 开发阶段可在「微信开发者工具 -> 详情 -> 本地设置」勾选
 * 「不校验合法域名、TLS 版本以及 HTTPS 证书」。
 *
 * 请将 BASE_URL / IMG_BASE 修改为你的后端实际地址。
 * 后端 mall-portal 通过 nginx 网关（18088）暴露，接口前缀为 /api；
 * 与 Web 前台共用同一入口，避免直连后端端口时缺少 /api 前缀而 403。
 * 如需直连后端（且自行处理 /api 前缀），可改回 http://localhost:8080/api。
 */
const BASE_URL = 'http://localhost:18088/api'
// 静态资源（商品图片等）根地址，图片路径通常以 / 开头
const IMG_BASE = 'http://localhost:18088'

module.exports = {
  BASE_URL,
  IMG_BASE
}
