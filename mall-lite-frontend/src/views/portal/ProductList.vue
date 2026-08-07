<template>
  <div class="product-list-page">
    <div class="sub-category-bar">
      <div class="sub-category-content">
        <div class="sub-category-title">{{ currentCategoryName }}</div>
        <div class="sub-category-list">
          <div 
            v-for="sub in currentSubCategories" 
            :key="sub"
            class="sub-category-item"
            :class="{ active: selectedSubCategory === sub }"
            @click="selectSubCategory(sub)"
          >{{ sub }}</div>
        </div>
      </div>
    </div>
    
    <div class="featured-product-bar">
      <div class="featured-product-content">
        <div class="featured-label">热门产品</div>
        <div class="featured-product-list">
          <div 
            v-for="product in currentFeaturedProducts" 
            :key="product.name"
            class="featured-product-item"
            @click="goToDetail(product.id)"
          >
            <div class="featured-product-image">
              <img :src="product.image" :alt="product.name" />
            </div>
            <div class="featured-product-name">{{ product.name }}</div>
            <div class="featured-product-price">{{ product.price }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="main-content">
      <div class="toolbar">
        <span class="result-count">共 {{ total }} 件商品</span>
        <div class="toolbar-right">
          <div class="sort-options">
            <div 
              v-for="option in sortOptions" 
              :key="option.value"
              class="sort-item"
              :class="{ active: selectedSort === option.value }"
              @click="selectSort(option.value)"
            >{{ option.label }}</div>
          </div>
          <div class="view-toggle">
            <el-button :class="{ active: viewMode === 'grid' }" @click="viewMode = 'grid'">
              <Grid />
            </el-button>
            <el-button :class="{ active: viewMode === 'list' }" @click="viewMode = 'list'">
              <List />
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="product-grid" :class="viewMode">
        <div 
          v-for="(product, index) in products" 
          :key="product.id" 
          class="product-card"
          :class="{ 'is-visible': visibleProducts.has(product.id) }"
          :style="{ animationDelay: `${index * 80}ms` }"
          :data-product-id="product.id"
          @click="goToDetail(product.id)"
        >
          <div class="product-image">
            <img :src="product.mainImage || product.pic || FALLBACK_IMG" :alt="product.name" @error="onImgError" />
          </div>
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <p class="product-desc">{{ product.subTitle }}</p>
            <div class="product-price">
              <span class="price">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
            </div>
            <div class="product-tags">
              <span v-if="product.stock > 0" class="tag stock">有货</span>
              <span v-else class="tag out-of-stock">缺货</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[12, 24, 48]"
          :page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ShoppingCart, User, Search, Grid, List } from '@element-plus/icons-vue'
import { getProductList } from '@/api/product'

const visibleProducts = ref(new Set())
let observer = null

function initObserver() {
  const options = {
    root: null,
    rootMargin: '0px 0px -10% 0px',
    threshold: 0.1
  }
  
  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const productId = entry.target.getAttribute('data-product-id')
        if (productId) {
          visibleProducts.value.add(parseInt(productId))
        }
      }
    })
  }, options)
  
  nextTick(() => {
    const productCards = document.querySelectorAll('.product-card')
    productCards.forEach(card => {
      observer.observe(card)
    })
  })
}

onMounted(() => {
  initObserver()
})

onUnmounted(() => {
  if (observer) {
    observer.disconnect()
  }
})

const router = useRouter()
const route = useRoute()
const searchText = ref('')
const searchVisible = ref(false)
const searchInputRef = ref(null)
const viewMode = ref('grid')
const selectedSubCategory = ref('')
const selectedSort = ref('')
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const activeNav = ref('数码')
const member = ref(null)
const dropdownVisible = ref(false)
const currentDropdownNav = ref('')
let dropdownHideTimer = null
const featuredProducts = ref([])
const accessoriesList = ref([])

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

