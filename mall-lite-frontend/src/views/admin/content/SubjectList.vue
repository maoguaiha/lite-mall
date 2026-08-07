<template>
  <div class="page">
    <div class="page-header">
      <h2>专题管理</h2>
      <el-button type="primary" @click="openCreate">新增专题</el-button>
    </div>
    <el-card shadow="never">
      <el-form inline @submit.prevent>
        <el-form-item label="标题">
          <el-input v-model="keyword" placeholder="搜索专题标题" clearable @clear="loadList" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="封面" width="120">
          <template #default="{ row }">
            <img v-if="row.pic" :src="row.pic" style="width:100px;height:50px;object-fit:cover" @error="e => e.target.src = '/images/placeholder.svg'" />
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="140" />
        <el-table-column prop="productCount" label="商品数" width="90" />
        <el-table-column label="推荐" width="80">
          <template #default="{ row }">
            <el-tag :type="row.recommendStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.recommendStatus === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="显示" width="80">
          <template #default="{ row }">
            <el-tag :type="row.showStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.showStatus === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
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
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="封面URL"><el-input v-model="form.pic" placeholder="/images/subject.png" /></el-form-item>
        <el-form-item label="分类ID"><el-input-number v-model="form.categoryId" :min="0" /></el-form-item>
        <el-form-item label="商品数"><el-input-number v-model="form.productCount" :min="0" /></el-form-item>
        <el-form-item label="推荐">
          <el-switch v-model="form.recommendStatus" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="显示">
          <el-switch v-model="form.showStatus" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
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
const form = reactive({
  id: null, title: '', pic: '', categoryId: 0, productCount: 0,
  recommendStatus: 1, showStatus: 1, sort: 0, description: ''
})

async function loadList() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/subject/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value || undefined }
    })
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}
function openCreate() {
  title.value = '新增专题'
  Object.assign(form, {
    id: null, title: '', pic: '', categoryId: 0, productCount: 0,
    recommendStatus: 1, showStatus: 1, sort: 0, description: ''
  })
  visible.value = true
}
function openEdit(row) {
  title.value = '编辑专题'
  Object.assign(form, {
    id: row.id, title: row.title, pic: row.pic, categoryId: row.categoryId || 0,
    productCount: row.productCount || 0, recommendStatus: row.recommendStatus ?? 0,
    showStatus: row.showStatus ?? 0, sort: row.sort || 0, description: row.description
  })
  visible.value = true
}
async function submit() {
  if (form.id) await adminRequest.post('/admin/subject/update', form)
  else await adminRequest.post('/admin/subject/create', form)
  ElMessage.success('保存成功')
  visible.value = false
  loadList()
}
async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除该专题？', '提示', { type: 'warning' })
  await adminRequest.post('/admin/subject/delete', null, { params: { id: row.id } })
  ElMessage.success('已删除')
  loadList()
}
onMounted(loadList)
</script>

<style scoped>
.page { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
