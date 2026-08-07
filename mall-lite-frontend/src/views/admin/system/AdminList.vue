<template>
  <div class="admin-page">
    <div class="page-header">
      <h2>管理员管理</h2>
      <el-button type="primary" @click="openCreate">新增管理员</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="admins" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="role" label="角色" width="110" />
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadList"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item :label="form.id ? '重置密码' : '密码'">
          <el-input v-model="form.password" type="password" placeholder="留空则不修改" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="form.role" style="width: 100%">
            <el-option value="admin" label="admin" />
            <el-option value="operator" label="operator" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import adminRequest from '@/utils/adminRequest'

const admins = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive({ id: null, username: '', password: '', nickname: '', role: 'admin' })

function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

async function loadList() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/admin/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value },
    })
    admins.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}
function openCreate() {
  dialogTitle.value = '新增管理员'
  Object.assign(form, { id: null, username: '', password: '', nickname: '', role: 'admin' })
  dialogVisible.value = true
}
function openEdit(row) {
  dialogTitle.value = '编辑管理员'
  Object.assign(form, { id: row.id, username: row.username, password: '', nickname: row.nickname, role: row.role })
  dialogVisible.value = true
}
async function submit() {
  if (form.id) {
    await adminRequest.post('/admin/admin/update', form)
  } else {
    await adminRequest.post('/admin/admin/create', form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadList()
}
async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除该管理员？', '提示', { type: 'warning' })
  await adminRequest.post('/admin/admin/delete', null, { params: { id: row.id } })
  ElMessage.success('已删除')
  loadList()
}

onMounted(loadList)
</script>

<style scoped>
.admin-page { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
