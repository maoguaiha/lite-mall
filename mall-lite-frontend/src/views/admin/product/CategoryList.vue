<template>
  <div class="category-page">
    <div class="page-header">
      <h2>商品分类</h2>
      <el-button type="primary" @click="openCreate">新增分类</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="categories" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="140" />
        <el-table-column label="上级分类" min-width="140">
          <template #default="{ row }">{{ parentName(row.parentId) }}</template>
        </el-table-column>
        <el-table-column label="层级" width="90">
          <template #default="{ row }">{{ row.level === 1 ? '一级' : '二级' }}</template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="显示" width="90">
          <template #default="{ row }">
            <el-switch :model-value="row.showStatus === 1" @change="(v) => toggleShow(row, v)" />
          </template>
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
        <el-form-item label="分类名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="上级分类">
          <el-select v-model="form.parentId" placeholder="一级分类" style="width: 100%">
            <el-option :value="0" label="一级分类" />
            <el-option v-for="o in parentOptions" :key="o.id" :value="o.id" :label="o.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="是否显示">
          <el-switch v-model="form.showStatus" :active-value="1" :inactive-value="0" />
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

const categories = ref([])
const parentOptions = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive({ id: null, name: '', parentId: 0, sort: 0, showStatus: 1 })

function parentName(id) {
  if (!id || id === 0) return '无'
  const p = parentOptions.value.find(o => o.id === id)
  return p ? p.name : '无'
}

async function loadOptions() {
  const res = await adminRequest.get('/admin/product/category/options')
  parentOptions.value = res.data || []
}
async function loadList() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/product/category/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value },
    })
    categories.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function openCreate() {
  dialogTitle.value = '新增分类'
  Object.assign(form, { id: null, name: '', parentId: 0, sort: 0, showStatus: 1 })
  dialogVisible.value = true
}
function openEdit(row) {
  dialogTitle.value = '编辑分类'
  Object.assign(form, {
    id: row.id, name: row.name, parentId: row.parentId || 0,
    sort: row.sort || 0, showStatus: row.showStatus ?? 1,
  })
  dialogVisible.value = true
}
async function submit() {
  if (form.id) {
    await adminRequest.post('/admin/product/category/update', form)
  } else {
    await adminRequest.post('/admin/product/category/create', form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadList()
}
async function toggleShow(row, val) {
  await adminRequest.post('/admin/product/category/update', { id: row.id, showStatus: val ? 1 : 0 })
  row.showStatus = val ? 1 : 0
}
async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除该分类？', '提示', { type: 'warning' })
  await adminRequest.post('/admin/product/category/delete', null, { params: { id: row.id } })
  ElMessage.success('已删除')
  loadList()
}

onMounted(async () => {
  await loadOptions()
  loadList()
})
</script>

<style scoped>
.category-page { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
