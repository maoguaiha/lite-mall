<template>
  <div class="login-page">
    <div class="login-container">
      <div class="logo">
        <h1>mall-lite</h1>
        <p>电商平台</p>
      </div>
      <el-form ref="loginForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="handleLogin">登录</el-button>
        </el-form-item>
        <div class="register-link">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useMemberStore } from '@/stores/member'
import { useRouter } from 'vue-router'

const router = useRouter()
const memberStore = useMemberStore()
const loginForm = ref(null)
const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function handleLogin() {
  loginForm.value.validate((valid) => {
    if (valid) {
      memberStore.login(form).then(() => {
        router.push('/')
      }).catch(err => {
        console.error(err)
      })
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2AB795 0%, #1a8a6f 100%);
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
  
  h1 {
    font-size: 32px;
    color: #2AB795;
    margin-bottom: 8px;
  }
  
  p {
    color: #999;
    font-size: 14px;
  }
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
  
  a {
    color: #2AB795;
    margin-left: 4px;
  }
}
</style>
