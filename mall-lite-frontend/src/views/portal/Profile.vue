<template>
  <div class="profile-page">
    <div class="main-content">
      <div class="profile-container">
        <div class="user-card">
          <div class="user-avatar">
            <img :src="member.avatar || '/images/avatar.png'" :alt="member.nickname" />
          </div>
          <div class="user-info">
            <h2>{{ member.nickname || '用户' }}</h2>
            <p>{{ member.username }}</p>
            <p>{{ member.phone }}</p>
          </div>
          <el-button type="primary" @click="editProfile">编辑资料</el-button>
        </div>
        
        <div class="menu-card">
          <div class="menu-item" @click="goToOrderList">
            <div class="menu-icon">
              <ShoppingBag />
            </div>
            <span class="menu-text">我的订单</span>
            <ArrowRight />
          </div>
          <div class="menu-item" @click="goToFavorites">
            <div class="menu-icon">
              <Star />
            </div>
            <span class="menu-text">我的收藏</span>
            <ArrowRight />
          </div>
          <div class="menu-item" @click="goToAddress">
            <div class="menu-icon">
              <MapLocation />
            </div>
            <span class="menu-text">收货地址</span>
            <ArrowRight />
          </div>
          <div class="menu-item" @click="goToCoupon">
            <div class="menu-icon">
              <Ticket />
            </div>
            <span class="menu-text">我的优惠券</span>
            <ArrowRight />
          </div>
          <div class="menu-item" @click="goToWallet">
            <div class="menu-icon">
              <Wallet />
            </div>
            <span class="menu-text">我的钱包</span>
            <ArrowRight />
          </div>
          <div class="menu-item" @click="goToSettings">
            <div class="menu-icon">
              <Operation />
            </div>
            <span class="menu-text">设置</span>
            <ArrowRight />
          </div>
        </div>
        
        <div class="order-status-card">
          <h3>订单状态</h3>
          <div class="status-list">
            <div class="status-item" @click="goToOrderList('pending')">
              <div class="status-icon pending">
                <Clock />
              </div>
              <span class="status-text">待付款</span>
              <span v-if="orderCounts.pending > 0" class="status-count">{{ orderCounts.pending }}</span>
            </div>
            <div class="status-item" @click="goToOrderList('shipped')">
              <div class="status-icon shipped">
                <Bicycle />
              </div>
              <span class="status-text">待发货</span>
              <span v-if="orderCounts.shipped > 0" class="status-count">{{ orderCounts.shipped }}</span>
            </div>
            <div class="status-item" @click="goToOrderList('received')">
              <div class="status-icon received">
                <Box />
              </div>
              <span class="status-text">待收货</span>
              <span v-if="orderCounts.received > 0" class="status-count">{{ orderCounts.received }}</span>
            </div>
            <div class="status-item" @click="goToOrderList('completed')">
              <div class="status-icon completed">
                <CircleCheck />
              </div>
              <span class="status-text">待评价</span>
              <span v-if="orderCounts.completed > 0" class="status-count">{{ orderCounts.completed }}</span>
            </div>
          </div>
        </div>
        
        <div class="coupon-card">
          <div class="coupon-header">
            <h3>我的优惠券</h3>
            <span class="more-link" @click="goToCoupon">查看全部</span>
          </div>
          <div class="coupon-list">
            <div v-for="coupon in coupons" :key="coupon.id" class="coupon-item">
              <div class="coupon-left">
                <span class="coupon-value">¥{{ coupon.amount }}</span>
                <span class="coupon-condition">满{{ coupon.minPoint }}可用</span>
              </div>
              <div class="coupon-right">
                <span class="coupon-name">{{ coupon.name }}</span>
                <span class="coupon-date">{{ coupon.startTime }} - {{ coupon.endTime }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <el-button type="danger" @click="handleLogout" style="width: 100%; margin-top: 20px;">退出登录</el-button>
      </div>
    </div>
    
    <el-dialog v-model="showEditDialog" title="编辑资料">
      <el-form ref="editForm" :model="editFormData" :rules="editRules">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editFormData.nickname" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editFormData.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editFormData.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useMemberStore } from '@/stores/member'
import { ShoppingCart, User, Search, ShoppingBag, Star, MapLocation, Ticket, Wallet, Operation, ArrowRight, Clock, Bicycle, Box, CircleCheck } from '@element-plus/icons-vue'
import { updateMember } from '@/api/member'

const router = useRouter()
const memberStore = useMemberStore()
const searchText = ref('')
const searchVisible = ref(false)
const searchInputRef = ref(null)
const showEditDialog = ref(false)
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

const memberData = reactive({
  nickname: '',
  username: '',
  phone: '',
  avatar: '',
  email: ''
})

const editFormData = reactive({
  nickname: '',
  phone: '',
  email: ''
})

const editRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const orderCounts = reactive({
  pending: 1,
  shipped: 0,
  received: 1,
  completed: 0
})

const coupons = ref([
  { id: 1, name: '新人专享', amount: 100, minPoint: 1000, startTime: '2026-07-01', endTime: '2026-08-01' },
  { id: 2, name: '夏日特惠', amount: 50, minPoint: 500, startTime: '2026-07-01', endTime: '2026-07-31' }
])

onMounted(() => {
  if (memberStore.token) {
    memberStore.fetchMemberInfo().then(res => {
      Object.assign(memberData, res.data)
      member.value = memberData
    }).catch(() => {
      memberStore.logout()
      router.push('/login')
    })
  } else {
    router.push('/login')
  }
})

function editProfile() {
  editFormData.nickname = memberData.nickname
  editFormData.phone = memberData.phone
  editFormData.email = memberData.email || ''
  showEditDialog.value = true
}

function saveProfile() {
  updateMember(editFormData).then(() => {
    Object.assign(memberData, editFormData)
    showEditDialog.value = false
    alert('修改成功')
  }).catch(() => {
    alert('修改失败')
  })
}

function handleLogout() {
  memberStore.logout()
  router.push('/login')
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

function goToOrderList(status) {
  if (status) {
    router.push(`/order/list?status=${status}`)
  } else {
    router.push('/order/list')
  }
}

function goToFavorites() {
  alert('收藏功能开发中')
}

function goToAddress() {
  alert('收货地址功能开发中')
}

function goToCoupon() {
  alert('优惠券功能开发中')
}

function goToWallet() {
  alert('钱包功能开发中')
}

function goToSettings() {
  alert('设置功能开发中')
}
</script>

<style scoped>
.profile-page {
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

.profile-container {
  max-width: 600px;
  margin: 0 auto;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background: linear-gradient(135deg, #2AB795, #1a8a6f);
  padding: 30px;
  border-radius: 12px;
  color: #fff;
  margin-bottom: 20px;
}

.user-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  
  img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    object-fit: cover;
  }
}

.user-info {
  flex: 1;
  
  h2 {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 10px;
  }
  
  p {
    font-size: 14px;
    opacity: 0.9;
    margin-bottom: 5px;
  }
}

.menu-card {
  background: #fff;
  border-radius: 12px;
  padding: 10px 0;
  margin-bottom: 20px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 20px 30px;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
  transition: all 0.3s;
  
  &:last-child {
    border-bottom: none;
  }
  
  &:hover {
    background: #fafafa;
  }
}

.menu-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f0f9eb;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: #2AB795;
}

.menu-text {
  flex: 1;
  font-size: 16px;
  color: #333;
}

.order-status-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 30px;
  margin-bottom: 20px;
  
  h3 {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
  }
}

