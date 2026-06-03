import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import apiClient from '@/api/client.js'

function decodeToken(token) {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch {
    return null
  }
}

function parseUserFromToken(token) {
  const decoded = decodeToken(token)
  if (!decoded) return null
  return {
    email: decoded.sub,
    role: decoded.roles ? decoded.roles.replace('ROLE_', '') : null,
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(token.value ? parseUserFromToken(token.value) : null)
  const isLoading = ref(false)
  const error = ref(null)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isManager = computed(() => user.value?.role === 'ORGANIZATION_MANAGER')
  const isDispatcher = computed(() => user.value?.role === 'DISPATCHER')
  const canManageWaybills = computed(() => isDispatcher.value || isManager.value)
  const canManageDirectory = computed(() => isManager.value || isAdmin.value)

  async function login(credentials) {
    isLoading.value = true
    error.value = null
    try {
      const response = await apiClient.post('/auth/login', credentials)
      token.value = response.data.token
      user.value = parseUserFromToken(token.value)
      localStorage.setItem('token', token.value)
    } catch (err) {
      error.value = err.response?.data?.message || 'Ошибка входа'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    user,
    isLoading,
    error,
    isAuthenticated,
    isAdmin,
    isManager,
    isDispatcher,
    canManageWaybills,
    canManageDirectory,
    login,
    logout,
  }
})
