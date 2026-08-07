<template>
  <div class="product-image" :style="frameStyle" :class="{ 'is-loaded': loaded && src && !failed }">
    <!-- 真实图片：加载完成后淡入 -->
    <img
      v-if="src && !failed"
      :src="src"
      :alt="alt"
      class="real"
      :class="{ loaded }"
      @load="loaded = true"
      @error="failed = true"
    />
    <!-- 本地自包含占位图（固定 1:1，柔和品牌渐变 + 图标） -->
    <div v-if="!src || failed || !loaded" class="placeholder" :style="placeholderStyle">
      <svg class="ph-icon" viewBox="0 0 48 48" aria-hidden="true">
        <path
          d="M8 14a4 4 0 0 1 4-4h24a4 4 0 0 1 4 4v20a4 4 0 0 1-4 4H12a4 4 0 0 1-4-4V14Z"
          fill="none"
          stroke="currentColor"
          stroke-width="2.4"
        />
        <circle cx="18" cy="20" r="3" fill="currentColor" />
        <path d="M11 33l9-9 6 6 5-5 6 6" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round" />
      </svg>
      <span v-if="label" class="ph-label">{{ label }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  src: { type: String, default: '' },
  alt: { type: String, default: '' },
  label: { type: String, default: '' },
  seed: { type: [String, Number], default: '' }
})

const loaded = ref(false)
const failed = ref(false)

// 基于 seed 生成柔和、统一的品牌蓝/灰渐变（避免彩虹色）
const palette = [
  ['#eef3ff', '#dbe6ff'],
  ['#eaf1f8', '#d7e6f5'],
  ['#f1f5fb', '#e0e9f5'],
  ['#edf2fb', '#dbe7fa'],
  ['#eef4f4', '#dde9ea']
]

const placeholderStyle = computed(() => {
  const key = String(props.seed || props.alt || props.label || 'x')
  let h = 0
  for (let i = 0; i < key.length; i++) h = (h * 31 + key.charCodeAt(i)) >>> 0
  const [a, b] = palette[h % palette.length]
  return {
    background: `linear-gradient(135deg, ${a} 0%, ${b} 100%)`,
    color: '#94a3b8'
  }
})

const frameStyle = computed(() => ({
  aspectRatio: '1 / 1'
}))
</script>

<style scoped lang="scss">
@use '../styles/variables' as *;

.product-image {
  position: relative;
  width: 100%;
  overflow: hidden;
  background: $bg-color;
  border-radius: $radius-md;
}

.real {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transform: scale(1.04);
  transition: opacity 0.5s $ease-smooth, transform 0.6s $ease-smooth;
}

.real.loaded {
  opacity: 1;
  transform: scale(1);
}

.placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.ph-icon {
  width: 34%;
  max-width: 56px;
  height: auto;
}

.ph-label {
  font-size: 12px;
  letter-spacing: 0.04em;
  color: #94a3b8;
}
</style>
