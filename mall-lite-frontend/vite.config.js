import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler'
      }
    }
  },
  build: {
    chunkSizeWarningLimit: 1500,
    rollupOptions: {
      output: {
        manualChunks: {
          'element-plus': ['element-plus', '@element-plus/icons-vue'],
          vue: ['vue', 'vue-router', 'pinia']
        }
      }
    }
  },
  server: {
    port: 8088,
    proxy: {
      // 门户后端 mall-portal :8080
      '/api': {
        target: 'http://localhost:18080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      // 商家端后端 mall-admin :8081
      '/admin-api': {
        target: 'http://localhost:18081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/admin-api/, '')
      }
    }
  },
  preview: {
    port: 4173,
    proxy: {
      // 门户后端 mall-portal :8080
      '/api': {
        target: 'http://localhost:18080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      // 商家端后端 mall-admin :8081
      '/admin-api': {
        target: 'http://localhost:18081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/admin-api/, '')
      }
    }
  }
})
