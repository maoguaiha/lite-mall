<template>
  <header class="app-header" :class="{ 'is-scrolled': scrolled }">
    <div class="app-header__inner">
      <!-- Logo -->
      <router-link to="/" class="brand" @click="closeAll">
        <span class="brand__mark">M</span>
        <span class="brand__name">mall-lite</span>
      </router-link>

      <!-- 搜索框（显眼、带占位文字） -->
      <div class="search" :class="{ 'search--open': mobileSearchOpen }">
        <el-input
          v-model="searchText"
          class="search__input"
          size="large"
          clearable
          placeholder="搜索商品、品牌或分类..."
          @keyup.enter="doSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button class="search__btn" @click="doSearch">
              <el-icon><Search /></el-icon>
              <span class="search__btn-text">搜索</span>
            </el-button>
          </template>
        </el-input>
      </div>

      <!-- 右侧功能区 -->
      <div class="actions">
        <!-- 分类导航菜单 -->
        <div class="cat" @mouseenter="openCat" @mouseleave="closeCat">
          <button class="cat__trigger" :class="{ active: catOpen }" @click="goCategory">
            <el-icon><Menu /></el-icon>
            <span>分类</span>
            <el-icon class="cat__caret" :class="{ open: catOpen }"><ArrowDown /></el-icon>
          </button>
          <transition name="drop">
            <div v-if="catOpen" class="cat__panel" @click.stop>
              <div class="cat__grid">
                <router-link
                  v-for="c in categories"
                  :key="c.id"
                  :to="`/product/list?categoryId=${c.id}`"
                  class="cat__item"
                  @click="closeAll"
                >
                  <span class="cat__icon"><el-icon><component :is="c.icon" /></el-icon></span>
                  <span class="cat__label">{{ c.name }}</span>
                </router-link>
              </div>
              <router-link to="/category" class="cat__more" @click="closeAll">
                查看全部分类 <el-icon><ArrowRight /></el-icon>
              </router-link>
            </div>
          </transition>
        </div>

        <!-- 购物车（带数量 Badge） -->
        <router-link to="/cart" class="action action--cart" @click="closeAll">
          <el-badge :value="cartCount" :hidden="cartCount === 0" :max="99">
            <el-icon class="action__icon"><ShoppingCart /></el-icon>
          </el-badge>
          <span class="action__text">购物车</span>
        </router-link>

        <!-- 用户 / 登录 -->
        <router-link v-if="isLoggedIn" to="/profile" class="user" @click="closeAll">
          <el-avatar :size="30" :src="memberInfo?.avatar || ''" class="user__avatar">
            {{ avatarText }}
          </el-avatar>
          <span class="user__name">{{ memberInfo?.nickname || memberInfo?.username || '我的' }}</span>
        </router-link>
        <router-link v-else to="/login" class="action action--login" @click="closeAll">
          <el-icon class="action__icon"><User /></el-icon>
          <span class="action__text">登录</span>
        </router-link>

        <!-- 移动端菜单触发 -->
        <button class="hamburger" @click="drawer = !drawer" aria-label="菜单">
          <el-icon v-if="!drawer"><Expand /></el-icon>
          <el-icon v-else><Fold /></el-icon>
        </button>
      </div>
    </div>

    <!-- 移动端抽屉 -->
    <transition name="slide">
      <div v-if="drawer" class="drawer" @click.self="drawer = false">
        <div class="drawer__body">
          <div class="drawer__search">
            <el-input
              v-model="searchText"
              placeholder="搜索商品、品牌或分类..."
              size="large"
              clearable
              @keyup.enter="doSearch"
            >
              <template #append>
                <el-button @click="doSearch"><el-icon><Search /></el-icon></el-button>
              </template>
            </el-input>
          </div>
          <p class="drawer__title">全部分类</p>
          <div class="drawer__cats">
            <router-link
              v-for="c in categories"
              :key="c.id"
              :to="`/product/list?categoryId=${c.id}`"
              class="drawer__cat"
              @click="closeAll"
            >
              <span class="drawer__cat-icon"><el-icon><component :is="c.icon" /></el-icon></span>
              <span>{{ c.name }}</span>
            </router-link>
          </div>
          <router-link to="/category" class="drawer__all" @click="closeAll">
            <el-icon><Menu /></el-icon> 浏览全部分类
            <el-icon class="drawer__all-arrow"><ArrowRight /></el-icon>
          </router-link>
          <div class="drawer__foot">
            <router-link to="/cart" class="drawer__link" @click="closeAll">
              <el-icon><ShoppingCart /></el-icon> 购物车
            </router-link>
            <router-link v-if="isLoggedIn" to="/profile" class="drawer__link" @click="closeAll">
              <el-icon><User /></el-icon> 个人中心
            </router-link>
            <router-link v-else to="/login" class="drawer__link" @click="closeAll">
              <el-icon><User /></el-icon> 登录
            </router-link>
          </div>
        </div>
      </div>
    </transition>
  </header>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Search, Menu, ArrowDown, ShoppingCart, User, Expand, Fold, ArrowRight
} from '@element-plus/icons-vue'
import { getCategoryList } from '@/api/product'
import { iconForCategory } from '@/utils/categoryIcons'
import { useMemberStore } from '@/stores/member'
import { useCartStore } from '@/stores/cart'

