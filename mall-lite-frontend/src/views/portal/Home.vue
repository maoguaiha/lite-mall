<template>
  <div class="home">
    <!-- ============ Hero 轮播（微渐变 / 卡片式，降饱和） ============ -->
    <section class="hero">
      <el-carousel
        class="hero__carousel"
        height="auto"
        :interval="5000"
        arrow="always"
        indicator-position="outside"
      >
        <el-carousel-item v-for="(b, i) in banners" :key="i">
          <div class="banner" :style="{ background: b.bg }">
            <div class="banner__blob" :style="{ background: b.blob }"></div>
            <div class="banner__content">
              <span class="banner__eyebrow">{{ b.eyebrow }}</span>
              <h2 class="banner__title">{{ b.title }}</h2>
              <p class="banner__sub">{{ b.sub }}</p>
              <router-link :to="b.link" class="banner__cta">
                {{ b.cta }}
                <el-icon class="banner__arrow"><ArrowRight /></el-icon>
              </router-link>
            </div>
            <div class="banner__art" :style="{ background: b.art }">
              <el-icon class="banner__art-icon"><component :is="b.icon" /></el-icon>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- ============ 商品分类（统一样式 + 微上浮） ============ -->
    <section class="section">
      <div class="section__head">
        <h2 class="section__title">商品分类</h2>
        <router-link to="/product/list" class="section__more">查看全部 ›</router-link>
      </div>
      <div class="cats">
        <router-link
          v-for="(c, i) in categories"
          :key="c.id"
          :to="`/product/list?categoryId=${c.id}`"
          class="cat-card"
          :style="{ '--i': i }"
        >
          <span class="cat-card__icon"><el-icon><component :is="c.icon" /></el-icon></span>
          <span class="cat-card__name">{{ c.name }}</span>
        </router-link>
      </div>
    </section>

    <!-- ============ 热门商品（占位图 1:1 + 价格突出 + 加购按钮） ============ -->
    <section class="section">
      <div class="section__head">
        <h2 class="section__title">热门商品</h2>
        <router-link to="/product/list" class="section__more">更多好物 ›</router-link>
      </div>
      <div class="grid">
        <router-link
          v-for="(p, i) in products"
          :key="p.id"
          :to="`/product/detail/${p.id}`"
          class="card"
          :style="{ '--i': i }"
        >
          <div class="card__media">
            <ProductImage :src="p.pic" :label="p.name" :seed="p.id" :alt="p.name" />
            <span v-if="p.hot" class="card__flag">HOT</span>
          </div>
          <div class="card__body">
            <h3 class="card__name">{{ p.name }}</h3>
            <span class="card__chip">{{ p.subTitle || '平台精选' }}</span>
            <div class="card__foot">
              <span class="card__price"><i>¥</i>{{ formatPrice(p.price) }}</span>
            </div>
          </div>
          <button class="card__add" title="加入购物车" @click.prevent="addToCart(p)">
            <el-icon><Plus /></el-icon>
          </button>
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Plus, GoodsFilled, Present, Reading } from '@element-plus/icons-vue'
import { getProductList, getCategoryList } from '@/api/product'
import { iconForCategory } from '@/utils/categoryIcons'
import { useCartStore } from '@/stores/cart'
import ProductImage from '@/components/ProductImage.vue'

const router = useRouter()
const cartStore = useCartStore()

const products = ref([])
// 分类数据由后端 /product/category/list 真实驱动（id/名称/筛选均准确）
const categories = ref([])

// 微渐变、低饱和、品牌蓝 / 深灰体系
const banners = [
  {
    eyebrow: 'NEW ARRIVAL',
    title: '新品上架',
    sub: '探索本季精选好物，遇见更懂你的生活',
    cta: '立即查看',
    link: '/product/list',
    icon: GoodsFilled,
    bg: 'linear-gradient(120deg, #eaf0fb 0%, #dbe6fb 55%, #eef3ff 100%)',
    blob: 'radial-gradient(circle at 30% 30%, rgba(37,99,235,0.22), transparent 60%)',
    art: 'linear-gradient(135deg, #2563eb, #1e40af)'
  },
  {
    eyebrow: 'LIMITED TIME',
    title: '限时秒杀',
    sub: '每日精选爆款，低价风暴不容错过',
    cta: '马上抢购',
    link: '/seckill',
    icon: Present,
    bg: 'linear-gradient(120deg, #f1f3fb 0%, #e7ecf7 55%, #eef2fb 100%)',
    blob: 'radial-gradient(circle at 70% 40%, rgba(99,102,241,0.18), transparent 60%)',
    art: 'linear-gradient(135deg, #6366f1, #4338ca)'
  },
  {
    eyebrow: 'MEMBER ONLY',
    title: '会员专享',
    sub: '专属优惠与权益，尊享品质生活',
    cta: '了解权益',
    link: '/profile',
    icon: Reading,
    bg: 'linear-gradient(120deg, #eef4f4 0%, #e2edf0 55%, #f1f6f8 100%)',
    blob: 'radial-gradient(circle at 40% 60%, rgba(13,148,136,0.16), transparent 60%)',
    art: 'linear-gradient(135deg, #0d9488, #0f766e)'
  }
]

