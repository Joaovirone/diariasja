import { defineStore } from 'pinia'
import { ref } from 'vue'
import { usuarioService } from '../services/usuarioService'

export const useUsuarioStore = defineStore('usuario', () => {
  const usuario = ref(null)
  const isLoading = ref(false)
  const error = ref(null)

  const obterPerfil = async () => {
    isLoading.value = true
    error.value = null
    try {
      const response = await usuarioService.obterPerfil()
      usuario.value = response.data
      return usuario.value
    } catch (err) {
      error.value = err.response?.data || 'Erro ao obter perfil'
    } finally {
      isLoading.value = false
    }
  }

  const atualizar = async (dados) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await usuarioService.atualizar(dados)
      usuario.value = response.data
      return usuario.value
    } catch (err) {
      error.value = err.response?.data || 'Erro ao atualizar perfil'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const criar = async (dados) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await usuarioService.criarConta(dados)
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Erro ao criar conta'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  return {
    usuario,
    isLoading,
    error,
    obterPerfil,
    atualizar,
    criar
  }
})
