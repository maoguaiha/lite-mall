<template>
  <div class="page">
    <div class="page-header">
      <h2>订单设置</h2>
      <el-button type="primary" @click="save">保存设置</el-button>
    </div>
    <el-card shadow="never" v-loading="loading">
      <el-form :model="form" label-width="200px" style="max-width: 680px">
        <el-form-item label="秒杀订单超时关闭时间(分)">
          <el-input-number v-model="form.flashOrderOvertime" :min="0" />
        </el-form-item>
        <el-form-item label="正常订单超时时间(分)">
          <el-input-number v-model="form.normalOrderOvertime" :min="0" />
        </el-form-item>
        <el-form-item label="发货后自动确认收货(天)">
          <el-input-number v-model="form.confirmOvertime" :min="0" />
        </el-form-item>
        <el-form-item label="自动完成交易(天)">
          <el-input-number v-model="form.finishOvertime" :min="0" />
        </el-form-item>
        <el-form-item label="订单完成后自动好评(天)">
          <el-input-number v-model="form.commentOvertime" :min="0" />
        </el-form-item>
        <el-form-item label="会员等级">
          <el-input-number v-model="form.memberLevel" :min="0" />
        </el-form-item>
        <el-form-item label="自动好评">
          <el-switch v-model="form.autoComment" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import adminRequest from '@/utils/adminRequest'

const loading = ref(false)
const form = reactive({
  id: 1, flashOrderOvertime: 0, normalOrderOvertime: 0, confirmOvertime: 0,
  finishOvertime: 0, commentOvertime: 0, memberLevel: 0, autoComment: 0
})

async function load() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/order/setting/get')
    Object.assign(form, res.data || {})
  } finally { loading.value = false }
}
async function save() {
  await adminRequest.post('/admin/order/setting/update', form)
  ElMessage.success('设置已保存')
  load()
}
onMounted(load)
</script>

<style scoped>
.page { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
