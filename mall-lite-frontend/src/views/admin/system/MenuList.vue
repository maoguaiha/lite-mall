<template>
  <div class="page">
    <div class="page-header">
      <h2>菜单管理</h2>
      <el-button type="primary" @click="openCreate">新增菜单</el-button>
    </div>
    <el-card shadow="never">
      <el-table :data="list" border stripe v-loading="loading" row-key="id" default-expand-all>
        <el-table-column prop="title" label="菜单名称" min-width="160" />
        <el-table-column prop="name" label="标识" min-width="120" />
        <el-table-column prop="url" label="路径" min-width="160" />
        <el-table-column prop="icon" label="图标" width="100" />
        <el-table-column label="类型" width="90">
          <template #default="{ row }">
            <el-tag :type="row.type === 0 ? 'primary' : 'warning'" size="small">
              {{ row.type === 0 ? '目录' : '菜单' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="操作" min-width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" :title="title" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="菜单名称"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="标识"><el-input v-model="form.name" placeholder="如 pms" /></el-form-item>
        <el-form-item label="路径"><el-input v-model="form.url" placeholder="/pms" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" /></el-form-item>
        <el-form-item label="父级ID"><el-input-number v-model="form.parentId" :min="0" /></el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.type">
            <el-radio :value="0">目录</el-radio>
            <el-radio :value="1">菜单</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible=false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
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
const form = reactive({ id: null, title: '', name: '', url: '', icon: '', parentId: 0, type: 0, sort: 0 })

async function loadList() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/menu/list')
    list.value = buildTree(res.data || [])
  } finally { loading.value = false }
}
function buildTree(rows) {
  const map = new Map()
  rows.forEach((r) => { map.set(r.id, { ...r, children: [] }) })
  const tree = []
  rows.forEach((r) => {
    const node = map.get(r.id)
    if (r.parentId && map.has(r.parentId)) map.get(r.parentId).children.push(node)
    else tree.push(node)
  })
  return tree
}
function openCreate() {
  title.value = '新增菜单'
  Object.assign(form, { id: null, title: '', name: '', url: '', icon: '', parentId: 0, type: 0, sort: 0 })
  visible.value = true
}
function openEdit(row) {
  title.value = '编辑菜单'
  Object.assign(form, {
    id: row.id, title: row.title, name: row.name, url: row.url, icon: row.icon,
    parentId: row.parentId || 0, type: row.type ?? 0, sort: row.sort || 0
  })
  visible.value = true
}
async function submit() {
  if (form.id) await adminRequest.post('/admin/menu/update', form)
  else await adminRequest.post('/admin/menu/create', form)
  ElMessage.success('保存成功')
  visible.value = false
  loadList()
}
async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除该菜单？关联的角色菜单关联将一并清理。', '提示', { type: 'warning' })
  await adminRequest.post('/admin/menu/delete', null, { params: { id: row.id } })
  ElMessage.success('已删除')
  loadList()
}
onMounted(loadList)
</script>

<style scoped>
.page { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