const router = useRouter()
const route = useRoute()
const memberStore = useMemberStore()
const cartStore = useCartStore()

const searchText = ref('')
const catOpen = ref(false)
const drawer = ref(false)
const mobileSearchOpen = ref(false)
const scrolled = ref(false)

const isLoggedIn = computed(() => memberStore.isLoggedIn)
const memberInfo = computed(() => memberStore.memberInfo)
const cartCount = computed(() => cartStore.count)
const avatarText = computed(() => {
  const n = memberInfo.value?.nickname || memberInfo.value?.username || ''
  return n ? n.slice(0, 1).toUpperCase() : 'U'
})

// 顶层分类从后端拉取（含图标映射），避免与首页分类块重复维护
const categories = ref([])

function doSearch() {
  const kw = searchText.value.trim()
  router.push(kw ? `/product/list?keyword=${encodeURIComponent(kw)}` : '/product/list')
  closeAll()
}
function openCat() { if (window.innerWidth >= 992) catOpen.value = true }
function closeCat() { if (window.innerWidth >= 992) catOpen.value = false }
function goCategory() {
  closeCat()
  router.push('/category')
}
function closeAll() {
  catOpen.value = false
  drawer.value = false
  mobileSearchOpen.value = false
}
function onScroll() { scrolled.value = window.scrollY > 8 }

onMounted(async () => {
  cartStore.fetchCount()
  window.addEventListener('scroll', onScroll, { passive: true })
  try {
    const res = await getCategoryList({ parentId: 0 })
    const raw = res?.data ?? res
    categories.value = (Array.isArray(raw) ? raw : []).map((c) => ({
      id: c.id,
      name: c.name,
      icon: iconForCategory(c.id)
    }))
  } catch (e) {
    // 分类获取失败不影响其它功能
  }
})
onBeforeUnmount(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped lang="scss">
@use '../styles/variables' as *;

.app-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  height: $header-height;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: saturate(180%) blur(18px);
  -webkit-backdrop-filter: saturate(180%) blur(18px);
  border-bottom: 1px solid transparent;
  transition: box-shadow 0.3s $ease-smooth, border-color 0.3s $ease-smooth,
    background 0.3s $ease-smooth;

  &.is-scrolled {
    background: rgba(255, 255, 255, 0.92);
    border-bottom-color: $line;
    box-shadow: $shadow-header;
  }
}

.app-header__inner {
  max-width: $content-max;
  height: 100%;
  margin: 0 auto;
  padding: 0 $space-lg;
  display: flex;
  align-items: center;
  gap: $space-lg;
}

/* Logo */
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  flex-shrink: 0;

  &__mark {
    width: 36px;
    height: 36px;
    border-radius: 10px;
    display: grid;
    place-items: center;
    font-weight: 800;
    font-size: 20px;
    color: #fff;
    background: linear-gradient(135deg, $brand 0%, $brand-active 100%);
    box-shadow: $shadow-sm;
  }

  &__name {
    font-size: 20px;
    font-weight: 800;
    letter-spacing: -0.01em;
    color: $ink-1;
  }
}

/* Search */
.search {
  flex: 1 1 auto;
  max-width: 560px;
  margin: 0 auto;

  &__input {
    --el-border-radius-base: 999px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 999px;
    box-shadow: 0 0 0 1px $line inset;
    padding-left: 16px;
    background: #f4f6fb;
    transition: box-shadow 0.25s $ease-smooth, background 0.25s $ease-smooth;
  }
  :deep(.el-input__wrapper.is-focus) {
    background: #fff;
    box-shadow: 0 0 0 1px $brand inset, 0 6px 18px rgba(37, 99, 235, 0.12);
  }
  :deep(.el-input__prefix) { color: $ink-3; }

  &__btn {
    border: none;
    background: linear-gradient(135deg, $brand 0%, $brand-active 100%);
    color: #fff;
    border-radius: 999px !important;
    padding: 0 18px;
    box-shadow: $shadow-sm;
    transition: transform 0.2s $ease-smooth, filter 0.2s $ease-smooth;

    &:hover { filter: brightness(1.05); transform: translateY(-1px); }
    &:active { transform: translateY(0); }
  }
  &__btn-text { margin-left: 4px; }
}

/* Actions */
.actions {
  display: flex;
  align-items: center;
  gap: $space-sm;
  flex-shrink: 0;
}

.action,
.user {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 40px;
  padding: 0 12px;
  border-radius: 999px;
  text-decoration: none;
  color: $ink-2;
  font-size: 14px;
  transition: background 0.2s $ease-smooth, color 0.2s $ease-smooth;

  &:hover { background: $brand-soft; color: $brand; }
  &__icon { font-size: 20px; }
}

