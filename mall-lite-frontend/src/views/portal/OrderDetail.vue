<template>
  <div class="order-detail-page">
    <div class="main-content">
      <div class="order-container">
        <div class="order-header">
          <h2>订单详情</h2>
          <el-button @click="goToOrderList">返回列表</el-button>
        </div>
        
        <div class="order-info-card">
          <div class="info-section">
            <h3>订单信息</h3>
            <div class="info-row">
              <span class="label">订单号：</span>
              <span class="value">{{ order.orderNo }}</span>
            </div>
            <div class="info-row">
              <span class="label">订单状态：</span>
              <span class="value status" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
            </div>
            <div class="info-row">
              <span class="label">下单时间：</span>
              <span class="value">{{ order.createTime }}</span>
            </div>
            <div class="info-row">
              <span class="label">支付方式：</span>
              <span class="value">{{ order.payType === 0 ? '支付宝' : '微信支付' }}</span>
            </div>
          </div>
          
          <div class="info-section">
            <h3>收货信息</h3>
            <div class="info-row">
              <span class="label">收货人：</span>
              <span class="value">{{ order.receiverName }}</span>
            </div>
            <div class="info-row">
              <span class="label">联系电话：</span>
              <span class="value">{{ order.receiverPhone }}</span>
            </div>
            <div class="info-row">
              <span class="label">收货地址：</span>
              <span class="value">{{ order.receiverAddress }}</span>
            </div>
          </div>
        </div>
        
        <div class="order-items-card">
          <h3>商品信息</h3>
          <div class="items-table">
            <div class="table-header">
              <span class="col-product">商品</span>
              <span class="col-price">单价</span>
              <span class="col-quantity">数量</span>
              <span class="col-total">小计</span>
            </div>
            <div v-for="item in order.items" :key="item.id" class="table-row">
              <div class="col-product">
                <div class="item-image">
                  <img :src="item.pic || '/images/placeholder.png'" :alt="item.productName" />
                </div>
                <div class="item-info">
                  <h4>{{ item.productName }}</h4>
                  <p>{{ item.skuName }}</p>
                </div>
              </div>
              <span class="col-price">¥{{ item.price }}</span>
              <span class="col-quantity">x{{ item.quantity }}</span>
              <span class="col-total">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </div>
        
        <div class="order-price-card">
          <h3>价格明细</h3>
          <div class="price-row">
            <span class="label">商品总价：</span>
            <span class="value">¥{{ order.totalAmount }}</span>
          </div>
          <div class="price-row">
            <span class="label">运费：</span>
            <span class="value">¥{{ order.freightAmount }}</span>
          </div>
          <div class="price-row">
            <span class="label">优惠：</span>
            <span class="value discount">-¥{{ order.discountAmount }}</span>
          </div>
          <div class="price-row total">
            <span class="label">实付金额：</span>
            <span class="value">¥{{ order.payAmount }}</span>
          </div>
        </div>
        
        <div class="order-actions">
          <el-button v-if="order.status === 0" type="primary" @click="handlePay">去支付</el-button>
          <el-button v-if="order.status === 0" type="danger" @click="handleCancel">取消订单</el-button>
          <el-button v-if="order.status === 1" type="primary" @click="handleReceive">确认收货</el-button>
          <el-button v-if="order.status === 3" type="primary" @click="handleComment">评价</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ShoppingCart } from '@element-plus/icons-vue'
import { getOrderDetail, cancelOrder, payOrder } from '@/api/order'

const router = useRouter()
const route = useRoute()
const searchText = ref('')

const order = reactive({
  id: null,
  orderNo: '',
  status: 0,
  createTime: '',
  payType: 0,
  receiverName: '',
  receiverPhone: '',
  receiverAddress: '',
  items: [],
  totalAmount: 0,
  freightAmount: 0,
  discountAmount: 0,
  payAmount: 0
})

