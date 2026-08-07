<template>
  <div class="coupon-list-page">
    <div class="page-header">
      <h2>优惠券管理</h2>
      <el-button type="primary" @click="openCreate">新建优惠券</el-button>
    </div>

    <el-table :data="list" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column label="面额/门槛" width="160">
        <template #default="{ row }">满{{ row.minPoint }}减{{ row.amount }}</template>
      </el-table-column>
      <el-table-column prop="publishCount" label="发行量" width="90" />
      <el-table-column prop="receivedCount" label="已领" width="80" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button type="text" @click="viewHistories(row.id)">发放记录</el-button>
          <el-button type="text" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新建优惠券">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="面额"><el-input-number v-model="form.amount" :min="0" /></el-form-item>
        <el-form-item label="使用门槛"><el-input-number v-model="form.minPoint" :min="0" /></el-form-item>
        <el-form-item label="每人限领"><el-input-number v-model="form.perLimit" :min="1" /></el-form-item>
        <el-form-item label="发行量"><el-input-number v-model="form.publishCount" :min="1" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="create">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="historyVisible" title="发放记录">
      <el-table :data="histories" border>
        <el-table-column prop="memberId" label="会员ID" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">{{ row.useStatus === 1 ? '已使用' : '未使用' }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import adminRequest from '@/utils/adminRequest'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const historyVisible = ref(false)
const histories = ref([])
const form = ref({ name: '', amount: 10, minPoint: 100, perLimit: 1, publishCount: 100 })

async function load() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/coupon/list')
    list.value = res.data || []
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}
function openCreate() {
  form.value = { name: '', amount: 10, minPoint: 100, perLimit: 1, publishCount: 100 }
  dialogVisible.value = true
}
async function create() {
  try {
    await adminRequest.post('/admin/coupon/create', { ...form.value })
    ElMessage.success('创建成功')
    dialogVisible.value = false
    load()
  } catch (e) {
    ElMessage.error(e.message || '创建失败')
  }
}
async function remove(id) {
  await ElMessageBox.confirm('确定删除该优惠券？')
  await adminRequest.post('/admin/coupon/delete', null, { params: { id } })
  ElMessage.success('已删除')
  load()
}
async function viewHistories(id) {
  try {
    const res = await adminRequest.get(`/admin/coupon/histories/${id}`)
    histories.value = res.data || []
    historyVisible.value = true
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  }
}
onMounted(load)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
</style>