.user {
  &__avatar { background: linear-gradient(135deg, $brand 0%, $brand-active 100%); color: #fff; }
  &__name { max-width: 96px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
}

/* 分类菜单 */
.cat { position: relative; display: flex; align-items: center; }
.cat__trigger {
  display: flex;
  align-items: center;
  gap: 6px;
  height: 40px;
  padding: 0 14px;
  border: none;
  background: transparent;
  border-radius: 999px;
  color: $ink-2;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s $ease-smooth, color 0.2s $ease-smooth;

  &:hover, &.active { background: $brand-soft; color: $brand; }
}
.cat__caret { transition: transform 0.25s $ease-smooth; font-size: 14px; }
.cat__caret.open { transform: rotate(180deg); }

.cat__panel {
  position: absolute;
  top: calc(100% + 10px);
  left: 0;
  width: 288px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: saturate(160%) blur(12px);
  -webkit-backdrop-filter: saturate(160%) blur(12px);
  border: 1px solid rgba(15, 23, 42, 0.06);
  border-radius: 18px;
  box-shadow: 0 20px 40px -12px rgba(15, 23, 42, 0.22);
}
.cat__grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 4px;
}
.cat__item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 9px 10px;
  border-radius: 12px;
  text-decoration: none;
  color: $ink-2;
  transition: background 0.2s $ease-smooth, color 0.2s $ease-smooth;

  &:hover { background: $brand-soft; color: $brand; }
}
.cat__icon {
  width: 32px;
  height: 32px;
  border-radius: 9px;
  display: grid;
  place-items: center;
  font-size: 17px;
  color: $brand;
  background: rgba(37, 99, 235, 0.1);
  flex-shrink: 0;
}
.cat__label { font-size: 13px; font-weight: 500; }
.cat__more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 8px;
  padding: 9px;
  border-radius: 12px;
  text-decoration: none;
  font-size: 13px;
  font-weight: 600;
  color: $brand;
  background: rgba(37, 99, 235, 0.08);
  transition: background 0.2s $ease-smooth;

  &:hover { background: rgba(37, 99, 235, 0.16); }
  .el-icon { font-size: 14px; }
}

/* Hamburger */
.hamburger {
  display: none;
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: 10px;
  color: $ink-1;
  font-size: 22px;
  cursor: pointer;
  &:hover { background: $brand-soft; }
}

/* Drawer */
.drawer {
  position: fixed;
  inset: $header-height 0 0 0;
  z-index: 999;
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(2px);
}
.drawer__body {
  background: #fff;
  max-width: 420px;
  margin-left: auto;
  height: 100%;
  padding: $space-lg;
  overflow-y: auto;
  box-shadow: $shadow-lg;
}
.drawer__title {
  margin: $space-lg 0 $space-sm;
  font-size: 13px;
  font-weight: 700;
  color: $ink-3;
  letter-spacing: 0.06em;
}
.drawer__cats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}
.drawer__cat {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: $radius-md;
  text-decoration: none;
  color: $ink-2;
  background: #f6f8fb;
  transition: background 0.2s, color 0.2s;
  &:hover { background: $brand-soft; color: $brand; }
}
.drawer__cat-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  color: $brand;
  background: $brand-soft;
}
.drawer__all {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
  padding: 14px;
  border-radius: $radius-md;
  text-decoration: none;
  font-weight: 600;
  color: #fff;
  background: linear-gradient(135deg, $brand 0%, $brand-active 100%);
  box-shadow: $shadow-sm;
  transition: filter 0.2s $ease-smooth;

  .drawer__all-arrow { margin-left: auto; }
  &:hover { filter: brightness(1.05); }
}
.drawer__foot {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: $space-lg;
  padding-top: $space-lg;
  border-top: 1px solid $line;
}
.drawer__link {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: $radius-md;
  text-decoration: none;
  color: $ink-1;
  background: #f6f8fb;
  &:hover { background: $brand-soft; color: $brand; }
}

/* Transitions */
.drop-enter-active, .drop-leave-active { transition: opacity 0.2s $ease-smooth, transform 0.2s $ease-smooth; }
.drop-enter-from, .drop-leave-to { opacity: 0; transform: translateY(-6px); }
.slide-enter-active, .slide-leave-active { transition: opacity 0.25s $ease-smooth; }
.slide-enter-from, .slide-leave-to { opacity: 0; }

/* Responsive */
@media (max-width: 991px) {
  .cat, .action--cart .action__text, .action--login .action__text, .user__name { display: none; }
  .hamburger { display: grid; place-items: center; }
  .app-header__inner { gap: $space-sm; padding: 0 $space-md; }
}

@media (max-width: 640px) {
  .search { display: none; }
  .search--open { display: block; width: 100%; }
  .brand__name { display: none; }
  .app-header__inner { gap: 8px; }
}
</style>
