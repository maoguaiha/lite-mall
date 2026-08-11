<template>
  <div class="comment-list-page">
    <div class="page-header">
      <h2>评价管理</h2>
    </div>
    <el-table :data="list" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="productId" label="商品ID" width="100" />
      <el-table-column prop="star" label="评分" width="80" />
      <el-table-column prop="content" label="内容" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 2" type="success">已通过</el-tag>
          <el-tag v-else-if="row.status === 3" type="info">已拒绝</el-tag>
          <el-tag v-else type="warning">待审核</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link :disabled="row.status === 2" @click="audit(row.id, 2)">
            通过
          </el-button>
          <el-button link :disabled="row.status === 3" @click="audit(row.id, 3)">
            拒绝
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination">
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import adminRequest from '@/utils/adminRequest'

const list = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

async function load() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/comment/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value }
    })
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}
async function audit(id, status) {
  try {
    await adminRequest.post(`/admin/comment/audit/${id}`, null, { params: { status } })
    ElMessage.success(status === 2 ? '已通过' : '已拒绝')
    load()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}
function handleCurrentChange(page) {
  pageNum.value = page
  load()
}
onMounted(load)
</script>

<style scoped>
.page-header {
  margin-bottom: 16px;
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