const categoryFeaturedProducts = {
  '数码': [
    { id: 1, name: 'iPhone 15 Pro', price: '¥7999起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=iPhone%2015%20Pro%20smartphone%20product%20photo%20white%20background&image_size=square' },
    { id: 2, name: '华为 Mate 60', price: '¥5499起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Huawei%20Mate%2060%20smartphone%20product%20photo%20white%20background&image_size=square' },
    { id: 3, name: '小米 14', price: '¥3999起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Xiaomi%2014%20smartphone%20product%20photo%20white%20background&image_size=square' },
    { id: 4, name: 'OPPO Find X7', price: '¥3999起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=OPPO%20Find%20X7%20smartphone%20product%20photo%20white%20background&image_size=square' }
  ],
  '电脑': [
    { id: 5, name: 'MacBook Pro', price: '¥14999起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=MacBook%20Pro%20laptop%20product%20photo%20silver%20white%20background&image_size=square' },
    { id: 6, name: 'ThinkPad X1', price: '¥9999起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=ThinkPad%20X1%20laptop%20product%20photo%20black%20white%20background&image_size=square' },
    { id: 7, name: '戴尔 XPS 15', price: '¥12999起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Dell%20XPS%2015%20laptop%20product%20photo%20white%20background&image_size=square' },
    { id: 8, name: '华为 MateBook', price: '¥7999起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Huawei%20MateBook%20laptop%20product%20photo%20silver%20white%20background&image_size=square' }
  ],
  '服饰': [
    { id: 9, name: '纯棉T恤', price: '¥99起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cotton%20t-shirt%20fashion%20product%20photo%20white%20background&image_size=square' },
    { id: 10, name: '牛仔裤', price: '¥199起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=blue%20jeans%20fashion%20product%20photo%20white%20background&image_size=square' },
    { id: 11, name: '连衣裙', price: '¥299起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=elegant%20dress%20fashion%20product%20photo%20white%20background&image_size=square' },
    { id: 12, name: '运动外套', price: '¥399起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=sports%20jacket%20fashion%20product%20photo%20white%20background&image_size=square' }
  ],
  '美妆': [
    { id: 13, name: 'SK-II神仙水', price: '¥1540起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=SK-II%20facial%20treatment%20essence%20bottle%20cosmetic%20product%20photo%20white%20background&image_size=square' },
    { id: 14, name: '兰蔻小黑瓶', price: '¥1080起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Lancome%20Advanced%20Genifique%20serum%20bottle%20cosmetic%20product%20photo%20white%20background&image_size=square' },
    { id: 15, name: '雅诗兰黛眼霜', price: '¥680起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Estee%20Lauder%20eye%20cream%20jar%20cosmetic%20product%20photo%20white%20background&image_size=square' },
    { id: 16, name: 'MAC口红', price: '¥250起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=MAC%20lipstick%20cosmetic%20product%20photo%20white%20background&image_size=square' }
  ],
  '运动': [
    { id: 17, name: '篮球鞋', price: '¥599起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=basketball%20shoes%20sports%20product%20photo%20white%20background&image_size=square' },
    { id: 18, name: '瑜伽垫', price: '¥199起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=yoga%20mat%20sports%20product%20photo%20white%20background&image_size=square' },
    { id: 19, name: '运动背包', price: '¥299起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=sports%20backpack%20product%20photo%20white%20background&image_size=square' },
    { id: 20, name: '跑步鞋', price: '¥499起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=running%20shoes%20sports%20product%20photo%20white%20background&image_size=square' }
  ],
  '图书': [
    { id: 21, name: '三体全集', price: '¥93起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=science%20fiction%20book%20set%20product%20photo%20white%20background&image_size=square' },
    { id: 22, name: '活着', price: '¥39起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Chinese%20literature%20book%20product%20photo%20white%20background&image_size=square' },
    { id: 23, name: '人类简史', price: '¥68起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=history%20book%20product%20photo%20white%20background&image_size=square' },
    { id: 24, name: '原则', price: '¥128起', image: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=business%20book%20product%20photo%20white%20background&image_size=square' }
  ]
}

const categorySubCategories = {
  '数码': ['热门机型', '配件', '智能穿戴', '手机壳', '充电器', '数据线', '耳机', '贴膜'],
  '电脑': ['热门机型', '显示器', '配件', '显卡', 'MacBook', 'ThinkPad', '戴尔XPS', '华为MateBook', '4K显示器', '曲面屏', '电竞显示器', '机械键盘', '电竞鼠标', 'NVIDIA RTX', 'AMD RX'],
  '服饰': ['热门推荐', '男装', '女装', '鞋包', 'T恤', '衬衫', '裤子', '连衣裙', '外套', '运动鞋', '皮鞋', '包包'],
  '美妆': ['热门推荐', '护肤', '彩妆', '香水', '洁面', '爽肤水', '乳液', '面霜', '口红', '粉底', '眼影'],
  '运动': ['热门推荐', '运动装备', '健身器材', '户外', '篮球', '足球', '羽毛球', '跑步', '哑铃', '瑜伽垫'],
  '图书': ['热门推荐', '畅销书籍', '教材教辅', '数字阅读', '小说', '经管', '科技', '人文', '电子书', '有声书']
}

const sortOptions = [
  { label: '综合排序', value: '' },
  { label: '价格从低到高', value: 'price_asc' },
  { label: '价格从高到低', value: 'price_desc' },
  { label: '销量优先', value: 'sale_desc' }
]

const currentCategoryName = computed(() => {
  return activeNav.value || '全部商品'
})

const currentSubCategories = computed(() => {
  return categorySubCategories[activeNav.value] || ['热门推荐']
})

const currentFeaturedProducts = computed(() => {
  return categoryFeaturedProducts[activeNav.value] || []
})

const products = ref([
  { id: 1, name: 'iPhone 15 Pro', subTitle: '全新A17芯片，钛金属设计', price: 7999, originalPrice: 8999, stock: 100, pic: '' },
  { id: 2, name: 'MacBook Pro', subTitle: 'M3 Pro芯片，超强性能', price: 14999, originalPrice: 15999, stock: 50, pic: '' },
  { id: 3, name: '无线蓝牙耳机', subTitle: '主动降噪，超长续航', price: 599, originalPrice: 699, stock: 200, pic: '' },
  { id: 4, name: '智能手表', subTitle: '健康监测，运动追踪', price: 1999, originalPrice: 2199, stock: 80, pic: '' },
  { id: 5, name: '机械键盘', subTitle: 'RGB背光，青轴手感', price: 399, originalPrice: 459, stock: 150, pic: '' },
  { id: 6, name: '电竞鼠标', subTitle: '高精度传感器，人体工学', price: 299, originalPrice: 349, stock: 300, pic: '' },
  { id: 7, name: '平板电视', subTitle: '4K超高清，智能语音', price: 3999, originalPrice: 4599, stock: 60, pic: '' },
  { id: 8, name: '空气净化器', subTitle: 'HEPA滤网，静音设计', price: 899, originalPrice: 999, stock: 120, pic: '' },
  { id: 9, name: '智能门锁', subTitle: '指纹识别，密码开锁', price: 1299, originalPrice: 1599, stock: 90, pic: '' },
  { id: 10, name: '便携式音箱', subTitle: '防水设计，立体声效', price: 299, originalPrice: 359, stock: 180, pic: '' },
  { id: 11, name: '智能台灯', subTitle: '护眼模式，无极调光', price: 199, originalPrice: 249, stock: 250, pic: '' },
  { id: 12, name: '无线充电器', subTitle: '快充支持，多设备兼容', price: 99, originalPrice: 129, stock: 500, pic: '' }
])

onMounted(() => {
  if (route.query.keyword) {
    searchText.value = route.query.keyword
  }
  if (route.query.category) {
    activeNav.value = decodeURIComponent(route.query.category)
  }
  if (route.query.subCategory) {
    selectedSubCategory.value = decodeURIComponent(route.query.subCategory)
  }
  loadProducts()
})

function loadProducts() {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    keyword: searchText.value,
    category: activeNav.value || undefined,
    subCategory: selectedSubCategory.value || undefined,
    sort: selectedSort.value || undefined
  }
  
  getProductList(params).then(res => {
    products.value = res.data?.list || products.value
    total.value = res.data?.total || products.value.length
    nextTick(() => {
      if (observer) {
        observer.disconnect()
      }
      initObserver()
    })
  }).catch(() => {
    total.value = products.value.length
    nextTick(() => {
      if (observer) {
        observer.disconnect()
      }
      initObserver()
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
  pageNum.value = 1
  router.push({ query: { keyword: searchText.value, category: activeNav.value } })
  loadProducts()
}

function handleHotSearch(keyword) {
  searchText.value = keyword
  searchVisible.value = false
  pageNum.value = 1
  router.push({ query: { keyword: keyword, category: activeNav.value } })
  loadProducts()
}

function selectNav(item) {
  activeNav.value = item.name
  selectedSubCategory.value = ''
  searchVisible.value = false
  pageNum.value = 1
  if (item.name === '首页') {
    router.push('/')
  } else {
    router.push({ query: { category: item.name } })
    loadProducts()
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
  activeNav.value = currentDropdownNav.value
  searchText.value = product.name
  pageNum.value = 1
  router.push({ query: { category: currentDropdownNav.value, keyword: product.name } })
  loadProducts()
}

function handleAccessoriesClick(item) {
  dropdownVisible.value = false
  activeNav.value = currentDropdownNav.value
  selectedSubCategory.value = item
  pageNum.value = 1
  router.push({ query: { category: currentDropdownNav.value, subCategory: item } })
  loadProducts()
}

function selectSubCategory(sub) {
  selectedSubCategory.value = sub
  pageNum.value = 1
  router.push({ query: { category: activeNav.value, subCategory: sub } })
  loadProducts()
}

function selectSort(value) {
  selectedSort.value = value
  loadProducts()
}

function handleSizeChange(size) {
  pageSize.value = size
  pageNum.value = 1
  loadProducts()
}

function handleCurrentChange(page) {
  pageNum.value = page
  loadProducts()
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

function goToDetail(id) {
  router.push(`/product/detail/${id}`)
}

// 图片加载失败兜底：使用内联 SVG 占位，避免破图与重复 404
const FALLBACK_IMG = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200'%3E%3Crect width='100%25' height='100%25' fill='%23f0f0f0'/%3E%3Ctext x='50%25' y='50%25' font-size='14' fill='%23999' text-anchor='middle' dominant-baseline='middle'%3Emall-lite%3C/text%3E%3C/svg%3E"
function onImgError(e) {
  if (e && e.target && e.target.src !== FALLBACK_IMG) {
    e.target.src = FALLBACK_IMG
  }
}
</script>

<style scoped>
.product-list-page {
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

.featured-price {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
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

.sub-category-bar {
  background: #fff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 12px 0;
}

.sub-category-content {
  display: flex;
  align-items: center;
  gap: 20px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  overflow-x: auto;
}

.sub-category-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  white-space: nowrap;
}

.sub-category-list {
  display: flex;
  gap: 15px;
}

.sub-category-item {
  font-size: 14px;
  color: #666;
  padding: 6px 15px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  white-space: nowrap;
  
  &:hover {
    color: #2AB795;
    background: #f0f9eb;
  }
  
  &.active {
    background: #2AB795;
    color: #fff;
  }
}

.featured-product-bar {
  background: #fff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 15px 0;
}

.featured-product-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.featured-label {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.featured-product-list {
  display: flex;
  gap: 20px;
  overflow-x: auto;
}

.featured-product-item {
  flex-shrink: 0;
  width: 140px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-5px);
  }
}

.featured-product-image {
  width: 140px;
  height: 140px;
  background: #f5f5f7;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 10px;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.featured-product-name {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  text-align: center;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.featured-product-price {
  font-size: 13px;
  color: #FB0017;
  text-align: center;
}

.main-content {
  max-width: 1400px;
  margin: 20px auto;
  padding: 0 20px;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  background: #fff;
  padding: 15px 20px;
  border-radius: 12px;
}

.result-count {
  color: #666;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.sort-options {
  display: flex;
  gap: 15px;
}

.sort-item {
  font-size: 14px;
  color: #666;
  padding: 6px 15px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    color: #2AB795;
    background: #f0f9eb;
  }
  
  &.active {
    background: #2AB795;
    color: #fff;
  }
}

.view-toggle {
  display: flex;
  gap: 5px;
  
  .el-button {
    &.active {
      background: #2AB795;
      border-color: #2AB795;
      color: #fff;
    }
  }
}

.product-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  
  &.list {
    .product-card {
      width: 100%;
      display: flex;
      
      .product-image {
        width: 200px;
        height: 200px;
        flex-shrink: 0;
      }
      
      .product-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
      }
    }
  }
}

.product-card {
  width: calc(25% - 15px);
  background: #ffffff;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  opacity: 0;
  transform: translateY(40px) scale(0.96);
  transition: transform 0.6s cubic-bezier(0.23, 1, 0.32, 1), opacity 0.5s ease-out, box-shadow 0.4s ease;
  
  &.is-visible {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
  
  &:hover {
    transform: translateY(-8px) scale(1.01);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08), 0 4px 12px rgba(0, 0, 0, 0.04);
  }
}

.product-image {
  width: 100%;
  height: 240px;
  background: #fafafa;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  transition: background 0.3s ease;
  
  img {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
    transition: transform 0.4s cubic-bezier(0.23, 1, 0.32, 1);
  }
  
  .product-card:hover & {
    background: #f5f5f5;
    
    img {
      transform: scale(1.05);
    }
  }
}

.product-info {
  padding: 20px;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 14px;
  color: #999;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 10px;
  
  .price {
    font-size: 22px;
    font-weight: bold;
    color: #FB0017;
  }
  
  .original-price {
    font-size: 14px;
    color: #999;
    text-decoration: line-through;
  }
}

.product-tags {
  display: flex;
  gap: 8px;
}

.tag {
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 4px;
  
  &.stock {
    background: #f0f9eb;
    color: #67c23a;
  }
  
  &.out-of-stock {
    background: #fef0f0;
    color: #f56c6c;
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
