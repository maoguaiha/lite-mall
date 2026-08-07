<template>
  <div class="product-detail-page">
    <div class="main-content">
      <div class="product-container">
        <div class="product-images">
          <div class="main-image">
            <img :src="product.pic || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=iPhone%2015%20Pro%20smartphone%20product%20photo%20white%20background&image_size=square'" :alt="product.name" />
          </div>
          <div class="thumbnails">
            <div 
              v-for="(pic, index) in product.pics" 
              :key="index" 
              class="thumbnail"
              :class="{ active: currentPicIndex === index }"
              @click="currentPicIndex = index"
            >
              <img :src="pic || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=electronic%20product%20detail%20photo%20white%20background&image_size=square'" :alt="'图片' + (index + 1)" />
            </div>
          </div>
        </div>
        
        <div class="product-info">
          <h1 class="product-name">{{ product.name }}</h1>
          <p class="product-subtitle">{{ product.subTitle }}</p>
          
          <div class="price-section">
            <div class="price">
              <span class="currency">¥</span>
              <span class="amount">{{ product.price }}</span>
            </div>
            <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
            <span v-if="product.discount" class="discount">限时 {{ product.discount }} 折</span>
          </div>
          
          <div class="stock-section">
            <span>库存：</span>
            <span :class="product.stock > 0 ? 'in-stock' : 'out-of-stock'">
              {{ product.stock > 0 ? `有货 (${product.stock})` : '缺货' }}
            </span>
          </div>
          
          <div class="sku-section">
            <span class="label">规格：</span>
            <div class="sku-options">
              <div 
                v-for="sku in product.skus" 
                :key="sku.id"
                class="sku-option"
                :class="{ active: selectedSku === sku.id }"
                @click="selectSku(sku)"
              >
                {{ sku.name }}
              </div>
            </div>
          </div>
          
          <div class="quantity-section">
            <span class="label">数量：</span>
            <div class="quantity-control">
              <el-button size="small" @click="decreaseQuantity">-</el-button>
              <span class="quantity">{{ quantity }}</span>
              <el-button size="small" @click="increaseQuantity">+</el-button>
            </div>
          </div>
          
          <div class="action-buttons">
            <el-button type="primary" size="large" @click="addToCart">加入购物车</el-button>
            <el-button type="danger" size="large" @click="buyNow">立即购买</el-button>
          </div>
        </div>
      </div>
      
      <div class="product-tabs">
        <el-tabs v-model="activeTab" type="border-card">
          <el-tab-pane label="商品详情" name="detail">
            <div class="detail-content">
              <h3>商品描述</h3>
              <p>{{ product.description }}</p>
              <h3>商品参数</h3>
              <table class="params-table">
                <tr v-for="(value, key) in product.params" :key="key">
                  <td class="param-name">{{ key }}</td>
                  <td class="param-value">{{ value }}</td>
                </tr>
              </table>
            </div>
          </el-tab-pane>
          <el-tab-pane label="用户评价" name="reviews">
            <div class="review-list">
              <div v-for="review in product.reviews" :key="review.id" class="review-item">
                <div class="review-header">
                  <span class="reviewer">{{ review.nickname }}</span>
                  <span class="review-date">{{ review.createTime }}</span>
                </div>
                <div class="review-rating">
                  <Star v-for="i in 5" :key="i" :class="{ active: i <= review.rating }" />
                </div>
                <p class="review-content">{{ review.content }}</p>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ShoppingCart, User, Search, Star } from '@element-plus/icons-vue'
import { getProductDetail } from '@/api/product'
import { addCartItem } from '@/api/cart'

const router = useRouter()
const route = useRoute()
const searchText = ref('')
const searchVisible = ref(false)
const searchInputRef = ref(null)
const activeTab = ref('detail')
const currentPicIndex = ref(0)
const selectedSku = ref(null)
const quantity = ref(1)
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

const product = reactive({
  id: null,
  name: '',
  subTitle: '',
  price: 0,
  originalPrice: 0,
  discount: null,
  stock: 0,
  pic: '',
  pics: [],
  description: '',
  params: {},
  skus: [],
  reviews: []
})

onMounted(() => {
  loadProductDetail()
})

