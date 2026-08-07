<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h1>mall-lite</h1>
      </div>
      <el-menu
        :default-active="activeMenu"
        mode="vertical"
        background-color="#2A3F5F"
        text-color="#fff"
        active-text-color="#2AB795"
        :collapse="collapsed"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><component :is="icons.Dashboard" /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        
        <el-sub-menu index="product">
          <template #title>
            <el-icon><component :is="icons.Goods" /></el-icon>
            <span>商品管理</span>
          </template>
          <el-menu-item index="/admin/product/list">商品列表</el-menu-item>
          <el-menu-item index="/admin/product/add">添加商品</el-menu-item>
          <el-menu-item index="/admin/product/category">分类管理</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="order">
          <template #title>
            <el-icon><component :is="icons.ShoppingCart" /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/admin/order/list">订单列表</el-menu-item>
          <el-menu-item index="/admin/order/setting">订单设置</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="member">
          <template #title>
            <el-icon><component :is="icons.User" /></el-icon>
            <span>会员管理</span>
          </template>
          <el-menu-item index="/admin/member/list">会员列表</el-menu-item>
          <el-menu-item index="/admin/member/level">会员等级</el-menu-item>
          <el-menu-item index="/admin/comment/list">评价管理</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="promotion">
          <template #title>
            <el-icon><component :is="icons.Coupon" /></el-icon>
            <span>促销管理</span>
          </template>
          <el-menu-item index="/admin/coupon/list">优惠券管理</el-menu-item>
          <el-menu-item index="/admin/flash/list">秒杀活动</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="content">
          <template #title>
            <el-icon><component :is="icons.Picture" /></el-icon>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/admin/advertise/list">广告管理</el-menu-item>
          <el-menu-item index="/admin/subject/list">专题管理</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="system">
          <template #title>
            <el-icon><component :is="icons.Setting" /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/admin/admin/list">管理员管理</el-menu-item>
          <el-menu-item index="/admin/role/list">角色管理</el-menu-item>
          <el-menu-item index="/admin/menu/list">菜单管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </aside>
    
    <div class="main-content">
      <header class="header">
        <div class="header-left">
          <el-button @click="collapsed = !collapsed" class="collapse-btn">
            <el-icon><component :is="collapsed ? icons.Expand : icons.Collapse" /></el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path" :to="{ path: item.path }">
              {{ item.name }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span>管理员</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToProfile">个人中心</el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  DataBoard, Goods, ShoppingCart, User, Discount, Picture, Setting, 
  Expand, Fold, User as UserIcon 
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const collapsed = ref(false)

const icons = {
  Dashboard: DataBoard,
  Goods,
  ShoppingCart,
  User: UserIcon,
  Coupon: Discount,
  Picture,
  Setting,
  Expand,
  Collapse: Fold
}

const menuMap = {
  '/admin/dashboard': { name: '首页', parent: null },
  '/admin/product/list': { name: '商品列表', parent: '商品管理' },
  '/admin/product/add': { name: '添加商品', parent: '商品管理' },
  '/admin/product/category': { name: '分类管理', parent: '商品管理' },
  '/admin/order/list': { name: '订单列表', parent: '订单管理' },
  '/admin/order/setting': { name: '订单设置', parent: '订单管理' },
  '/admin/member/list': { name: '会员列表', parent: '会员管理' },
  '/admin/member/level': { name: '会员等级', parent: '会员管理' },
  '/admin/comment/list': { name: '评价管理', parent: '会员管理' },
  '/admin/coupon/list': { name: '优惠券管理', parent: '促销管理' },
  '/admin/flash/list': { name: '秒杀活动', parent: '促销管理' },
  '/admin/advertise/list': { name: '广告管理', parent: '内容管理' },
  '/admin/subject/list': { name: '专题管理', parent: '内容管理' },
  '/admin/admin/list': { name: '管理员管理', parent: '系统管理' },
  '/admin/role/list': { name: '角色管理', parent: '系统管理' },
  '/admin/menu/list': { name: '菜单管理', parent: '系统管理' }
}

const activeMenu = computed(() => route.path)

const breadcrumbs = computed(() => {
  const current = menuMap[route.path]
  if (!current) return []
  
  const items = []
  if (current.parent) {
    items.push({ name: current.parent, path: null })
  }
  items.push({ name: current.name, path: route.path })
  return items
})

onMounted(() => {
  if (!localStorage.getItem('adminToken')) {
    router.push('/admin/login')
  }
})

function handleLogout() {
  localStorage.removeItem('adminToken')
  router.push('/admin/login')
}

function goToProfile() {
  alert('个人中心开发中')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

.sidebar {
  width: 220px;
  background: #2A3F5F;
  transition: width 0.3s;
  flex-shrink: 0;
  
  &.is-collapsed {
    width: 60px;
  }
}

.sidebar-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  
  h1 {
    font-size: 20px;
    color: #fff;
    font-weight: bold;
  }
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #eee;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  width: 36px;
  height: 36px;
  padding: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  cursor: pointer;
  font-size: 14px;
}

.content-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.el-menu {
  border-right: none;
}

.el-menu-item,
.el-sub-menu__title {
  height: 48px;
  line-height: 48px;
}

.el-menu-item.is-active {
  background-color: rgba(42, 183, 149, 0.2);
}

.el-sub-menu .el-menu-item {
  padding-left: 48px !important;
}
</style>
