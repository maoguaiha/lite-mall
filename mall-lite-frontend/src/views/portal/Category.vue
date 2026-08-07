<template>
  <div class="cat-page">
    <!-- 顶部返回栏 -->
    <div class="cat-bar">
      <button class="cat-bar__back" @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回</span>
      </button>
      <h1 class="cat-bar__title">全部分类</h1>
      <span class="cat-bar__count">{{ topCategories.length }} 个分类</span>
    </div>

    <div class="cat-wrap">
      <section
        v-for="top in topCategories"
        :key="top.id"
        :id="`cat-${top.id}`"
        class="cat-card"
      >
        <div class="cat-card__head">
          <span class="cat-card__icon">
            <el-icon><component :is="iconForCategory(top.id)" /></el-icon>
          </span>
          <router-link :to="`/product/list?categoryId=${top.id}`" class="cat-card__name">
            {{ top.name }}
          </router-link>
          <router-link :to="`/product/list?categoryId=${top.id}`" class="cat-card__more">
            全部商品 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="cat-card__subs">
          <router-link
            v-for="sub in (group[top.id] || [])"
            :key="sub.id"
            :to="`/product/list?categoryId=${sub.id}`"
            class="sub-chip"
          >
            {{ sub.name }}
          </router-link>
          <span v-if="!(group[top.id] || []).length" class="sub-empty">暂无子分类</span>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { getCategoryAll } from '@/api/product'
import { iconForCategory } from '@/utils/categoryIcons'

const router = useRouter()
const all = ref([])

const topCategories = computed(() =>
  all.value.filter((c) => c.parentId === 0 || c.parentId === '0')
)

const group = computed(() => {
  const map = {}
  for (const c of all.value) {
    if (c.parentId && c.parentId !== 0 && c.parentId !== '0') {
      ;(map[c.parentId] = map[c.parentId] || []).push(c)
    }
  }
  return map
})

function goBack() {
  if (window.history.length > 1) router.back()
  else router.push('/')
}

onMounted(async () => {
  try {
    const res = await getCategoryAll()
    const raw = res?.data ?? res
    all.value = Array.isArray(raw) ? raw : []
  } catch (e) {
    // 分类获取失败保留空态
  }
})
</script>

<style scoped lang="scss">
@use '../../styles/variables' as *;

.cat-page {
  min-height: calc(100vh - #{$header-height});
  background:
    radial-gradient(1100px 460px at 12% -8%, rgba(37, 99, 235, 0.08), transparent 60%),
    radial-gradient(900px 420px at 100% 0%, rgba(16, 185, 129, 0.07), transparent 55%),
    $bg-color;
  padding-bottom: $space-xl;
}

/* 顶部返回栏 */
.cat-bar {
  position: sticky;
  top: $header-height;
  z-index: 20;
  max-width: $content-max;
  margin: 0 auto;
  padding: $space-lg $space-lg $space-md;
  display: flex;
  align-items: center;
  gap: 14px;

  &__back {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    height: 38px;
    padding: 0 16px;
    border: 1px solid $line;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.7);
    backdrop-filter: saturate(160%) blur(10px);
    -webkit-backdrop-filter: saturate(160%) blur(10px);
    color: $ink-2;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    box-shadow: $shadow-sm;
    transition: background 0.2s $ease-smooth, color 0.2s $ease-smooth,
      transform 0.2s $ease-smooth;

    &:hover {
      background: #fff;
      color: $brand;
      transform: translateX(-2px);
    }
    .el-icon { font-size: 16px; }
  }

  &__title {
    margin: 0;
    font-size: 22px;
    font-weight: 800;
    letter-spacing: -0.01em;
    color: $ink-1;
  }

  &__count {
    margin-left: auto;
    font-size: 13px;
    color: $ink-3;
    background: rgba(255, 255, 255, 0.7);
    border: 1px solid $line;
    padding: 6px 12px;
    border-radius: 999px;
  }
}

.cat-wrap {
  max-width: $content-max;
  margin: 0 auto;
  padding: 0 $space-lg;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $space-md;
}

/* 分类卡片 */
.cat-card {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: saturate(160%) blur(12px);
  -webkit-backdrop-filter: saturate(160%) blur(12px);
  border: 1px solid rgba(15, 23, 42, 0.06);
  border-radius: 20px;
  padding: $space-lg;
  box-shadow: 0 16px 36px -18px rgba(15, 23, 42, 0.18);
  transition: transform 0.25s $ease-smooth, box-shadow 0.25s $ease-smooth;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 26px 48px -20px rgba(15, 23, 42, 0.26);
  }

  &__head {
    display: flex;
    align-items: center;
    gap: 12px;
    padding-bottom: $space-sm;
    margin-bottom: $space-sm;
    border-bottom: 1px solid $line;
  }

  &__icon {
    width: 42px;
    height: 42px;
    border-radius: 12px;
    display: grid;
    place-items: center;
    font-size: 22px;
    color: #fff;
    background: linear-gradient(135deg, $brand 0%, $brand-active 100%);
    box-shadow: $shadow-sm;
    flex-shrink: 0;
  }

  &__name {
    font-size: 17px;
    font-weight: 800;
    color: $ink-1;
    text-decoration: none;
    &:hover { color: $brand; }
  }

  &__more {
    margin-left: auto;
    display: inline-flex;
    align-items: center;
    gap: 2px;
    font-size: 13px;
    font-weight: 600;
    color: $brand;
    text-decoration: none;
    transition: gap 0.2s $ease-smooth;
    &:hover { gap: 6px; }
  }

  &__subs {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
}

.sub-chip {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.07);
  color: $ink-2;
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
  transition: background 0.2s $ease-smooth, color 0.2s $ease-smooth,
    transform 0.2s $ease-smooth;

  &:hover {
    background: $brand;
    color: #fff;
    transform: translateY(-1px);
  }
}

.sub-empty {
  font-size: 13px;
  color: $ink-3;
}

@media (max-width: 760px) {
  .cat-wrap { grid-template-columns: 1fr; }
}
</style>
