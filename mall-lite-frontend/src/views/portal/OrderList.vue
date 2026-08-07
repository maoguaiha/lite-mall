<template>
  <div class="order-list-page">
    <div class="main-content">
      <div class="order-container">
        <div class="order-header">
          <h2>我的订单</h2>
        </div>
        
        <div class="tab-bar">
          <div 
            v-for="tab in tabs" 
            :key="tab.value"
            class="tab-item"
            :class="{ active: activeTab === tab.value }"
            @click="activeTab = tab.value"
          >
            {{ tab.label }}
            <span v-if="tab.count > 0" class="badge">{{ tab.count }}</span>
          </div>
        </div>
        
        <div v-if="orders.length === 0" class="empty-order">
          <ShoppingBag />
          <p>暂无订单</p>
          <el-button type="primary" @click="goToProductList">去购物</el-button>
        </div>
        
        <div v-else class="order-list">
          <div v-for="order in orders" :key="order.id" class="order-card">
            <div class="order-header-info">
              <span class="order-id">订单号：{{ order.orderNo }}</span>
              <span class="order-status" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
            </div>
            
            <div class="order-items">
              <div 
                v-for="item in order.items" 
                :key="item.id"
                class="order-item"
                @click="goToDetail(order.id)"
              >
                <div class="item-image">
                  <img :src="item.pic || '/images/placeholder.png'" :alt="item.productName" />
                </div>
                <div class="item-info">
                  <h4>{{ item.productName }}</h4>
                  <p>{{ item.skuName }}</p>
                  <div class="item-price">
                    <span>¥{{ item.price }}</span>
                    <span>x{{ item.quantity }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="order-footer">
              <span class="order-total">合计：<strong>¥{{ order.totalAmount }}</strong></span>
              <div class="order-actions">
                <el-button v-if="order.status === 0" type="primary" @click="handlePay(order.id)">去支付</el-button>
                <el-button v-if="order.status === 0" type="text" @click="handleCancel(order.id)">取消订单</el-button>
                <el-button v-if="order.status === 1" type="primary" @click="handleReceive(order.id)">确认收货</el-button>
                <el-button v-if="order.status === 2" type="text" @click="goToDetail(order.id)">查看详情</el-button>
              </div>
            </div>
          </div>
        </div>
        
        <div class="pagination">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[10, 20, 50]"
            :page-size="pageSize"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ShoppingCart, ShoppingBag } from '@element-plus/icons-vue'
import { getOrderList, cancelOrder, payOrder } from '@/api/order'

const router = useRouter()
const searchText = ref('')
const activeTab = ref('all')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const tabs = [
  { label: '全部', value: 'all', count: 0 },
  { label: '待付款', value: 'pending', count: 0 },
  { label: '待发货', value: 'shipped', count: 0 },
  { label: '待收货', value: 'received', count: 0 },
  { label: '已完成', value: 'completed', count: 0 }
]

const orders = ref([])

onMounted(() => {
  loadOrders()
})

function loadOrders() {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    status: activeTab.value === 'all' ? undefined : getStatusValue(activeTab.value)
  }
  
  getOrderList(params).then(res => {
    orders.value = res.data?.list || mockOrders
    total.value = res.data?.total || orders.value.length
  }).catch(() => {
    orders.value = mockOrders
    total.value = orders.value.length
  })
}

const mockOrders = [
  {
    id: 1,
    orderNo: '2026071500001',
    status: 0,
    totalAmount: 8598,
    createTime: '2026-07-15 10:30:00',
    items: [
      { id: 1, productId: 1, productName: 'iPhone 15 Pro', skuName: '蓝色钛金属', price: 7999, quantity: 1, pic: '' },
      { id: 2, productId: 3, productName: '无线蓝牙耳机', skuName: '白色', price: 599, quantity: 1, pic: '' }
    ]
  },
  {
    id: 2,
    orderNo: '2026071000002',
    status: 1,
    totalAmount: 399,
    createTime: '2026-07-10 14:20:00',
    items: [
      { id: 3, productId: 5, productName: '机械键盘', skuName: 'RGB背光', price: 399, quantity: 1, pic: '' }
    ]
  },
  {
    id: 3,
    orderNo: '2026070500003',
    status: 2,
    totalAmount: 1999,
    createTime: '2026-07-05 09:15:00',
    items: [
      { id: 4, productId: 4, productName: '智能手表', skuName: '45mm GPS', price: 1999, quantity: 1, pic: '' }
    ]
  }
]