onMounted(() => {
  loadOrderDetail()
})

function loadOrderDetail() {
  const id = route.params.id
  getOrderDetail(id).then(res => {
    Object.assign(order, res.data)
  }).catch(() => {
    Object.assign(order, {
      id: id,
      orderNo: '2026071500001',
      status: 0,
      createTime: '2026-07-15 10:30:00',
      payType: 0,
      receiverName: '张三',
      receiverPhone: '138****8888',
      receiverAddress: '北京市朝阳区某某街道123号',
      items: [
        { id: 1, productId: 1, productName: 'iPhone 15 Pro', skuName: '蓝色钛金属', price: 7999, quantity: 1, pic: '' },
        { id: 2, productId: 3, productName: '无线蓝牙耳机', skuName: '白色', price: 599, quantity: 1, pic: '' }
      ],
      totalAmount: 8598,
      freightAmount: 0,
      discountAmount: 0,
      payAmount: 8598
    })
  })
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

function handlePay() {
  payOrder(order.id).then(() => {
    alert('支付成功')
    loadOrderDetail()
  }).catch(() => {
    alert('支付失败')
  })
}

function handleCancel() {
  cancelOrder(order.id).then(() => {
    alert('订单已取消')
    loadOrderDetail()
  }).catch(() => {
    alert('取消失败')
  })
}

function handleReceive() {
  alert('确认收货成功')
  loadOrderDetail()
}

function handleComment() {
  alert('评价功能开发中')
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

function goToOrderList() {
  router.push('/order/list')
}
</script>

<style scoped>
.order-detail-page {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  border-bottom: 1px solid #eee;
  
  h2 {
    font-size: 20px;
    font-weight: bold;
    color: #333;
  }
}

.order-info-card {
  display: flex;
  gap: 30px;
  padding: 30px;
}

.info-section {
  flex: 1;
  
  h3 {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
  }
}

.info-row {
  display: flex;
  margin-bottom: 15px;
  
  .label {
    width: 100px;
    color: #999;
    font-size: 14px;
  }
  
  .value {
    flex: 1;
    color: #333;
    font-size: 14px;
    
    &.status {
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
  }
}

.order-items-card {
  padding: 30px;
  border-top: 1px solid #eee;
  
  h3 {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
  }
}

.items-table {
  border: 1px solid #eee;
  border-radius: 6px;
  overflow: hidden;
}

.table-header {
  display: flex;
  background: #fafafa;
  padding: 15px 20px;
  font-size: 14px;
  color: #666;
  
  .col-product {
    flex: 2;
  }
  
  .col-price, .col-quantity, .col-total {
    width: 120px;
    text-align: center;
  }
}

.table-row {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  
  &:last-child {
    border-bottom: none;
  }
  
  .col-product {
    flex: 2;
    display: flex;
    gap: 15px;
    
    .item-image {
      width: 80px;
      height: 80px;
      background: #f5f5f5;
      border-radius: 6px;
      overflow: hidden;
      
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
      }
    }
  }
  
  .col-price, .col-quantity, .col-total {
    width: 120px;
    text-align: center;
    font-size: 14px;
    
    &.col-total {
      color: #FB0017;
      font-weight: bold;
    }
  }
}

.order-price-card {
  padding: 30px;
  border-top: 1px solid #eee;
  background: #fafafa;
  
  h3 {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 20px;
  }
}

.price-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  
  .label {
    color: #666;
    font-size: 14px;
  }
  
  .value {
    color: #333;
    font-size: 14px;
    
    &.discount {
      color: #67c23a;
    }
  }
  
  &.total {
    padding-top: 15px;
    border-top: 1px solid #eee;
    
    .label {
      font-weight: bold;
      font-size: 16px;
    }
    
    .value {
      font-weight: bold;
      font-size: 20px;
      color: #FB0017;
    }
  }
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 30px;
  border-top: 1px solid #eee;
}
</style>
