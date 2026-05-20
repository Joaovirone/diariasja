import { defineStore } from 'pinia'
import { ref } from 'vue'
import { usuarioService } from '../services/usuarioService'

export const useUsuarioStore = defineStore('usuario', () => {
  const usuario = ref(null)
  const prestadores = ref([])
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

  const listarPrestadores = async (page = 0, size = 12) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await usuarioService.listarPrestadores(page, size)
      prestadores.value = response.data.content || []
      return prestadores.value
    } catch (err) {
      error.value = err.response?.data || 'Erro ao listar prestadores de serviço'
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
    prestadores,
    isLoading,
    error,
    obterPerfil,
    listarPrestadores,
    criar
  }
})