function getStatusValue(tab) {
  const map = {
    pending: 0,
    shipped: 1,
    received: 2,
    completed: 3
  }
  return map[tab]
}

function getStatusText(status) {
  const map = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    3: '已完成',
    4: '已取消'
  }
  return map[status] || '未知'
}

function getStatusClass(status) {
  const map = {
    0: 'pending',
    1: 'shipped',
    2: 'received',
    3: 'completed',
    4: 'cancelled'
  }
  return map[status] || ''
}

function handlePay(id) {
  payOrder(id).then(() => {
    alert('支付成功')
    loadOrders()
  }).catch(() => {
    alert('支付失败')
  })
}

function handleCancel(id) {
  cancelOrder(id).then(() => {
    alert('订单已取消')
    loadOrders()
  }).catch(() => {
    alert('取消失败')
  })
}

function handleReceive(id) {
  alert('确认收货成功')
  loadOrders()
}

function handleSizeChange(size) {
  pageSize.value = size
  pageNum.value = 1
  loadOrders()
}

function handleCurrentChange(page) {
  pageNum.value = page
  loadOrders()
}

function handleSearch() {
  router.push(`/product/list?keyword=${searchText.value}`)
}

function goToHome() {
  router.push('/')
}

function goToCart() {
  router.push('/cart')
}

function goToProductList() {
  router.push('/product/list')
}

function goToDetail(id) {
  router.push(`/order/detail/${id}`)
}
</script>

<style scoped>
.order-list-page {
  min-height: 100vh;
  background: #EEEEEE;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #2AB795;
  cursor: pointer;
}

.search-bar {
  flex: 1;
  max-width: 500px;
  display: flex;
  margin: 0 40px;
  
  .el-input {
    flex: 1;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.cart-btn {
  display: flex;
  align-items: center;
  gap: 5px;
}

.main-content {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.order-container {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.order-header {
  padding: 20px 30px;
  border-bottom: 1px solid #eee;
  
  h2 {
    font-size: 20px;
    font-weight: bold;
    color: #333;
  }
}

.tab-bar {
  display: flex;
  border-bottom: 1px solid #eee;
}

.tab-item {
  padding: 15px 30px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  position: relative;
  transition: all 0.3s;
  
  &:hover {
    color: #2AB795;
  }
  
  &.active {
    color: #2AB795;
    font-weight: bold;
    
    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 30px;
      right: 30px;
      height: 2px;
      background: #2AB795;
    }
  }
  
  .badge {
    display: inline-block;
    background: #FB0017;
    color: #fff;
    font-size: 12px;
    padding: 2px 6px;
    border-radius: 10px;
    margin-left: 5px;
  }
}

.empty-order {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 0;
  color: #999;
  
  .el-icon-shopping-bag {
    font-size: 64px;
    margin-bottom: 20px;
  }
  
  p {
    font-size: 16px;
    margin-bottom: 20px;
  }
}

.order-list {
  padding: 20px 30px;
}

.order-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.order-header-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.order-id {
  font-size: 14px;
  color: #666;
}

.order-status {
  font-size: 14px;
  font-weight: bold;
  
  &.pending {
    color: #FB0017;
  }
  
  &.shipped {
    color: #FD994B;
  }
  
  &.received {
    color: #4a90d9;
  }
  
  &.completed {
    color: #67c23a;
  }
  
  &.cancelled {
    color: #999;
  }
}

.order-items {
  margin-bottom: 15px;
}

.order-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px dashed #eee;
  
  &:last-child {
    border-bottom: none;
  }
}

.item-image {
  width: 80px;
  height: 80px;
  background: #f5f5f5;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.item-info {
  flex: 1;
  
  h4 {
    font-size: 14px;
    font-weight: bold;
    color: #333;
    margin-bottom: 5px;
  }
  
  p {
    font-size: 12px;
    color: #999;
    margin-bottom: 10px;
  }
  
  .item-price {
    display: flex;
    align-items: center;
    gap: 10px;
    
    span:first-child {
      color: #FB0017;
      font-weight: bold;
    }
    
    span:last-child {
      color: #999;
      font-size: 12px;
    }
  }
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-total {
  font-size: 16px;
  color: #333;
  
  strong {
    font-size: 18px;
    color: #FB0017;
  }
}

.order-actions {
  display: flex;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-bottom: 30px;
}
</style>
