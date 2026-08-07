<template>
  <div class="page">
    <div class="page-header">
      <h2>首页广告</h2>
      <el-button type="primary" @click="openCreate">新增广告</el-button>
    </div>
    <el-card shadow="never">
      <el-form inline @submit.prevent>
        <el-form-item label="名称">
          <el-input v-model="keyword" placeholder="搜索广告名称" clearable @clear="loadList" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图片" width="120">
          <template #default="{ row }">
            <img v-if="row.pic" :src="row.pic" style="width:100px;height:50px;object-fit:cover" @error="e => e.target.src = '/images/placeholder.svg'" />
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" min-width="140" />
        <el-table-column prop="url" label="跳转链接" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="note" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" min-width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pageNum" :page-size="pageSize" :total="total"
        layout="total, prev, pager, next" @current-change="loadList"
        style="margin-top:16px;justify-content:flex-end" />
    </el-card>

    <el-dialog v-model="visible" :title="title" width="560px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="图片URL"><el-input v-model="form.pic" placeholder="/images/banner.png" /></el-form-item>
        <el-form-item label="跳转链接"><el-input v-model="form.url" placeholder="/promotion/flash" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="类型"><el-input-number v-model="form.type" :min="0" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.note" type="textarea" :rows="2" /></el-form-item>
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
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const keyword = ref('')

const visible = ref(false)
const title = ref('')
const form = reactive({ id: null, name: '', pic: '', url: '', status: 1, type: 0, note: '' })

async function loadList() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/advertise/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value || undefined }
    })
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}
function openCreate() {
  title.value = '新增广告'
  Object.assign(form, { id: null, name: '', pic: '', url: '', status: 1, type: 0, note: '' })
  visible.value = true
}
function openEdit(row) {
  title.value = '编辑广告'
  Object.assign(form, {
    id: row.id, name: row.name, pic: row.pic, url: row.url,
    status: row.status ?? 0, type: row.type ?? 0, note: row.note
  })
  visible.value = true
}
async function submit() {
  if (form.id) await adminRequest.post('/admin/advertise/update', form)
  else await adminRequest.post('/admin/advertise/create', form)
  ElMessage.success('保存成功')
  visible.value = false
  loadList()
}
async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除该广告？', '提示', { type: 'warning' })
  await adminRequest.post('/admin/advertise/delete', null, { params: { id: row.id } })
  ElMessage.success('已删除')
  loadList()
}
onMounted(loadList)
</script>

<style scoped>
.page { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
