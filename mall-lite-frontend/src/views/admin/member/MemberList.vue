<template>
  <div class="member-list-page">
    <div class="page-header">
      <h2>会员列表</h2>
    </div>

    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.keyword" placeholder="请输入用户名/昵称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="members" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="(v) => handleStatusChange(row, v)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link @click="handleView(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import adminRequest from '@/utils/adminRequest'

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const searchForm = reactive({ keyword: '' })
const members = ref([])

async function loadMembers() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/member/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value, keyword: searchForm.keyword }
    })
    members.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

async function handleStatusChange(row, val) {
  try {
    await adminRequest.post('/admin/member/status', null, {
      params: { id: row.id, status: val ? 1 : 0 }
    })
    row.status = val ? 1 : 0
    ElMessage.success('更新成功')
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
    loadMembers()
  }
}

function handleView(row) {
  ElMessage.info('会员：' + (row.nickname || row.username))
}
function handleSearch() {
  pageNum.value = 1
  loadMembers()
}
function handleReset() {
  searchForm.keyword = ''
  handleSearch()
}
function handleCurrentChange(page) {
  pageNum.value = page
  loadMembers()
}

onMounted(loadMembers)
</script>

<style scoped>
.member-list-page {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  h2 {
    font-size: 20px;
    font-weight: bold;
    color: #333;
  }
}
.search-form {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}
.table-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
