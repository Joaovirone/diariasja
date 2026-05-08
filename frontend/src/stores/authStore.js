import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '../services/authService'
import { usuarioService } from '../services/usuarioService'
import { getApiErrorMessage } from '../services/api'
import { getMockUserByCredentials, MOCK_TOKEN_PREFIX } from '../services/mockData'

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
      const mockUser = getMockUserByCredentials(email, senha)
      if (mockUser) {
        const { senha: _senha, ...usuarioMockado } = mockUser
        const mockToken = `${MOCK_TOKEN_PREFIX}-${usuarioMockado.tipo.toLowerCase()}`

        token.value = mockToken
        user.value = usuarioMockado
        authService.setToken(mockToken)
        localStorage.setItem('user', JSON.stringify(usuarioMockado))
        return true
      }

      const response = await authService.login(email, senha)
      token.value = response.data
      authService.setToken(response.data)
      const perfil = await usuarioService.obterPerfil()
      user.value = perfil.data
      localStorage.setItem('user', JSON.stringify(perfil.data))
      return true
    } catch (err) {
      error.value = getApiErrorMessage(err, 'Erro ao fazer login')
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
    const savedUser = localStorage.getItem('user')
    if (savedToken) {
      token.value = savedToken
    }
    if (savedUser) {
      try {
        user.value = JSON.parse(savedUser)
      } catch {
        localStorage.removeItem('user')
      }
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
