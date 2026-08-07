<template>
  <div class="portal">
    <AppHeader />
    <main class="portal-main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
</script>

<style scoped lang="scss">
@use '../../styles/variables' as *;

.portal {
  min-height: 100vh;
  background: $bg-color;
}

.portal-main {
  max-width: $content-max;
  margin: 0 auto;
  padding: $space-xl $space-lg $space-xl;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s $ease-smooth;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 640px) {
  .portal-main {
    padding: $space-lg $space-md $space-xl;
  }
}
</style>
