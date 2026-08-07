import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { login as loginApi, register as registerApi, getMemberInfo } from '@/api/member'

export const useMemberStore = defineStore('member', () => {
  const token = ref(localStorage.getItem('token') || '')
  const member = ref(null)
  const isLoggedIn = computed(() => !!token.value)

  function login(data) {
    return loginApi(data).then(res => {
      token.value = res.data
      localStorage.setItem('token', res.data)
      return res
    })
  }

  function register(data) {
    return registerApi(data)
  }

  function logout() {
    token.value = ''
    member.value = null
    localStorage.removeItem('token')
  }

  function fetchMemberInfo() {
    return getMemberInfo().then(res => {
      member.value = res.data
      return res
    })
  }

  return {
    token,
    member,
    isLoggedIn,
    login,
    register,
    logout,
    fetchMemberInfo
  }
})
