import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '../services/authService'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(null)
  const user = ref(null)
  const isLoading = ref(false)
  const error = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  const login = async (email, senha) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await authService.login(email, senha)
      token.value = response.data
      authService.setToken(response.data)
      return true
    } catch (err) {
      error.value = err.response?.data || 'Erro ao fazer login'
      return false
    } finally {
      isLoading.value = false
    }
  }

  const logout = () => {
    token.value = null
    user.value = null
    authService.logout()
  }

  const loadToken = () => {
    const savedToken = authService.getToken()
    if (savedToken) {
      token.value = savedToken
    }
  }

  return {
    token,
    user,
    isLoading,
    error,
    isLoggedIn,
    login,
    logout,
    loadToken
  }
})
