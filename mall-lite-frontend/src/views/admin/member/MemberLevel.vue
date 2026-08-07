<template>
  <div class="level-page">
    <div class="page-header">
      <h2>会员等级</h2>
      <el-button type="primary" @click="openCreate">新增等级</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="levels" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="等级名称" min-width="140" />
        <el-table-column prop="growthPoint" label="成长值" width="100" />
        <el-table-column label="默认" width="80">
          <template #default="{ row }">
            <el-tag :type="row.defaultStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.defaultStatus === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="commentIntegral" label="评论奖励积分" width="120" />
        <el-table-column prop="freeFreightPoint" label="免邮门槛" width="110" />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="等级名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="成长值">
          <el-input-number v-model="form.growthPoint" :min="0" />
        </el-form-item>
        <el-form-item label="免邮门槛">
          <el-input-number v-model="form.freeFreightPoint" :min="0" :precision="2" :step="1" />
        </el-form-item>
        <el-form-item label="评论奖励积分">
          <el-input-number v-model="form.commentIntegral" :min="0" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.defaultStatus" :active-value="1" :inactive-value="0" />
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

const levels = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive({
  id: null, name: '', growthPoint: 0, freeFreightPoint: 0,
  commentIntegral: 0, defaultStatus: 0,
})

async function loadList() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/member/level/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value },
    })
    levels.value = res.data?.list || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}
function openCreate() {
  dialogTitle.value = '新增等级'
  Object.assign(form, { id: null, name: '', growthPoint: 0, freeFreightPoint: 0, commentIntegral: 0, defaultStatus: 0 })
  dialogVisible.value = true
}
function openEdit(row) {
  dialogTitle.value = '编辑等级'
  Object.assign(form, {
    id: row.id, name: row.name, growthPoint: row.growthPoint || 0,
    freeFreightPoint: row.freeFreightPoint || 0, commentIntegral: row.commentIntegral || 0,
    defaultStatus: row.defaultStatus ?? 0,
  })
  dialogVisible.value = true
}
async function submit() {
  if (form.id) {
    await adminRequest.post('/admin/member/level/update', form)
  } else {
    await adminRequest.post('/admin/member/level/create', form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadList()
}
async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除该会员等级？', '提示', { type: 'warning' })
  await adminRequest.post('/admin/member/level/delete', null, { params: { id: row.id } })
  ElMessage.success('已删除')
  loadList()
}

onMounted(loadList)
</script>

<style scoped>
.level-page { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
</style>
