<template>
  <div class="seckill-page">
    <h2>秒杀专区</h2>
    <el-row :gutter="16">
      <el-col v-for="p in list" :key="p.id" :span="8" style="margin-bottom: 16px">
        <el-card shadow="hover">
          <div class="sk-title">商品ID：{{ p.productId }}</div>
          <div class="sk-price">秒杀价：¥{{ p.seckillPrice }}</div>
          <div class="sk-stock">剩余库存：{{ p.stock }}</div>
          <el-button type="danger" :disabled="p.stock <= 0" @click="buy(p.id)">立即抢购</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import seckillApi from '@/api/seckill'

const list = ref([])

async function load() {
  try {
    list.value = (await seckillApi.list()) || []
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  }
}
async function buy(id) {
  try {
    const orderId = await seckillApi.buy(id)
    ElMessage.success('抢购成功，订单号：' + orderId)
    load()
  } catch (e) {
    ElMessage.error(e.message || '抢购失败')
  }
}

onMounted(load)
</script>

<style scoped>
.sk-title {
  font-weight: bold;
}
.sk-price {
  color: #f56c6c;
  font-size: 16px;
  margin: 8px 0;
}
.sk-stock {
  color: #999;
  margin-bottom: 12px;
}
</style>
