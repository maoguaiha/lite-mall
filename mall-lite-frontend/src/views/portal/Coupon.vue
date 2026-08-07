<template>
  <div class="coupon-page">
    <el-tabs v-model="active">
      <el-tab-pane label="领券中心" name="center">
        <el-row :gutter="16">
          <el-col v-for="c in centerList" :key="c.id" :span="8" style="margin-bottom: 16px">
            <el-card shadow="hover">
              <div class="coupon-amount">满{{ c.minPoint }}减{{ c.amount }}</div>
              <div class="coupon-meta">已领 {{ c.receivedCount }}/{{ c.publishCount }}</div>
              <el-button type="primary" @click="receive(c.id)">立即领取</el-button>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <el-tab-pane label="我的优惠券" name="my">
        <el-table :data="myList" border empty-text="暂无优惠券">
          <el-table-column prop="couponId" label="券ID" width="100" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">{{ row.useStatus === 1 ? '已使用' : '未使用' }}</template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="使用优惠券" name="use">
        <el-form :model="useForm" label-width="90px" style="max-width: 420px">
          <el-form-item label="券ID">
            <el-input v-model="useForm.couponId" />
          </el-form-item>
          <el-form-item label="订单ID">
            <el-input v-model="useForm.orderId" />
          </el-form-item>
          <el-button type="primary" @click="use">计算抵扣</el-button>
          <span v-if="discount !== null" class="discount">抵扣：¥{{ discount }}</span>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import couponApi from '@/api/coupon'

const active = ref('center')
const centerList = ref([])
const myList = ref([])
const useForm = ref({ couponId: '', orderId: '' })
const discount = ref(null)

async function loadCenter() {
  try {
    centerList.value = (await couponApi.center()) || []
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  }
}
async function loadMy() {
  try {
    myList.value = (await couponApi.my()) || []
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  }
}
async function receive(id) {
  try {
    await couponApi.receive(id)
    ElMessage.success('领取成功')
    loadCenter()
  } catch (e) {
    ElMessage.error(e.message || '领取失败')
  }
}
async function use() {
  if (!useForm.value.couponId || !useForm.value.orderId) {
    ElMessage.warning('请填写券ID与订单ID')
    return
  }
  try {
    const d = await couponApi.use(Number(useForm.value.couponId), Number(useForm.value.orderId))
    discount.value = d
    ElMessage.success('抵扣计算成功')
  } catch (e) {
    discount.value = null
    ElMessage.error(e.message || '使用失败')
  }
}

onMounted(() => {
  loadCenter()
  loadMy()
})
</script>

<style scoped>
.coupon-amount {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}
.coupon-meta {
  color: #999;
  margin: 8px 0 12px;
}
.discount {
  margin-left: 12px;
  color: #f56c6c;
  font-weight: bold;
}
</style>