function loadProductDetail() {
  const id = route.params.id
  getProductDetail(id).then(res => {
    Object.assign(product, res.data)
    if (product.skus.length > 0) {
      selectedSku.value = product.skus[0].id
    }
  }).catch(() => {
    Object.assign(product, {
      id: id,
      name: 'iPhone 15 Pro Max',
      subTitle: '全新A17 Pro芯片，钛金属设计，专业级影像系统',
      price: 9999,
      originalPrice: 10999,
      discount: 9.1,
      stock: 100,
      pic: '',
      pics: ['', '', ''],
      description: 'iPhone 15 Pro Max 采用全新钛金属设计，搭载A17 Pro芯片，支持USB-C接口，配备专业级影像系统。',
      params: {
        '品牌': 'Apple',
        '型号': 'iPhone 15 Pro Max',
        '屏幕尺寸': '6.7英寸',
        '处理器': 'A17 Pro',
        '内存': '8GB',
        '存储': '256GB',
        '摄像头': '4800万像素'
      },
      skus: [
        { id: 1, name: '蓝色钛金属' },
        { id: 2, name: '白色钛金属' },
        { id: 3, name: '黑色钛金属' },
        { id: 4, name: '原色钛金属' }
      ],
      reviews: [
        { id: 1, nickname: '用户123', createTime: '2026-07-15', rating: 5, content: '非常好的手机，性能强劲，拍照效果一流！' },
        { id: 2, nickname: '数码达人', createTime: '2026-07-10', rating: 5, content: '钛金属手感很好，比之前的不锈钢轻很多。' },
        { id: 3, nickname: '科技爱好者', createTime: '2026-07-05', rating: 4, content: '整体满意，就是价格有点贵。' }
      ]
    })
  })
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

function goToLogin() {
  router.push('/login')
}

function selectSku(sku) {
  selectedSku.value = sku.id
}

function decreaseQuantity() {
  if (quantity.value > 1) {
    quantity.value--
  }
}

function increaseQuantity() {
  if (quantity.value < product.stock) {
    quantity.value++
  }
}

function addToCart() {
  if (!selectedSku.value) {
    alert('请选择规格')
    return
  }
  
  addCartItem({
    productId: product.id,
    skuId: selectedSku.value,
    quantity: quantity.value
  }).then(() => {
    alert('添加成功')
    router.push('/cart')
  }).catch(err => {
    console.error(err)
    alert('添加失败')
  })
}

function buyNow() {
  if (!selectedSku.value) {
    alert('请选择规格')
    return
  }
  
  router.push({
    path: '/order/create',
    query: {
      productId: product.id,
      skuId: selectedSku.value,
      quantity: quantity.value
    }
  })
}
</script>

<style scoped>
.product-detail-page {
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

.featured-desc {
  font-size: 12px;
  color: #999;
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

.product-container {
  display: flex;
  gap: 30px;
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
}

.product-images {
  width: 500px;
}

.main-image {
  width: 500px;
  height: 500px;
  background: #f5f5f7;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  margin-bottom: 15px;
  
  img {
    max-width: 100%;
    max-height: 100%;
    object-fit: cover;
  }
}

.thumbnails {
  display: flex;
  gap: 10px;
}

.thumbnail {
  width: 80px;
  height: 80px;
  background: #f5f5f7;
  border: 2px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover, &.active {
    border-color: #2AB795;
  }
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 6px;
  }
}

.product-info {
  flex: 1;
}

.product-name {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.product-subtitle {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: 15px;
  margin-bottom: 20px;
}

.price {
  display: flex;
  align-items: baseline;
  
  .currency {
    font-size: 20px;
    color: #FB0017;
    font-weight: bold;
  }
  
  .amount {
    font-size: 40px;
    color: #FB0017;
    font-weight: bold;
  }
}

.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
}

.discount {
  font-size: 14px;
  color: #fff;
  background: #FB0017;
  padding: 3px 8px;
  border-radius: 4px;
}

.stock-section {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
  
  .in-stock {
    color: #67c23a;
  }
  
  .out-of-stock {
    color: #f56c6c;
  }
}

.sku-section {
  margin-bottom: 20px;
  
  .label {
    font-size: 14px;
    color: #666;
    margin-right: 10px;
  }
}

.sku-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.sku-option {
  padding: 10px 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    border-color: #2AB795;
  }
  
  &.active {
    border-color: #2AB795;
    background: #f0f9eb;
    color: #2AB795;
  }
}

.quantity-section {
  margin-bottom: 30px;
  
  .label {
    font-size: 14px;
    color: #666;
    margin-right: 10px;
  }
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 15px;
  
  .quantity {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    min-width: 40px;
    text-align: center;
  }
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.product-tabs {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.detail-content {
  padding: 30px;
}

.detail-content h3 {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
}

.detail-content p {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 30px;
}

.params-table {
  width: 100%;
  border-collapse: collapse;
  
  tr {
    border-bottom: 1px solid #eee;
  }
  
  td {
    padding: 12px 0;
    font-size: 14px;
  }
  
  .param-name {
    width: 20%;
    color: #999;
  }
  
  .param-value {
    color: #333;
  }
}

.review-list {
  padding: 30px;
}

.review-item {
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
  
  &:last-child {
    border-bottom: none;
    margin-bottom: 0;
  }
}

.review-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  
  .reviewer {
    font-weight: bold;
    color: #333;
  }
  
  .review-date {
    color: #999;
    font-size: 12px;
  }
}

.review-rating {
  margin-bottom: 10px;
  
  .el-icon-star {
    color: #e4e4e4;
    
    &.active {
      color: #FD994B;
    }
  }
}

.review-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}
</style>