function formatPrice(v) {
  const n = Number(v)
  return isNaN(n) ? '0.00' : n.toFixed(2)
}

function addToCart(p) {
  cartStore.addItem(p, 1)
  // 轻提示已在全局 ElMessage 体系可用；此处保持无依赖
}

async function fetchCategories() {
  try {
    const res = await getCategoryList({ parentId: 0 })
    const list = res?.data || []
    categories.value = Array.isArray(list)
      ? list.map(c => ({ id: c.id, name: c.name, icon: iconForCategory(c.id) }))
      : []
  } catch (e) {
    categories.value = []
  }
}

onMounted(async () => {
  fetchCategories()
  try {
    const res = await getProductList({ pageNum: 1, pageSize: 8 })
    const data = res?.data || {}
    const list = data.list || data.records || data || []
    products.value = Array.isArray(list) ? list : []
  } catch (e) {
    products.value = []
  }
})
</script>

<style scoped lang="scss">
@use '../../styles/variables' as *;

.home { display: block; }

/* ---------- Hero ---------- */
.hero { margin-bottom: $space-xl; }
.hero__carousel {
  border-radius: $radius-xl;
  overflow: hidden;
  box-shadow: $shadow-md;
}
.banner {
  position: relative;
  height: 340px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 56px;
  overflow: hidden;
}
.banner__blob {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.banner__content {
  position: relative;
  max-width: 52%;
  z-index: 1;
}
.banner__eyebrow {
  display: inline-block;
  font-size: 12px;
  letter-spacing: 0.18em;
  font-weight: 700;
  color: $brand;
  margin-bottom: 12px;
}
.banner__title {
  font-size: 40px;
  line-height: 1.1;
  font-weight: 800;
  color: $ink-1;
  margin: 0 0 14px;
  letter-spacing: -0.02em;
}
.banner__sub {
  font-size: 15px;
  color: $ink-2;
  margin: 0 0 26px;
  line-height: 1.6;
}
.banner__cta {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 999px;
  background: $brand;
  color: #fff;
  font-weight: 600;
  text-decoration: none;
  box-shadow: $shadow-sm;
  transition: transform 0.25s $ease-smooth, box-shadow 0.25s $ease-smooth,
    background 0.25s $ease-smooth;

  &:hover {
    transform: translateY(-2px);
    background: $brand-hover;
    box-shadow: $shadow-md;
  }
}
.banner__arrow {
  transition: transform 0.25s $ease-smooth;
}
.banner__cta:hover .banner__arrow { transform: translateX(4px); }

.banner__art {
  position: relative;
  z-index: 1;
  width: 200px;
  height: 200px;
  border-radius: 28px;
  display: grid;
  place-items: center;
  box-shadow: $shadow-lg;
  transform: rotate(-6deg);
}
.banner__art-icon {
  font-size: 92px;
  color: rgba(255, 255, 255, 0.92);
}

/* ---------- Section ---------- */
.section { margin-bottom: $space-xl; }
.section__head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: $space-lg;
}
.section__title {
  font-size: $font-size-title-lg;
  font-weight: 800;
  color: $ink-1;
  margin: 0;
  position: relative;
  padding-left: 14px;
  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 5px;
    height: 20px;
    border-radius: 4px;
    background: linear-gradient(180deg, $brand, $brand-active);
  }
}
.section__more {
  font-size: 13px;
  color: $ink-3;
  text-decoration: none;
  transition: color 0.2s $ease-smooth;
  &:hover { color: $brand; }
}

