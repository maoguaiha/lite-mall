<template>
  <div class="address-page">
    <div class="page-header">
      <h2>收货地址</h2>
      <el-button type="primary" @click="openCreate">新增地址</el-button>
    </div>

    <el-table :data="list" border v-loading="loading">
      <el-table-column prop="receiverName" label="收货人" width="120" />
      <el-table-column prop="phone" label="电话" width="140" />
      <el-table-column label="地址">
        <template #default="{ row }">
          {{ row.province }}{{ row.city }}{{ row.region }}{{ row.detailAddress }}
        </template>
      </el-table-column>
      <el-table-column label="默认" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.isDefault === 1" type="success">默认</el-tag>
          <el-button v-else type="text" @click="setDefault(row.id)">设为默认</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button type="text" @click="openEdit(row)">编辑</el-button>
          <el-button type="text" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑地址' : '新增地址'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="收货人">
          <el-input v-model="form.receiverName" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="省">
          <el-input v-model="form.province" />
        </el-form-item>
        <el-form-item label="市">
          <el-input v-model="form.city" />
        </el-form-item>
        <el-form-item label="区">
          <el-input v-model="form.region" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="form.detailAddress" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import addressApi from '@/api/address'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({})

async function load() {
  loading.value = true
  try {
    list.value = (await addressApi.list()) || []
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function openCreate() {
  form.value = {}
  dialogVisible.value = true
}
function openEdit(row) {
  form.value = { ...row }
  dialogVisible.value = true
}
async function save() {
  if (form.value.id) await addressApi.update(form.value)
  else await addressApi.create(form.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}
async function setDefault(id) {
  await addressApi.setDefault(id)
  ElMessage.success('已设为默认')
  load()
}
async function remove(id) {
  await ElMessageBox.confirm('确定删除该地址？')
  await addressApi.remove(id)
  ElMessage.success('已删除')
  load()
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