.status-list {
  display: flex;
  justify-content: space-between;
}

.status-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  position: relative;
}

.status-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  
  &.pending {
    background: #fff0f0;
    color: #f56c6c;
  }
  
  &.shipped {
    background: #fdf6ec;
    color: #e6a23c;
  }
  
  &.received {
    background: #ecf5ff;
    color: #409eff;
  }
  
  &.completed {
    background: #f0f9eb;
    color: #67c23a;
  }
}

.status-text {
  font-size: 14px;
  color: #666;
}

.status-count {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #FB0017;
  color: #fff;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
}

.coupon-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 30px;
  margin-bottom: 20px;
}

.coupon-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h3 {
    font-size: 18px;
    font-weight: bold;
    color: #333;
  }
  
  .more-link {
    font-size: 14px;
    color: #2AB795;
    cursor: pointer;
  }
}

.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.coupon-item {
  display: flex;
  border: 1px dashed #2AB795;
  border-radius: 8px;
  overflow: hidden;
}

.coupon-left {
  width: 120px;
  background: linear-gradient(135deg, #2AB795, #1a8a6f);
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  
  .coupon-value {
    font-size: 28px;
    font-weight: bold;
  }
  
  .coupon-condition {
    font-size: 12px;
    opacity: 0.9;
    margin-top: 5px;
  }
}

.coupon-right {
  flex: 1;
  padding: 20px;
  
  .coupon-name {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    display: block;
    margin-bottom: 10px;
  }
  
  .coupon-date {
    font-size: 12px;
    color: #999;
  }
}
</style>
