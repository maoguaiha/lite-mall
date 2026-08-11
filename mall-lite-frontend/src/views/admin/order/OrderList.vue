<template>
  <div class="order-list-page">
    <div class="page-header">
      <h2>订单列表</h2>
    </div>

    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="会员名称">
          <el-input v-model="searchForm.memberUsername" placeholder="请输入会员名称" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待付款" :value="0" />
            <el-option label="待发货" :value="1" />
            <el-option label="待收货" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="orders" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderSn" label="订单号" />
        <el-table-column prop="memberUsername" label="会员名称" width="120" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="payAmount" label="实付金额" width="120">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column label="订单状态" width="100">
          <template #default="{ row }">
            <span :class="getStatusClass(row.status)">{{ getStatusText(row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link @click="handleView(row.id)">查看</el-button>
            <el-button v-if="row.status === 1" link @click="handleShip(row)">发货</el-button>
            <el-button v-if="row.status === 0" link @click="handleRefund(row)">退款</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import adminRequest from '@/utils/adminRequest'

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const searchForm = reactive({
  memberUsername: '',
  status: ''
})

const orders = ref([])

async function loadOrders() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/order/list', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        memberUsername: searchForm.memberUsername || undefined,
        status: searchForm.status === '' ? undefined : searchForm.status
      }
    })
    orders.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function getStatusText(status) {
  const map = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}
function getStatusClass(status) {
  const map = {
    0: 'status-pending',
    1: 'status-shipped',
    2: 'status-received',
    3: 'status-completed',
    4: 'status-cancelled'
  }
  return map[status] || ''
}

function handleSearch() {
  pageNum.value = 1
  loadOrders()
}
function handleReset() {
  searchForm.memberUsername = ''
  searchForm.status = ''
  handleSearch()
}
function handleCurrentChange(page) {
  pageNum.value = page
  loadOrders()
}
function handleView(id) {
  ElMessage.info('订单详情 ID: ' + id)
}
async function handleShip(row) {
  try {
    const { value } = await ElMessageBox.prompt('请输入快递单号', '发货', {
      inputPattern: /\S+/,
      inputErrorMessage: '不能为空'
    })
    const company = '顺丰速运'
    await adminRequest.post('/admin/order/ship', null, {
      params: { id: row.id, deliveryCompany: company, deliverySn: value }
    })
    ElMessage.success('已发货')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '发货失败')
  }
}
async function handleRefund(row) {
  await ElMessageBox.confirm('确定对该订单退款？')
  await adminRequest.post('/admin/order/refund', null, { params: { id: row.id } })
  ElMessage.success('已退款')
  loadOrders()
}

onMounted(loadOrders)
</script>

<style scoped>
.order-list-page {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  h2 {
    font-size: 20px;
    font-weight: bold;
    color: #333;
  }
}
.search-form {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}
.table-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}
.status-pending {
  color: #f56c6c;
}
.status-shipped {
  color: #e6a23c;
}
.status-received {
  color: #409eff;
}
.status-completed {
  color: #67c23a;
}
.status-cancelled {
  color: #999;
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
