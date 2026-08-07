const { get, post } = require('./request')

/**
 * 会员相关
 */
function register(data) {
  // data: { username, password, phone, nickname }
  return post('/member/register', data)
}

function login(data) {
  // data: { username, password } -> 返回 token 字符串
  return post('/member/login', data)
}

function loginByWeixin(code) {
  // 微信小程序登录：wx.login 得到的 code -> 返回 token 字符串
  // 后端 /member/loginByWeixin 使用 @RequestParam，需走 params
  return post('/member/loginByWeixin', null, { code })
}

function getMemberInfo() {
  return get('/member/info')
}

function updatePassword(oldPassword, newPassword) {
  return post('/member/updatePassword', null, { oldPassword, newPassword })
}

function updateMember(member) {
  return post('/member/update', member)
}

/**
 * 商品相关
 */
function getCategoryList(parentId) {
  return get('/product/category/list', { parentId: parentId || 0 })
}

function getAllCategories() {
  return get('/product/category/all')
}

function getProductList(params) {
  // params: { categoryId, keyword, pageNum, pageSize }
  return get('/product/list', params)
}

function getRecommendProducts() {
  return get('/product/recommend')
}

function getNewProducts() {
  return get('/product/new')
}

function getProductDetail(id) {
  // 注意：后端真实路径为 /product/detail/{id}
  return get('/product/detail/' + id)
}

/**
 * 购物车相关
 * add / update 使用 @RequestParam，需走 params
 */
function getCartList() {
  return get('/cart/list')
}

function addCart(params) {
  // params: { productId, productSkuId, quantity }
  return post('/cart/add', null, params)
}

function updateCart(id, quantity) {
  return post('/cart/update/' + id, null, { quantity })
}

function deleteCart(id) {
  return post('/cart/delete/' + id)
}

function deleteCartBatch(ids) {
  return post('/cart/delete/batch', ids)
}

function clearCart() {
  return post('/cart/clear')
}

/**
 * 订单相关
 */
function createOrder(data) {
  // data: { cartItemIds, receiverName, receiverPhone,
  //         receiverProvince, receiverCity, receiverDistrict, receiverDetailAddress }
  return post('/order/create', data)
}

function getOrderList(params) {
  // params: { status, pageNum, pageSize }
  return get('/order/list', params)
}

function getOrderDetail(id) {
  return get('/order/detail/' + id)
}

function payOrder(id) {
  return post('/order/pay/' + id)
}

function cancelOrder(id) {
  return post('/order/cancel/' + id)
}

function confirmOrder(id) {
  return post('/order/confirm/' + id)
}

/**
 * 支付相关
 */
function createPay(orderId) {
  return post('/pay/create', null, { orderId })
}

function getPayStatus(orderId) {
  return get('/pay/status/' + orderId)
}

/**
 * 收货地址相关
 */
function getAddressList() {
  return get('/member/address/list')
}
function getAddress(id) {
  return get('/member/address/' + id)
}
function createAddress(data) {
  // data: { name, phone, province, city, district, detailAddress, isDefault }
  return post('/member/address/create', data)
}
function updateAddress(data) {
  // data: { id, name, phone, province, city, district, detailAddress, isDefault }
  return post('/member/address/update', data)
}
function deleteAddress(id) {
  return post('/member/address/delete', null, { id })
}
function setDefaultAddress(id) {
  return post('/member/address/default', null, { id })
}

/**
 * 商品收藏相关
 */
function favoriteAdd(productId) {
  return post('/member/favorite/add', null, { productId })
}
function favoriteDelete(productId) {
  return post('/member/favorite/delete', null, { productId })
}
function favoriteList() {
  return get('/member/favorite/list')
}
function favoriteCheck(productId) {
  return get('/member/favorite/check', { productId })
}

module.exports = {
  register,
  login,
  loginByWeixin,
  getMemberInfo,
  updatePassword,
  updateMember,
  getAddressList,
  getAddress,
  createAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress,
  favoriteAdd,
  favoriteDelete,
  favoriteList,
  favoriteCheck,
  getCategoryList,
  getAllCategories,
  getProductList,
  getRecommendProducts,
  getNewProducts,
  getProductDetail,
  getCartList,
  addCart,
  updateCart,
  deleteCart,
  deleteCartBatch,
  clearCart,
  createOrder,
  getOrderList,
  getOrderDetail,
  payOrder,
  cancelOrder,
  confirmOrder,
  createPay,
  getPayStatus
}
