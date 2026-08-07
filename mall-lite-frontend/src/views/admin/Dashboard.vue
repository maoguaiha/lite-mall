<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in cards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value">{{ card.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header>近 7 天订单趋势</template>
          <svg class="line-chart" viewBox="0 0 520 260" preserveAspectRatio="none">
            <polyline :points="linePoints" fill="none" stroke="#409eff" stroke-width="3" />
            <text v-for="(p, i) in points" :key="'t' + i" :x="p.x" :y="240" class="axis-text">{{ orderTrend[i]?.date }}</text>
            <text v-for="(p, i) in points" :key="'v' + i" :x="p.x" :y="p.y - 8" class="val-text">{{ orderTrend[i]?.count }}</text>
          </svg>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>商品分类占比</template>
          <div class="pie-wrap">
            <div class="pie" :style="{ background: pieGradient }"></div>
            <ul class="legend">
              <li v-for="item in categoryStats" :key="item.categoryId">
                <span class="dot" :style="{ background: colorOf(item.categoryId) }"></span>
                {{ item.categoryName }}（{{ item.count }}）
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="tables-row">
      <el-col :span="14">
        <el-card shadow="hover">
          <template #header>最近订单</template>
          <el-table :data="recentOrders" size="small" stripe>
            <el-table-column prop="orderSn" label="订单号" min-width="140" />
            <el-table-column prop="memberUsername" label="用户" min-width="90" />
            <el-table-column label="金额" min-width="90">
              <template #default="{ row }">¥{{ row.totalAmount }}</template>
            </el-table-column>
            <el-table-column label="状态" min-width="80">
              <template #default="{ row }">
                <el-tag :type="orderStatusType(row.status)" size="small">{{ orderStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="下单时间" min-width="160">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="hover">
          <template #header>热销商品</template>
          <el-table :data="hotProducts" size="small" stripe>
            <el-table-column prop="name" label="商品" min-width="140" show-overflow-tooltip />
            <el-table-column label="价格" min-width="90">
              <template #default="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column prop="sales" label="销量" min-width="70" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import adminRequest from '@/utils/adminRequest'

function formatPrice(v) {
  const n = Number(v || 0)
  return n.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const stats = reactive({
  orderCount: 0,
  memberCount: 0,
  productCount: 0,
  totalSales: 0,
  commentPending: 0,
})
const recentOrders = ref([])
const hotProducts = ref([])
const categoryStats = ref([])
const orderTrend = ref([])

const cards = computed(() => [
  { label: '订单总数', value: stats.orderCount },
  { label: '会员总数', value: stats.memberCount },
  { label: '上架商品', value: stats.productCount },
  { label: '总销售额', value: formatPrice(stats.totalSales) },
])

const PALETTE = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#9254de', '#13c2c2']
function colorOf(id) {
  const idx = categoryStats.value.findIndex(c => c.categoryId === id)
  return PALETTE[(idx >= 0 ? idx : 0) % PALETTE.length]
}
const pieGradient = computed(() => {
  const total = categoryStats.value.reduce((s, c) => s + (c.count || 0), 0)
  if (total === 0) return '#eee'
  let acc = 0
  const stops = categoryStats.value.map(c => {
    const start = (acc / total) * 100
    acc += c.count
    const end = (acc / total) * 100
    return `${colorOf(c.categoryId)} ${start}% ${end}%`
  })
  return `conic-gradient(${stops.join(', ')})`
})

const points = computed(() => {
  const data = orderTrend.value
  const max = Math.max(1, ...data.map(d => d.count || 0))
  const W = 520, H = 260, pad = 30
  const step = data.length > 1 ? (W - pad * 2) / (data.length - 1) : 0
  return data.map((d, i) => ({
    x: pad + step * i,
    y: H - pad - ((d.count || 0) / max) * (H - pad * 2),
  }))
})
const linePoints = computed(() => points.value.map(p => `${p.x},${p.y}`).join(' '))

function orderStatusText(status) {
  return ['待付款', '待发货', '已发货', '已完成', '已取消'][status] || '未知'
}
function orderStatusType(status) {
  return ['warning', 'primary', 'success', 'success', 'info'][status] || 'info'
}
function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

onMounted(async () => {
  const [s, r, h, c, t] = await Promise.all([
    adminRequest.get('/admin/dashboard/stats'),
    adminRequest.get('/admin/dashboard/recent-orders'),
    adminRequest.get('/admin/dashboard/hot-products'),
    adminRequest.get('/admin/dashboard/category-stats'),
    adminRequest.get('/admin/dashboard/order-trend'),
  ])
  Object.assign(stats, s.data || {})
  recentOrders.value = r.data || []
  hotProducts.value = h.data || []
  categoryStats.value = c.data || []
  orderTrend.value = t.data || []
})
</script>

<style scoped>
.dashboard { padding: 8px; }
.stat-card { border-radius: 10px; }
.stat-label { color: #909399; font-size: 13px; }
.stat-value { font-size: 26px; font-weight: 600; margin-top: 8px; }
.charts-row, .tables-row { margin-top: 20px; }
.line-chart { width: 100%; height: 260px; }
.axis-text { font-size: 11px; fill: #909399; text-anchor: middle; }
.val-text { font-size: 11px; fill: #409eff; text-anchor: middle; }
.pie-wrap { display: flex; align-items: center; gap: 20px; }
.pie { width: 160px; height: 160px; border-radius: 50%; flex: none; }
.legend { list-style: none; padding: 0; margin: 0; font-size: 13px; }
.legend li { display: flex; align-items: center; margin-bottom: 6px; }
.dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; margin-right: 8px; }
</style>
