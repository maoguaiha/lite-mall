<template>
  <div class="comment-page">
    <el-tabs v-model="active">
      <el-tab-pane label="我的评价" name="my">
        <el-table :data="myList" border v-loading="loading" empty-text="暂无评价">
          <el-table-column prop="productId" label="商品ID" width="100" />
          <el-table-column prop="star" label="评分" width="80" />
          <el-table-column prop="content" label="内容" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="提交评价" name="submit">
        <el-form :model="submitForm" label-width="80px" style="max-width: 480px">
          <el-form-item label="订单ID">
            <el-input v-model="submitForm.orderId" placeholder="已支付订单ID" />
          </el-form-item>
          <el-form-item label="商品ID">
            <el-input v-model="submitForm.productId" />
          </el-form-item>
          <el-form-item label="评分">
            <el-input-number v-model="submitForm.star" :min="1" :max="5" />
          </el-form-item>
          <el-form-item label="内容">
            <el-input v-model="submitForm.content" type="textarea" />
          </el-form-item>
          <el-button type="primary" @click="submit">提交评价</el-button>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import commentApi from '@/api/comment'

const active = ref('my')
const myList = ref([])
const loading = ref(false)
const submitForm = ref({ orderId: '', productId: '', star: 5, content: '' })

async function loadMy() {
  loading.value = true
  try {
    const res = await commentApi.my(1, 10)
    myList.value = res?.list || []
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}
async function submit() {
  if (!submitForm.value.orderId || !submitForm.value.productId) {
    ElMessage.warning('请填写订单ID与商品ID')
    return
  }
  try {
    await commentApi.submit({ ...submitForm.value })
    ElMessage.success('评价已提交')
    submitForm.value.content = ''
    active.value = 'my'
    loadMy()
  } catch (e) {
    ElMessage.error(e.message || '提交失败')
  }
}

onMounted(loadMy)
</script>