/* ---------- Categories ---------- */
.cats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $space-md;
}
.cat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 26px 12px;
  background: $surface;
  border: 1px solid $line;
  border-radius: $radius-lg;
  text-decoration: none;
  color: $ink-1;
  box-shadow: $shadow-xs;
  transition: transform 0.28s $ease-smooth, box-shadow 0.28s $ease-smooth,
    border-color 0.28s $ease-smooth;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-md;
    border-color: transparent;
  }
}
.cat-card__icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  font-size: 26px;
  color: $brand;
  background: $brand-soft;
  transition: transform 0.28s $ease-smooth, background 0.28s $ease-smooth;
}
.cat-card:hover .cat-card__icon {
  transform: scale(1.06);
  background: $brand-soft-2;
}
.cat-card__name {
  font-size: 14px;
  font-weight: 600;
  color: $ink-2;
}

/* ---------- Products ---------- */
.grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $space-lg;
}
.card {
  position: relative;
  display: flex;
  flex-direction: column;
  background: $surface;
  border: 1px solid $line;
  border-radius: $radius-lg;
  overflow: hidden;
  text-decoration: none;
  box-shadow: $shadow-xs;
  transition: transform 0.28s $ease-smooth, box-shadow 0.28s $ease-smooth,
    border-color 0.28s $ease-smooth;

  &:hover {
    transform: translateY(-6px);
    box-shadow: $shadow-md;
    border-color: transparent;
  }
}
.card__media {
  position: relative;
  padding: 14px 14px 0;
}
.card__flag {
  position: absolute;
  top: 22px;
  left: 22px;
  z-index: 2;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.06em;
  color: #fff;
  background: linear-gradient(135deg, $price-color, #be123c);
  padding: 3px 8px;
  border-radius: 999px;
  box-shadow: $shadow-xs;
}
.card__body {
  padding: 14px 16px 18px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.card__name {
  font-size: 15px;
  font-weight: 600;
  color: $ink-1;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card__chip {
  align-self: flex-start;
  font-size: 12px;
  color: $brand;
  background: $brand-soft;
  padding: 3px 10px;
  border-radius: 999px;
}
.card__foot { margin-top: 2px; }
.card__price {
  font-size: 22px;
  font-weight: 800;
  color: $price-color;
  letter-spacing: -0.01em;
  i {
    font-size: 14px;
    font-style: normal;
    margin-right: 1px;
  }
}
.card__add {
  position: absolute;
  right: 14px;
  bottom: 14px;
  width: 42px;
  height: 42px;
  border: none;
  border-radius: 14px;
  display: grid;
  place-items: center;
  font-size: 22px;
  color: #fff;
  background: linear-gradient(135deg, $brand 0%, $brand-active 100%);
  box-shadow: $shadow-sm;
  cursor: pointer;
  opacity: 0;
  transform: translateY(8px) scale(0.92);
  transition: opacity 0.25s $ease-smooth, transform 0.25s $ease-smooth,
    filter 0.2s $ease-smooth;

  &:hover { filter: brightness(1.06); }
}
.card:hover .card__add {
  opacity: 1;
  transform: translateY(0) scale(1);
}

/* ---------- Entrance animation ---------- */
.cat-card, .card {
  opacity: 0;
  animation: rise 0.5s $ease-smooth forwards;
  animation-delay: calc(var(--i) * 45ms);
}
@keyframes rise {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}
/* 卡片 hover 时覆盖 entrance 的 transform */
.card:hover { transform: translateY(-6px); }
.cat-card:hover { transform: translateY(-4px); }

/* ---------- Responsive ---------- */
@media (max-width: 991px) {
  .cats { grid-template-columns: repeat(4, 1fr); }
  .grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .banner { height: 260px; padding: 0 28px; }
  .banner__title { font-size: 30px; }
  .banner__sub { font-size: 13px; }
  .banner__art { width: 120px; height: 120px; border-radius: 18px; }
  .banner__art-icon { font-size: 56px; }
  .banner__content { max-width: 70%; }
  .cats { grid-template-columns: repeat(4, 1fr); gap: 10px; }
  .cat-card { padding: 18px 8px; }
  .cat-card__icon { width: 46px; height: 46px; font-size: 22px; }
  .grid { grid-template-columns: repeat(2, 1fr); gap: $space-md; }
}
@media (max-width: 480px) {
  .cats { grid-template-columns: repeat(2, 1fr); }
  .card__add { opacity: 1; transform: none; } /* 移动端常显加购按钮 */
}
</style>
