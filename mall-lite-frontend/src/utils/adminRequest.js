import axios from 'axios'

// 商家端（mall-admin）独立 axios 实例，统一走 /admin-api 代理到 :8081
const adminRequest = axios.create({
  baseURL: '/admin-api',
  timeout: 10000
})

adminRequest.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('adminToken')
    if (token) {
      config.headers.Authorization = 'Bearer ' + token
    }
    return config
  },
  (error) => Promise.reject(error)
)

adminRequest.interceptors.response.use(
  (res) => {
    const data = res.data
    // 商家端统一返回 CommonResult { code, message, data }
    if (data && typeof data.code !== 'undefined' && data.code !== 200) {
      if (data.code === 401 || data.code === 403) {
        localStorage.removeItem('adminToken')
        window.location.hash = '#/admin/login'
      }
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data
  },
  (error) => Promise.reject(error)
)

export default adminRequest
