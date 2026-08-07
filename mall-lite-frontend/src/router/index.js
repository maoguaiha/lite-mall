import { createRouter, createWebHistory } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    component: () => import('@/views/portal/Layout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/portal/Home.vue')
      },
      {
        path: 'login',
        name: 'Login',
        component: () => import('@/views/portal/Login.vue')
      },
      {
        path: 'register',
        name: 'Register',
        component: () => import('@/views/portal/Register.vue')
      },
      {
        path: 'product/list',
        name: 'ProductList',
        component: () => import('@/views/portal/ProductList.vue')
      },
      {
        path: 'product/detail/:id',
        name: 'ProductDetail',
        component: () => import('@/views/portal/ProductDetail.vue')
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/portal/Cart.vue')
      },
      {
        path: 'order/list',
        name: 'OrderList',
        component: () => import('@/views/portal/OrderList.vue')
      },
      {
        path: 'order/detail/:id',
        name: 'OrderDetail',
        component: () => import('@/views/portal/OrderDetail.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/portal/Profile.vue')
      },
      {
        path: 'address',
        name: 'Address',
        component: () => import('@/views/portal/Address.vue')
      },
      {
        path: 'comment',
        name: 'Comment',
        component: () => import('@/views/portal/Comment.vue')
      },
      {
        path: 'coupon',
        name: 'Coupon',
        component: () => import('@/views/portal/Coupon.vue')
      },
      {
        path: 'seckill',
        name: 'Seckill',
        component: () => import('@/views/portal/Seckill.vue')
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/portal/Category.vue')
      }
    ]
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/Login.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/Layout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'product/list',
        name: 'AdminProductList',
        component: () => import('@/views/admin/product/ProductList.vue')
      },
      {
        path: 'product/add',
        name: 'AdminProductAdd',
        component: () => import('@/views/admin/product/ProductAdd.vue')
      },
      {
        path: 'product/edit/:id',
        name: 'AdminProductEdit',
        component: () => import('@/views/admin/product/ProductEdit.vue')
      },
      {
        path: 'product/category',
        name: 'AdminProductCategory',
        component: () => import('@/views/admin/product/CategoryList.vue')
      },
      {
        path: 'order/list',
        name: 'AdminOrderList',
        component: () => import('@/views/admin/order/OrderList.vue')
      },
      {
        path: 'order/setting',
        name: 'AdminOrderSetting',
        component: () => import('@/views/admin/order/OrderSetting.vue')
      },
      {
        path: 'member/list',
        name: 'AdminMemberList',
        component: () => import('@/views/admin/member/MemberList.vue')
      },
      {
        path: 'member/level',
        name: 'AdminMemberLevel',
        component: () => import('@/views/admin/member/MemberLevel.vue')
      },
      {
        path: 'coupon/list',
        name: 'AdminCouponList',
        component: () => import('@/views/admin/coupon/CouponList.vue')
      },
      {
        path: 'comment/list',
        name: 'AdminCommentList',
        component: () => import('@/views/admin/comment/CommentList.vue')
      },
      {
        path: 'flash/list',
        name: 'AdminFlashList',
        component: () => import('@/views/admin/promotion/FlashList.vue')
      },
      {
        path: 'advertise/list',
        name: 'AdminAdvertiseList',
        component: () => import('@/views/admin/advertise/AdvertiseList.vue')
      },
      {
        path: 'subject/list',
        name: 'AdminSubjectList',
        component: () => import('@/views/admin/content/SubjectList.vue')
      },
      {
        path: 'admin/list',
        name: 'AdminAdminList',
        component: () => import('@/views/admin/system/AdminList.vue'),
        meta: { title: '管理员管理', roles: ['ROLE_ADMIN'] }
      },
      {
        path: 'role/list',
        name: 'AdminRoleList',
        component: () => import('@/views/admin/system/RoleList.vue'),
        meta: { title: '角色管理', roles: ['ROLE_ADMIN'] }
      },
      {
        path: 'menu/list',
        name: 'AdminMenuList',
        component: () => import('@/views/admin/system/MenuList.vue'),
        meta: { title: '菜单管理', roles: ['ROLE_ADMIN'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 前端 RBAC 守卫：标记了 meta.roles 的路由要求登录管理员具备相应角色
router.beforeEach((to) => {
  const adminStore = useAdminStore()
  const required = to.meta && to.meta.roles
  if (Array.isArray(required) && required.length) {
    const ok = required.some((r) => (adminStore.roles || []).includes(r))
    if (!ok) {
      ElMessage.error('无权限访问该页面')
      return '/admin/dashboard'
    }
  }
  return true
})

export default router
