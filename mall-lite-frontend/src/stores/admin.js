import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('adminToken') || '')
  const username = ref(localStorage.getItem('adminUsername') || '')
  const roles = ref(JSON.parse(localStorage.getItem('adminRoles') || '[]'))

  function setToken(t, name) {
    token.value = t
    localStorage.setItem('adminToken', t)
    if (name) {
      username.value = name
      localStorage.setItem('adminUsername', name)
    }
  }

  function setRoles(r) {
    roles.value = Array.isArray(r) ? r : []
    localStorage.setItem('adminRoles', JSON.stringify(roles.value))
  }

  function logout() {
    token.value = ''
    username.value = ''
    roles.value = []
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminUsername')
    localStorage.removeItem('adminRoles')
  }

  return { token, username, roles, setToken, setRoles, logout }
})
