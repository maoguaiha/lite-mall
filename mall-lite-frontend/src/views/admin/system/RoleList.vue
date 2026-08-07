<template>
  <div class="page">
    <div class="page-header">
      <h2>角色管理</h2>
      <el-button type="primary" @click="openCreate">新增角色</el-button>
    </div>
    <el-card shadow="never">
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名称" min-width="140" />
        <el-table-column prop="code" label="权限编码" min-width="140" />
        <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" @click="viewMenus(row)">查看菜单</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="title" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="角色名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="权限编码"><el-input v-model="form.code" placeholder="如 ROLE_ADMIN" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="menuVisible" title="角色拥有菜单" width="480px">
      <el-table :data="menus" border v-loading="menuLoading">
        <el-table-column prop="title" label="菜单名称" min-width="140" />
        <el-table-column prop="url" label="路径" min-width="160" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import adminRequest from '@/utils/adminRequest'

const list = ref([])
const loading = ref(false)

const visible = ref(false)
const title = ref('')
const form = reactive({ id: null, name: '', code: '', description: '', status: 1, sort: 0 })

const menuVisible = ref(false)
const menuLoading = ref(false)
const menus = ref([])

async function loadList() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/role/list', { params: { pageNum: 1, pageSize: 100 } })
    list.value = res.data?.list || []
  } finally { loading.value = false }
}
function openCreate() {
  title.value = '新增角色'
  Object.assign(form, { id: null, name: '', code: '', description: '', status: 1, sort: 0 })
  visible.value = true
}
function openEdit(row) {
  title.value = '编辑角色'
  Object.assign(form, {
    id: row.id, name: row.name, code: row.code, description: row.description,
    status: row.status ?? 1, sort: row.sort || 0
  })
  visible.value = true
}
async function submit() {
  if (form.id) await adminRequest.post('/admin/role/update', form)
  else await adminRequest.post('/admin/role/create', form)
  ElMessage.success('保存成功')
  visible.value = false
  loadList()
}
async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除该角色？关联的管理员与菜单关联将一并清理。', '提示', { type: 'warning' })
  await adminRequest.post('/admin/role/delete', null, { params: { id: row.id } })
  ElMessage.success('已删除')
  loadList()
}
async function viewMenus(row) {
  menuVisible.value = true
  menuLoading.value = true
  try {
    const res = await adminRequest.get('/admin/role/menus', { params: { roleId: row.id } })
    menus.value = res.data || []
  } finally { menuLoading.value = false }
}
onMounted(loadList)
</script>

<style scoped>
.page { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
