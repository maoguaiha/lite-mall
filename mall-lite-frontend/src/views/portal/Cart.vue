<template>
  <div class="cart-page">
    <div class="main-content">
      <div class="cart-container">
        <div class="cart-header">
          <h2>购物车</h2>
          <el-button type="danger" @click="handleClear">清空购物车</el-button>
        </div>
        
        <div v-if="cartItems.length === 0" class="empty-cart">
          <ShoppingCart />
          <p>购物车是空的</p>
          <el-button type="primary" @click="goToProductList">去购物</el-button>
        </div>
        
        <div v-else class="cart-list">
          <div class="cart-table">
            <div class="table-header">
              <label class="checkbox">
                <el-checkbox v-model="selectAll" @change="handleSelectAll" />
                <span>全选</span>
              </label>
              <span class="col-product">商品信息</span>
              <span class="col-price">单价</span>
              <span class="col-quantity">数量</span>
              <span class="col-total">小计</span>
              <span class="col-action">操作</span>
            </div>
            
            <div 
              v-for="item in cartItems" 
              :key="item.id" 
              class="table-row"
            >
              <label class="checkbox">
                <el-checkbox v-model="item.selected" @change="handleSelectItem" />
              </label>
              <div class="col-product">
                <div class="product-image" @click="goToDetail(item.productId)">
                  <img :src="item.pic || '/images/placeholder.png'" :alt="item.productName" />
                </div>
                <div class="product-info">
                  <h4 @click="goToDetail(item.productId)">{{ item.productName }}</h4>
                  <p>{{ item.skuName }}</p>
                </div>
              </div>
              <span class="col-price">¥{{ item.price }}</span>
              <span class="col-quantity">
                <div class="quantity-control">
                  <el-button size="small" @click="decreaseQuantity(item)">-</el-button>
                  <span class="quantity">{{ item.quantity }}</span>
                  <el-button size="small" @click="increaseQuantity(item)">+</el-button>
                </div>
              </span>
              <span class="col-total">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
              <span class="col-action">
                <el-button link @click="handleDelete(item.id)">删除</el-button>
              </span>
            </div>
          </div>
        </div>
        
        <div v-if="cartItems.length > 0" class="cart-footer">
          <div class="footer-left">
            <label class="checkbox">
              <el-checkbox v-model="selectAll" @change="handleSelectAll" />
              <span>全选</span>
            </label>
            <span class="selected-count">已选 {{ selectedCount }} 件</span>
          </div>
          <div class="footer-right">
            <span class="total-price">合计：<strong>¥{{ totalPrice.toFixed(2) }}</strong></span>
            <el-button type="danger" size="large" @click="handleCheckout">结算</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ShoppingCart, User, Search } from '@element-plus/icons-vue'
import { getCartList, updateCartItem, deleteCartItem, clearCart } from '@/api/cart'
import { useMemberStore } from '@/stores/member'

const router = useRouter()
const memberStore = useMemberStore()
const searchText = ref('')
const searchVisible = ref(false)
const searchInputRef = ref(null)
const selectAll = ref(false)
const cartItems = ref([])
const activeNav = ref('')
const member = ref(null)
const dropdownVisible = ref(false)
const currentDropdownNav = ref('')
const featuredProducts = ref([])
const accessoriesList = ref([])
let dropdownHideTimer = null

const hotSearches = ['iPhone', 'MacBook', '蓝牙耳机', '护肤品', '运动装备']

