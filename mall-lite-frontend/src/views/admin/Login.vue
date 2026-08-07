<template>
  <div class="admin-login-page">
    <div class="login-container">
      <div class="logo">
        <h1>mall-lite</h1>
        <p>后台管理系统</p>
      </div>
      <el-form ref="loginForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="admin" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="admin123" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import adminRequest from '@/utils/adminRequest'
import { useAdminStore } from '@/stores/admin'

const router = useRouter()
const adminStore = useAdminStore()
const loginForm = ref(null)
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  loginForm.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await adminRequest.post('/admin/login', { ...form })
      adminStore.setToken(res.data, form.username)
      // 拉取角色用于在路由守卫中做权限判断
      try {
        const info = await adminRequest.get('/admin/info')
        adminStore.setRoles(info.data?.roles || [])
      } catch (e) {
        // 忽略：令牌无效时保持空角色
      }
      ElMessage.success('登录成功')
      router.push('/admin')
    } catch (e) {
      ElMessage.error(e.message || '登录失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a365d, #2c5282);
}
.login-container {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}
.logo {
  text-align: center;
  margin-bottom: 30px;
}
.logo h1 {
  font-size: 32px;
  color: #2ab795;
  margin-bottom: 8px;
}
.logo p {
  color: #999;
  font-size: 14px;
}
.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}
</style>
