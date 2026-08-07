<template>
  <div class="app-container">
    <el-card shadow="never">
      <div class="filter-container">
        <el-input v-model="keyword" placeholder="活动名称" style="width: 200px" @keyup.enter="loadList" />
        <el-button type="primary" @click="loadList">查询</el-button>
        <el-button type="success" @click="openCreate">添加秒杀活动</el-button>
      </div>

      <el-table :data="flashList" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="活动名称" min-width="140" />
        <el-table-column label="开始时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="秒杀商品数" width="110" prop="flashCount" />
        <el-table-column label="活动金额" min-width="120">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewProducts(row)">商品</el-button>
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
      <el-form :model="form" label-width="90px">
        <el-form-item label="活动名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option :value="0" label="未开始" />
            <el-option :value="1" label="进行中" />
            <el-option :value="2" label="已结束" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="productDialog" title="秒杀商品" width="560px">
      <el-table :data="products" border>
        <el-table-column prop="productName" label="商品" min-width="140" show-overflow-tooltip />
        <el-table-column label="秒杀价" min-width="100">
          <template #default="{ row }">¥{{ row.seckillPrice }}</template>
        </el-table-column>
        <el-table-column prop="seckillStock" label="库存" width="90" />
        <el-table-column prop="seckillSales" label="已售" width="90" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import adminRequest from '@/utils/adminRequest'

const flashList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const keyword = ref('')

const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive({ id: null, name: '', startTime: '', endTime: '', status: 0 })

const productDialog = ref(false)
const products = ref([])

function statusText(s) {
  return ['未开始', '进行中', '已结束'][s] || '未知'
}
function statusType(s) {
  return ['info', 'success', 'warning'][s] || 'info'
}
function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

async function loadList() {
  const res = await adminRequest.get('/admin/seckill/list', {
    params: { pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value },
  })
  const page = res.data
  flashList.value = page.list || []
  total.value = page.total || 0
}

function openCreate() {
  dialogTitle.value = '添加秒杀活动'
  Object.assign(form, { id: null, name: '', startTime: '', endTime: '', status: 0 })
  dialogVisible.value = true
}
function openEdit(row) {
  dialogTitle.value = '编辑秒杀活动'
  Object.assign(form, {
    id: row.id,
    name: row.name,
    startTime: formatTime(row.startTime),
    endTime: formatTime(row.endTime),
    status: row.status,
  })
  dialogVisible.value = true
}
async function submit() {
  if (form.id) {
    await adminRequest.post('/admin/seckill/update', form)
  } else {
    await adminRequest.post('/admin/seckill/create', form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadList()
}
async function handleDelete(row) {
  await ElMessageBox.confirm('确认删除该秒杀活动？', '提示', { type: 'warning' })
  await adminRequest.post('/admin/seckill/delete', null, { params: { id: row.id } })
  ElMessage.success('已删除')
  loadList()
}
async function viewProducts(row) {
  const res = await adminRequest.get('/admin/seckill/products', { params: { sessionId: row.id } })
  products.value = res.data || []
  productDialog.value = true
}

loadList()
</script>

<style scoped>
.filter-container { display: flex; gap: 12px; margin-bottom: 16px; }
</style>