const navItems = [
  { name: '首页' },
  { name: '数码', dropdownData: {
    featured: [
      { name: 'iPhone 15 Pro', price: '¥7999起', desc: 'A17芯片', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=iPhone%2015%20Pro%20smartphone%20product%20photo%20white%20background&image_size=square' },
      { name: '华为 Mate 60', price: '¥5499起', desc: '麒麟芯片', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Huawei%20Mate%2060%20smartphone%20product%20photo%20white%20background&image_size=square' },
      { name: '小米 14', price: '¥3999起', desc: '骁龙8 Gen3', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Xiaomi%2014%20smartphone%20product%20photo%20white%20background&image_size=square' },
      { name: 'OPPO Find X7', price: '¥3999起', desc: '天玑9300', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=OPPO%20Find%20X7%20smartphone%20product%20photo%20white%20background&image_size=square' }
    ],
    accessories: ['手机壳', '充电器', '数据线', '蓝牙耳机', '贴膜', '移动电源', '支架', '保护套']
  }},
  { name: '电脑', dropdownData: {
    featured: [
      { name: 'MacBook Pro', price: '¥14999起', desc: 'M3 Pro芯片', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=MacBook%20Pro%20laptop%20product%20photo%20silver%20white%20background&image_size=square' },
      { name: 'ThinkPad X1', price: '¥9999起', desc: '商务旗舰', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=ThinkPad%20X1%20laptop%20product%20photo%20black%20white%20background&image_size=square' },
      { name: '戴尔 XPS 15', price: '¥12999起', desc: '4K触控屏', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Dell%20XPS%2015%20laptop%20product%20photo%20white%20background&image_size=square' },
      { name: '华为 MateBook', price: '¥7999起', desc: '2.5K屏', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Huawei%20MateBook%20laptop%20product%20photo%20silver%20white%20background&image_size=square' }
    ],
    accessories: ['显示器', '机械键盘', '电竞鼠标', '耳机', '散热器', '显卡', '固态硬盘', '内存']
  }},
  { name: '服饰', dropdownData: {
    featured: [
      { name: '纯棉T恤', price: '¥99起', desc: '夏季新款', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cotton%20t-shirt%20fashion%20product%20photo%20white%20background&image_size=square' },
      { name: '牛仔裤', price: '¥199起', desc: '经典版型', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=blue%20jeans%20fashion%20product%20photo%20white%20background&image_size=square' },
      { name: '连衣裙', price: '¥299起', desc: '优雅气质', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=elegant%20dress%20fashion%20product%20photo%20white%20background&image_size=square' },
      { name: '运动外套', price: '¥399起', desc: '防风保暖', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=sports%20jacket%20fashion%20product%20photo%20white%20background&image_size=square' }
    ],
    accessories: ['T恤', '衬衫', '裤子', '外套', '连衣裙', '鞋子', '包包', '配饰']
  }},
  { name: '美妆', dropdownData: {
    featured: [
      { name: 'SK-II神仙水', price: '¥1540起', desc: '护肤精华', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=SK-II%20facial%20treatment%20essence%20bottle%20cosmetic%20product%20photo%20white%20background&image_size=square' },
      { name: '兰蔻小黑瓶', price: '¥1080起', desc: '肌底液', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Lancome%20Advanced%20Genifique%20serum%20bottle%20cosmetic%20product%20photo%20white%20background&image_size=square' },
      { name: '雅诗兰黛眼霜', price: '¥680起', desc: '抗蓝光', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Estee%20Lauder%20eye%20cream%20jar%20cosmetic%20product%20photo%20white%20background&image_size=square' },
      { name: 'MAC口红', price: '¥250起', desc: '经典色号', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=MAC%20lipstick%20cosmetic%20product%20photo%20white%20background&image_size=square' }
    ],
    accessories: ['洁面', '爽肤水', '乳液', '面霜', '口红', '粉底', '眼影', '香水']
  }},
  { name: '运动', dropdownData: {
    featured: [
      { name: '篮球鞋', price: '¥599起', desc: '实战款', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=basketball%20shoes%20sports%20product%20photo%20white%20background&image_size=square' },
      { name: '瑜伽垫', price: '¥199起', desc: '防滑耐用', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=yoga%20mat%20sports%20product%20photo%20white%20background&image_size=square' },
      { name: '运动背包', price: '¥299起', desc: '大容量', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=sports%20backpack%20product%20photo%20white%20background&image_size=square' },
      { name: '跑步鞋', price: '¥499起', desc: '缓震舒适', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=running%20shoes%20sports%20product%20photo%20white%20background&image_size=square' }
    ],
    accessories: ['篮球', '足球', '羽毛球拍', '哑铃', '瑜伽垫', '跑步机', '登山装备', '骑行装备']
  }},
  { name: '图书', dropdownData: {
    featured: [
      { name: '三体全集', price: '¥93起', desc: '科幻经典', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=science%20fiction%20book%20set%20product%20photo%20white%20background&image_size=square' },
      { name: '活着', price: '¥39起', desc: '余华作品', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Chinese%20literature%20book%20product%20photo%20white%20background&image_size=square' },
      { name: '人类简史', price: '¥68起', desc: '畅销读物', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=history%20book%20product%20photo%20white%20background&image_size=square' },
      { name: '原则', price: '¥128起', desc: '投资经典', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=business%20book%20product%20photo%20white%20background&image_size=square' }
    ],
    accessories: ['小说', '经管', '科技', '人文', '教材教辅', '电子书', '有声书', '杂志']
  }}
]

onMounted(() => {
  loadCart()
})

function loadCart() {
  // 未登录时后端 /cart/list 返回 403，直接展示 mock 数据，避免无谓请求
  if (!memberStore.isLoggedIn) {
    useMockCart()
    return
  }
  getCartList().then(res => {
    cartItems.value = res.data || mockCartItems
    cartItems.value.forEach(item => {
      item.selected = true
    })
    updateSelectAll()
  }).catch(() => {
    useMockCart()
  })
}

function useMockCart() {
  cartItems.value = mockCartItems
  cartItems.value.forEach(item => {
    item.selected = true
  })
  updateSelectAll()
}

const mockCartItems = [
  { id: 1, productId: 1, productName: 'iPhone 15 Pro', skuName: '蓝色钛金属', price: 7999, quantity: 1, pic: '' },
  { id: 2, productId: 3, productName: '无线蓝牙耳机', skuName: '白色', price: 599, quantity: 2, pic: '' },
  { id: 3, productId: 5, productName: '机械键盘', skuName: 'RGB背光', price: 399, quantity: 1, pic: '' }
]

function updateSelectAll() {
  selectAll.value = cartItems.value.length > 0 && cartItems.value.every(item => item.selected)
}

function handleSelectAll() {
  cartItems.value.forEach(item => {
    item.selected = selectAll.value
  })
}

function handleSelectItem() {
  updateSelectAll()
}

function decreaseQuantity(item) {
  if (item.quantity > 1) {
    item.quantity--
    updateCartItem({ id: item.id, quantity: item.quantity })
  }
}

function increaseQuantity(item) {
  item.quantity++
  updateCartItem({ id: item.id, quantity: item.quantity })
}

function handleDelete(id) {
  deleteCartItem(id).then(() => {
    cartItems.value = cartItems.value.filter(item => item.id !== id)
    updateSelectAll()
  }).catch(() => {
    cartItems.value = cartItems.value.filter(item => item.id !== id)
    updateSelectAll()
  })
}

function handleClear() {
  clearCart().then(() => {
    cartItems.value = []
    selectAll.value = false
  }).catch(() => {
    cartItems.value = []
    selectAll.value = false
  })
}

function handleCheckout() {
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    alert('请选择商品')
    return
  }
  
  const productIds = selectedItems.map(item => item.productId).join(',')
  router.push(`/order/create?productIds=${productIds}`)
}

function toggleSearch() {
  searchVisible.value = !searchVisible.value
  dropdownVisible.value = false
  if (searchVisible.value) {
    nextTick(() => {
      searchInputRef.value?.focus()
    })
  }
}

function keepSearch() {
  searchVisible.value = true
}

function hideSearch() {
  searchVisible.value = false
}

function handleSearch() {
  searchVisible.value = false
  router.push(`/product/list?keyword=${searchText.value}`)
}

function handleHotSearch(keyword) {
  searchText.value = keyword
  searchVisible.value = false
  router.push(`/product/list?keyword=${keyword}`)
}

function selectNav(item) {
  activeNav.value = item.name
  searchVisible.value = false
  if (item.name === '首页') {
    router.push('/')
  } else {
    router.push(`/product/list?category=${item.name}`)
  }
}

function showDropdown(navName) {
  const navItem = navItems.find(item => item.name === navName)
  if (navItem && navItem.dropdownData) {
    featuredProducts.value = navItem.dropdownData.featured || []
    accessoriesList.value = navItem.dropdownData.accessories || []
    currentDropdownNav.value = navName
    dropdownVisible.value = true
    searchVisible.value = false
  }
}

function scheduleHideDropdown() {
  if (dropdownHideTimer) {
    clearTimeout(dropdownHideTimer)
  }
  dropdownHideTimer = setTimeout(() => {
    dropdownVisible.value = false
  }, 200)
}

function cancelHideDropdown() {
  if (dropdownHideTimer) {
    clearTimeout(dropdownHideTimer)
    dropdownHideTimer = null
  }
}

function handleFeaturedClick(product) {
  dropdownVisible.value = false
  router.push(`/product/list?category=${currentDropdownNav.value}&keyword=${product.name}`)
}

function handleAccessoriesClick(item) {
  dropdownVisible.value = false
  router.push(`/product/list?category=${currentDropdownNav.value}&subCategory=${item}`)
}

function goToHome() {
  router.push('/')
}

function goToCart() {
  router.push('/cart')
}

function goToProductList() {
  router.push('/product/list')
}

function goToDetail(id) {
  router.push(`/product/detail/${id}`)
}

function goToLogin() {
  router.push('/login')
}

const totalCount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).reduce((sum, item) => sum + item.quantity, 0)
})

const totalPrice = computed(() => {
  return cartItems.value.filter(item => item.selected).reduce((sum, item) => sum + item.price * item.quantity, 0)
})
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  background: #f5f5f7;
}

.header {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.header-left {
  width: 100px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  cursor: pointer;
}

.nav {
  display: flex;
  align-items: center;
  gap: 30px;
}

.nav-item {
  font-size: 14px;
  color: #333;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
  
  &:hover {
    color: #2AB795;
  }
  
  &.active {
    color: #2AB795;
  }
  
  &.has-dropdown:hover {
    background: rgba(42, 183, 149, 0.1);
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
  width: 100px;
  justify-content: flex-end;
}

.search-trigger,
.cart-trigger,
.user-trigger {
  cursor: pointer;
  padding: 6px;
  border-radius: 50%;
  transition: all 0.2s;
  
  &:hover {
    background: rgba(0, 0, 0, 0.05);
  }
}

.search-icon,
.cart-icon,
.user-icon {
  font-size: 18px;
  color: #333;
}

.login-btn {
  font-size: 14px;
  padding: 6px 16px;
}

.search-panel {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #fff;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  z-index: 999;
  animation: searchFadeIn 0.2s ease;
}

@keyframes searchFadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.search-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.search-input-wrap {
  display: flex;
  align-items: center;
  background: #f5f5f7;
  border-radius: 10px;
  padding: 12px 15px;
}

.search-icon-small {
  font-size: 16px;
  color: #999;
  margin-right: 10px;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  
  :deep(.el-input__wrapper) {
    border: none;
    box-shadow: none;
    background: transparent;
  }
}

.search-suggestions {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.suggestions-title {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.suggestions-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.suggestion-item {
  font-size: 14px;
  color: #666;
  padding: 8px 15px;
  background: #f5f5f7;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    background: #e8f5f0;
    color: #2AB795;
  }
}

.dropdown-panel {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #fff;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  z-index: 998;
  animation: dropdownFadeIn 0.25s ease;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

@keyframes dropdownFadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-content {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  padding: 25px 30px;
  gap: 60px;
}

.dropdown-column {
  flex: 1;
}

.featured-column {
  flex: 2;
}

.accessories-column {
  flex: 1;
}

.dropdown-title {
  font-size: 12px;
  font-weight: bold;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 18px;
}

.featured-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.featured-item {
  width: calc(50% - 5px);
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.2s;
  
  &:hover {
    background: #f0f9eb;
  }
}

.featured-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 2px;
}

.featured-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 2px;
}

.featured-price {
  font-size: 13px;
  color: #FB0017;
}

.accessories-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.accessories-link {
  font-size: 14px;
  color: #666;
  padding: 12px 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
  
  &:hover {
    color: #2AB795;
    background: #f0f9eb;
  }
}

.main-content {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.cart-container {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  border-bottom: 1px solid #eee;
  
  h2 {
    font-size: 20px;
    font-weight: bold;
    color: #333;
  }
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 0;
  color: #999;
  
  .el-icon-shopping-cart {
    font-size: 64px;
    margin-bottom: 20px;
  }
  
  p {
    font-size: 16px;
    margin-bottom: 20px;
  }
}

.cart-table {
  padding: 20px 30px;
}

.table-header {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  font-size: 14px;
  color: #666;
}

.checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
}

.col-product {
  flex: 2;
  margin-left: 15px;
}

.col-price {
  width: 120px;
  text-align: center;
}

.col-quantity {
  width: 150px;
  text-align: center;
}

.col-total {
  width: 120px;
  text-align: center;
  color: #FB0017;
  font-weight: bold;
}

.col-action {
  width: 80px;
  text-align: center;
}

.table-row {
  display: flex;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  
  &:last-child {
    border-bottom: none;
  }
}

.table-row .col-product {
  display: flex;
  gap: 15px;
  
  .product-image {
    width: 100px;
    height: 100px;
    background: #f5f5f5;
    border-radius: 6px;
    overflow: hidden;
    cursor: pointer;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .product-info {
    flex: 1;
    
    h4 {
      font-size: 16px;
      font-weight: bold;
      color: #333;
      cursor: pointer;
      margin-bottom: 8px;
    }
    
    p {
      font-size: 13px;
      color: #999;
    }
  }
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 10px;
  
  .quantity {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    min-width: 30px;
    text-align: center;
  }
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  background: #fafafa;
  border-top: 1px solid #eee;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
  
  .selected-count {
    font-size: 14px;
    color: #666;
  }
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 20px;
  
  .total-price {
    font-size: 18px;
    color: #333;
    
    strong {
      font-size: 24px;
      color: #FB0017;
    }
  }
}
</style>
