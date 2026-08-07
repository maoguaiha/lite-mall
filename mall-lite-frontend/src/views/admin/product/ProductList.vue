<template>
  <div class="product-list-page">
    <div class="page-header">
      <h2>商品列表</h2>
      <el-button type="primary" @click="goToAdd">添加商品</el-button>
    </div>

    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <el-table :data="products" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="商品名称" />
        <el-table-column prop="price" label="价格" width="110">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-switch :model-value="row.publishStatus === 1" @change="(v) => handlePublish(row, v)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="text" @click="goToEdit(row.id)">编辑</el-button>
            <el-button type="text" @click="handleDelete(row.id)">删除</el-button>
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import adminRequest from '@/utils/adminRequest'

const router = useRouter()
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const searchForm = reactive({ keyword: '' })
const products = ref([])

async function loadProducts() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/product/list', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value, keyword: searchForm.keyword }
    })
    products.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    ElMessage.error(e.message || '加载失败')
  } finally {
    loading.value = false
  }
}

async function handlePublish(row, val) {
  try {
    await adminRequest.post('/admin/product/publish', null, {
      params: { id: row.id, publishStatus: val ? 1 : 0 }
    })
    row.publishStatus = val ? 1 : 0
    ElMessage.success('更新成功')
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
    loadProducts()
  }
}

async function handleDelete(id) {
  await ElMessageBox.confirm('确定删除该商品？')
  await adminRequest.post('/admin/product/delete', null, { params: { id } })
  ElMessage.success('已删除')
  loadProducts()
}

function handleSearch() {
  pageNum.value = 1
  loadProducts()
}
function handleReset() {
  searchForm.keyword = ''
  handleSearch()
}
function handleCurrentChange(p) {
  pageNum.value = p
  loadProducts()
}
function goToAdd() {
  router.push('/admin/product/add')
}
function goToEdit(id) {
  router.push(`/admin/product/edit/${id}`)
}

onMounted(loadProducts)
</script>

<style scoped>
.product-list-page {
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
