<template>
  <div class="product-form-page">
    <div class="page-header">
      <h2>编辑商品</h2>
    </div>
    <el-card shadow="never" v-loading="loading">
      <el-form :model="form" label-width="100px" style="max-width: 720px">
        <el-form-item label="商品名称" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subtitle" />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :value="c.id" :label="c.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="预警库存">
          <el-input-number v-model="form.lowStock" :min="0" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="是否上架">
          <el-switch v-model="form.publishStatus" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="新品">
          <el-switch v-model="form.newStatus" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="推荐">
          <el-switch v-model="form.recommendStatus" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="form.keywords" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.detail" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">保存</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import adminRequest from '@/utils/adminRequest'

const route = useRoute()
const router = useRouter()
const categories = ref([])
const loading = ref(false)
const form = reactive({
  id: null, name: '', subtitle: '', categoryId: null, price: 0, stock: 0,
  lowStock: 10, sort: 0, publishStatus: 1, newStatus: 0, recommendStatus: 0,
  keywords: '', detail: '',
})

async function loadCategories() {
  const res = await adminRequest.get('/admin/product/category/options')
  categories.value = res.data || []
}
async function loadProduct() {
  loading.value = true
  try {
    const res = await adminRequest.get('/admin/product/get/' + route.params.id)
    Object.assign(form, res.data || {})
  } finally {
    loading.value = false
  }
}
async function submit() {
  if (!form.name) {
    ElMessage.warning('请填写商品名称')
    return
  }
  await adminRequest.post('/admin/product/update', form)
  ElMessage.success('保存成功')
  router.push('/admin/product')
}
onMounted(async () => {
  await loadCategories()
  await loadProduct()
})
</script>

<style scoped>
.product-form-page { padding: 20px; }
.page-header { margin-bottom: 16px; }
</style>
